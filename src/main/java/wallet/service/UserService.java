package wallet.service;

import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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
			User result = userRepository.save(user);
			if (result.getUsername() == user.getUsername() && result.getPassword() == user.getPassword()) {
				response.setSuccessful(true);
			}
		}
		return response;
	}

	public GenericResponse getBalanceOf(String username, String currencySymbol) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			return new GenericResponse(true, user.getBalances().get(currencySymbol));
		}
		return new GenericResponse(false, NON_EXISTANT_USERNAME);
	}

	public GenericResponse getUser(String username) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			return new GenericResponse(true, user);
		}
		return new GenericResponse(false, NON_EXISTANT_USERNAME);
	}

	public GenericResponse isExistingUsername(String username) {
		return new GenericResponse(usernameExists(username), null);
	}

	public boolean usernameExists(String username) {
		return userRepository.existsByUsername(username);
	}

	public GenericResponse isValidCredentials(Credentials credentials) {
		boolean isValidCredentials = userRepository.existsByPassword(credentials.getPassword())
				&& userRepository.existsByUsername(credentials.getUsername());
		
		String details = null;
		if (!isValidCredentials) {
			details = INVALID_CREDENTIALS;
		}
		return new GenericResponse(isValidCredentials, details);
	}

	public GenericResponse createAddressForUser(String username) {
		if(!userRepository.existsByUsername(username)) {
			return new GenericResponse(false, NON_EXISTANT_USERNAME);
		}
		
		// check if already has an address
		User user = userRepository.findByUsername(username);
		if(user.getAddress() != null) {
			return new GenericResponse(true, user.getAddress());
		}
		
		// create new address
		String newAddress = null;
		try {
			newAddress = rpcService.createAddress();
		} catch (ParseException | IOException e) {
			e.printStackTrace();
			return new GenericResponse(false, e);
		}

		user.setAddress(newAddress);
		userRepository.save(user);

		return new GenericResponse(true, newAddress);
	}
	
	public GenericResponse getAddressOfUser(String username) {
		if(!usernameExists(username)) {
			return new GenericResponse(false, null);
		}
		
		User user = userRepository.findByUsername(username);
		
		if(user.getAddress() == null) {
			return new GenericResponse(false, NO_ADDRESS); // if user doesn't have an address yet
		} else {
			return new GenericResponse(true, user.getAddress()); 
		}
	}
}