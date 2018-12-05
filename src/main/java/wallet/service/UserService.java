package wallet.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wallet.dto.Credentials;
import wallet.dto.GenericResponse;
import wallet.entity.User;
import wallet.entity.UserBalance;
import wallet.repository.UserRepository;

@Service
public class UserService {

	private static String EXISTING_USERNAME_MESSAGE = "Username already exists. Please try a different one.";
	private static String NON_EXISTENT_USERNAME = "Username does not exist.";
	private static String INVALID_CREDENTIALS = "Wrong username or password. Please try again.";
	private static String NO_ADDRESS = "User does not have an address yet. Please create one.";

	UserRepository userRepository;
	RpcService rpcService;

	@Value("${active.currencies}")
	String[] activeCurrencies;

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
		} else {
			User user = new User();
			user.setUsername(credentials.getUsername());
			user.setPassword(credentials.getPassword());

			List<UserBalance> balances = new ArrayList<>();
			for (String currency : activeCurrencies) {
				String[] strings = currency.split("@");
				String currencyName = strings[0];
				String currencySymbol = strings[1];
				balances.add(new UserBalance(currencyName, currencySymbol, BigInteger.valueOf(0)));
			}
			// Map<String, BigInteger> balances = new HashMap<>();
			// for(String currency: activeCurrencies) {
			// balances.put(currency, BigInteger.valueOf(0));
			// }
			user.setBalances(balances);

			User result = userRepository.save(user);
			if (result.getUsername() == user.getUsername() && result.getPassword() == user.getPassword()) {
				response.setSuccessful(true);
			}
		}
		return response;
	}

	public GenericResponse getUserBalance(String username) {
		Optional<User> user = userRepository.findByUsername(username);

		if (user.isPresent()) {
			updateUserBalances(user.get());
			return new GenericResponse(true, user.get().getBalances());
		} else {
			return new GenericResponse(false, NON_EXISTENT_USERNAME);
		}
	}

	@Transactional
	private boolean updateUserBalances(User user) {
		try {
			// Map<String, BigInteger> map = new HashMap<>(user.getBalances());
			// map.putAll(rpcService.getCurrentBalances(user.getAddress()));
			// user.setBalances(map);

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

		System.out.println(
				"Existence of user with username " + username + ": " + userRepository.existsByUsername(username));
		return userRepository.existsByUsername(username);
	}

	public GenericResponse isValidCredentials(Credentials credentials) {
		if (!usernameExists(credentials.getUsername())) {
			return new GenericResponse(false, NON_EXISTENT_USERNAME);
		}

		User user = userRepository.findByUsername(credentials.getUsername()).get();
		if (user.getPassword().equals(credentials.getPassword())) {
			return new GenericResponse(true, null);
		} else {
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

		if (addressOfUser.isPresent()) {
			return new GenericResponse(false, NO_ADDRESS); // if user doesn't have an address yet
		}

		String txId = null;
		txId = rpcService.transfer(addressOfUser.get(), destinationAddress, currencySymbol, amount);
		return new GenericResponse(true, txId);
	}

	private static Optional<String> getOptionalAddressOfUser(User user) {
		return Optional.ofNullable(user.getAddress());
	}
}