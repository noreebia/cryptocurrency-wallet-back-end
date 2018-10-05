package wallet.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wallet.dto.RequestCredentials;
import wallet.dto.UserCreationResponse;
import wallet.entity.Balance;
import wallet.entity.User;
import wallet.repository.UserRepository;

@Service
public class UserService {
	
	private static String EXISTING_USERNAME_MESSAGE = "Username already exists. Please try a different one. ";

	UserRepository userRepository;
	
	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public List<User> findAll() {
		return userRepository.findAll();
	}
	
	public UserCreationResponse createUser(RequestCredentials credentials) {
		UserCreationResponse response = new UserCreationResponse();
		if(isExistingUsername(credentials.getUsername())) {
			response.setSuccessful(false);
			response.setMessage(Optional.of(EXISTING_USERNAME_MESSAGE));
		} else {
			User user = new User();
			user.setUsername(credentials.getUsername());
			user.setPassword(credentials.getPassword());
			User result = userRepository.save(user);
			if(result.getUsername() == user.getUsername() && result.getPassword() == user.getPassword()) {
				response.setSuccessful(true);
			}			
		}
		return response;
	}
	
	public List<Balance> getBalanceOf(RequestCredentials credentials) {
		User user = userRepository.findByUsername(credentials.getUsername());
		return user.getBalances();
	}
	
	public boolean isExistingUsername (String username) {
		return userRepository.existsByUsername(username);
	}
}