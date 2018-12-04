package wallet.service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import wallet.dto.Credentials;
import wallet.dto.GenericResponse;
import wallet.entity.User;
import wallet.repository.UserRepository;

@Service
public class UserService {

	private static String EXISTING_USERNAME_MESSAGE = "Username already exists. Please try a different one.";
	private static String NON_EXISTANT_USERNAME = "Username does not exist.";
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
			
			Map<String, BigInteger> balances = new HashMap<>();
			for(String currency: activeCurrencies) {
				balances.put(currency, BigInteger.valueOf(0));
			}
			user.setBalances(balances);
			
			User result = userRepository.save(user);
			if (result.getUsername() == user.getUsername() && result.getPassword() == user.getPassword()) {
				response.setSuccessful(true);
			}
		}
		return response;
	}

	public GenericResponse getBalances(String username, String currencySymbol) {
		Optional<User> user = userRepository.findByUsername(username);

		if (user.isPresent()) {
			updateBalances(user.get());
			return new GenericResponse(true, user.get().getBalances());
		} else {
			return new GenericResponse(false, NON_EXISTANT_USERNAME);
		}
	}
	
	public void updateBalances(User user) {
	}

	public GenericResponse getUser(String username) {
		
		Optional<User> user = userRepository.findByUsername(username);
		
		if(user.isPresent()) {
			return new GenericResponse(true, user.get());
		} else {
			return new GenericResponse(false, NON_EXISTANT_USERNAME);
		}		
	}

	public GenericResponse isExistingUsername(String username) {
		return new GenericResponse(usernameExists(username), null);
	}

	public boolean usernameExists(String username) {
		
		System.out.println("Existence of user with username " + username + ": " + userRepository.existsByUsername(username));
		return userRepository.existsByUsername(username);
	}

	public GenericResponse isValidCredentials(Credentials credentials) {
		boolean isValidCredentials = userRepository.existsByPassword(credentials.getPassword())
				&& usernameExists(credentials.getUsername());

		String details = null;
		if (!isValidCredentials) {
			details = INVALID_CREDENTIALS;
		}
		return new GenericResponse(isValidCredentials, details);
	}

	public GenericResponse createAddressForUser(String username) {
		if (!usernameExists(username)) {
			return new GenericResponse(false, NON_EXISTANT_USERNAME);
		}

		// check if already has an address
		User user = userRepository.findByUsername(username).get();
		
		Optional<String> addressOfUser = getOptionalAddressOfUser(user);
		if(addressOfUser.isPresent()) {
			return new GenericResponse(true, addressOfUser);
		}

		// create new address
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

	public GenericResponse getAddressOfUser(String username) {
		
		if (!usernameExists(username)) {
			return new GenericResponse(false, NON_EXISTANT_USERNAME);
		}

		User user = userRepository.findByUsername(username).get();
		Optional<String> addressOfUser = getOptionalAddressOfUser(user);
		if(addressOfUser.isPresent()) {
			return new GenericResponse(true, user.getAddress());
		} else {
			return new GenericResponse(false, NO_ADDRESS);
		}
	}

	public GenericResponse send(String username, String addressOfRecipient) {
		if (!usernameExists(username)) {
			return new GenericResponse(false, NON_EXISTANT_USERNAME);
		}

		User user = userRepository.findByUsername(username).get();
		Optional<String> addressOfUser = getOptionalAddressOfUser(user);
		
		if(addressOfUser.isPresent()) {
			return new GenericResponse(false, NO_ADDRESS); // if user doesn't have an address yet
		}
		
		String txId = null;
		txId = rpcService.createTransaction(addressOfUser.get(), addressOfRecipient);
		return new GenericResponse(true, txId);
	}
	
	private static Optional<String> getOptionalAddressOfUser(User user){
		return Optional.ofNullable(user.getAddress());
	}
}