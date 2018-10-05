package wallet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import wallet.dto.RequestCredentials;
import wallet.dto.UserCreationResponse;
import wallet.entity.Balance;
import wallet.entity.User;
import wallet.service.UserService;

@RestController
public class UserController {
	
	UserService userService;
	
	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping(value="/users")
	public List<User> getAllUsers() {
		return userService.findAll();
	}
	
	@PostMapping(value="/users")
	public UserCreationResponse createUser(@RequestBody RequestCredentials credentials) {
		return userService.createUser(credentials);
	}
	
	@PostMapping(value="/balances")
	public List<Balance> getBalancesOfUser(@RequestBody RequestCredentials credentials){
		return userService.getBalanceOf(credentials);
	}
	
}
