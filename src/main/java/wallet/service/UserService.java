package wallet.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wallet.exception.RpcException;
import wallet.pojo.Credentials;
import wallet.pojo.GenericResponse;
import wallet.pojo.TransactionRequest;
import wallet.pojo.User;
import wallet.pojo.UserBalance;
import wallet.repository.UserRepository;
import wallet.scheduler.DepositChecker;

@Service
public class UserService {

	private static String EXISTING_USERNAME_MESSAGE = "Username already exists. Please try a different one.";
	private static String NON_EXISTENT_USERNAME = "Username does not exist.";
	private static String INVALID_CREDENTIALS = "Wrong username or password. Please try again.";
	private static String NO_ADDRESS = "User does not have an address yet. Please create one.";
	private static String WRONG_ADDRESS_FORMAT = "Input format of recipient's address is wrong. It should start with \"0x\" and be 42 characters long total. ";

	UserRepository userRepository;
	RpcService rpcService;

	@Value("${active.currencies}")
	String[] activeCurrencies;

	Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	public UserService(UserRepository userRepository, RpcService rpcService) {
		this.userRepository = userRepository;
		this.rpcService = rpcService;
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	public GenericResponse createUser(Credentials credentials) {
		GenericResponse response = new GenericResponse();
		if (usernameExists(credentials.getUsername())) {
			response.setSuccessful(false);
			response.setData((EXISTING_USERNAME_MESSAGE));
			logger.warn("Failed creating new user");
		} else {
			User user = new User();
			user.setUsername(credentials.getUsername());
			user.setPassword(credentials.getPassword());

			List<UserBalance> balances = new ArrayList<>();
			for (String currency : activeCurrencies) {
				String[] strings = currency.split("@");
				String currencyName = strings[0];
				String currencySymbol = strings[1];
				balances.add(new UserBalance(currencyName, currencySymbol, BigDecimal.valueOf(0)));
			}
			user.setBalances(balances);
			try {
				userRepository.save(user);
			} catch(Exception e) {
				response.setSuccessful(false);
				response.setData(e);
				return response;
			}
			response.setSuccessful(true);
			logActivity("Created new user with username", user.getUsername());
		}
		return response;
	}

	public GenericResponse getUserBalance(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		if (user.isPresent()) {
			updateUserBalances(user.get());
			logActivity("Returned balances of user", username);
			return new GenericResponse(true, user.get().getBalances());
		} else {
			logActivity("User with username", username, "does not exist");
			return new GenericResponse(false, NON_EXISTENT_USERNAME);
		}
	}

	@Transactional
	public boolean updateUserBalances(User user) {
		try {
			for (UserBalance balance : user.getBalances()) {
				balance.setBalance(rpcService.getBalance(balance.getCurrencySymbol(), user.getAddress()));
			}
			userRepository.save(user);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public GenericResponse getUser(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		if (user.isPresent()) {
			return new GenericResponse(true, user.get());
		} else {
			return new GenericResponse(false, NON_EXISTENT_USERNAME);
		}
	}

	public GenericResponse isExistingUsername(String username) {
		return new GenericResponse(usernameExists(username), null);
	}

	public boolean usernameExists(String username) {
		return userRepository.existsByUsername(username);
	}

	public GenericResponse isValidCredentials(Credentials credentials) {
		if (!usernameExists(credentials.getUsername())) {
			logger.debug("Credentials check failed");
			return new GenericResponse(false, NON_EXISTENT_USERNAME);
		}

		User user = userRepository.findByUsername(credentials.getUsername()).get();
		if (user.getPassword().equals(credentials.getPassword())) {
			logger.debug("Valid credentials");
			return new GenericResponse(true, null);
		} else {
			logger.debug("Credentials check failed");
			return new GenericResponse(false, INVALID_CREDENTIALS);
		}
	}

	public GenericResponse createUserAddress(String username) {
		if (!usernameExists(username)) {
			return new GenericResponse(false, NON_EXISTENT_USERNAME);
		}

		// check if already has an address
		User user = userRepository.findByUsername(username).get();

		Optional<String> addressOfUser = getOptionalAddressOfUser(user);
		if (addressOfUser.isPresent()) {
			logActivity("Returned existing ethereum address for user", username);
			return new GenericResponse(true, addressOfUser);
		}

		// if not, create new address
		String newAddress = null;
		try {
			newAddress = rpcService.createAddress();
		} catch (ParseException e) {
			e.printStackTrace();
			return new GenericResponse(false, e);
		}

		user.setAddress(newAddress);
		userRepository.save(user);
		logActivity("Created ethereum address for user", username);
		return new GenericResponse(true, newAddress);
	}

	public GenericResponse getUserAddress(String username) {

		if (!usernameExists(username)) {
			return new GenericResponse(false, NON_EXISTENT_USERNAME);
		}

		User user = userRepository.findByUsername(username).get();
		Optional<String> addressOfUser = getOptionalAddressOfUser(user);
		if (addressOfUser.isPresent()) {
			return new GenericResponse(true, user.getAddress());
		} else {
			return new GenericResponse(false, NO_ADDRESS);
		}
	}

	public GenericResponse transfer(String username, String destinationAddress, String currencySymbol, String amount) {
		if (!usernameExists(username)) {
			return new GenericResponse(false, NON_EXISTENT_USERNAME);
		}

		User user = userRepository.findByUsername(username).get();
		Optional<String> addressOfUser = getOptionalAddressOfUser(user);

		if (!addressOfUser.isPresent()) {
			return new GenericResponse(false, NO_ADDRESS); // if user doesn't have an address yet
		}

		String txId = null;
		try {
			txId = rpcService.transfer(addressOfUser.get(), destinationAddress, currencySymbol, amount);
		} catch(Exception e) {
			logger.debug("Transaction requested by user \"" + username + "\" failed");
			e.printStackTrace();
			return new GenericResponse(false, e.getMessage());
		}
		logger.debug("Successfully transferred " + amount + " " + currencySymbol + "from \"" + username + "\" to \"" + destinationAddress + "\"");
		return new GenericResponse(true, txId);
	}

	public GenericResponse transfer(TransactionRequest details) {
		if(!details.getDestinationAddress().startsWith("0x") || details.getDestinationAddress().length() != 42) {
			return new GenericResponse(false, WRONG_ADDRESS_FORMAT);
		}
		String usernameOfSender = details.getUsername();
		if (!usernameExists(usernameOfSender)) {
			return new GenericResponse(false, NON_EXISTENT_USERNAME);
		}

		User user = userRepository.findByUsername(usernameOfSender).get();
		Optional<String> addressOfUser = getOptionalAddressOfUser(user);

		if (!addressOfUser.isPresent()) {
			return new GenericResponse(false, NO_ADDRESS); // if user doesn't have an address yet
		}

		String txId = null;
		try {
			txId = rpcService.transfer(addressOfUser.get(), details.getDestinationAddress(),
					details.getCurrencySymbol(), details.getAmount());
		} catch (RpcException e) {
			logger.warn("Transaction requested by user \"" + addressOfUser.get() + "\" failed");
			e.printStackTrace();
			return new GenericResponse(false, e.getMessage());
		} catch (Exception e) {
			logger.warn("Transaction requested by user \"" + addressOfUser.get() + "\" failed");
			e.printStackTrace();
			return new GenericResponse(false, e.getMessage());
		}
		logger.debug("[Transferred] " + details.getAmount() + " " + details.getCurrencySymbol() + "from \"" + addressOfUser.get() + "\" to \"" + details.getDestinationAddress() + "\"");
		return new GenericResponse(true, txId);
	}

	private static Optional<String> getOptionalAddressOfUser(User user) {
		return Optional.ofNullable(user.getAddress());
	}
	
	private void logActivity(String message, String escapedMessage) {
		logger.debug(message + "\"" + escapedMessage + "\"");
	}
	
	private void logActivity(String beforeEscape, String escapedMessage, String afterEscape) {
		logger.debug(beforeEscape + "\"" + escapedMessage + "\"" + afterEscape);
	}
}