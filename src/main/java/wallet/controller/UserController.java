package wallet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wallet.pojo.Credentials;
import wallet.pojo.GenericResponse;
import wallet.pojo.User;
import wallet.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("")
	public List<User> getAllUsers() {
		return userService.findAll();
	}

	@PostMapping("")
	public GenericResponse createUser(@RequestBody Credentials credentials) {
		return userService.createUser(credentials);
	}

	@PostMapping("/validation")
	public GenericResponse isValidCredentials(@RequestBody Credentials credentials) {
		return userService.isValidCredentials(credentials);
	}

	@GetMapping("/{username}")
	public GenericResponse getUserInfo(@PathVariable String username) {
		return userService.getUser(username);
	}

	@GetMapping("/{username}/balances")
	public GenericResponse getUserBalance(@PathVariable String username) {
		return userService.getUserBalance(username);
	}
	
	@GetMapping("/{username}/addresses")
	public GenericResponse getUserAddress(@PathVariable String username) {
		return userService.getUserAddress(username);
	}

	@PostMapping("/{username}/addresses")
	public GenericResponse createUserAddress(@PathVariable String username) {
		return userService.createUserAddress(username);
	}
	
	@PostMapping("/{username}/transactions/{currencySymbol}/{destinationAddress}/{amount}")
	public GenericResponse sendTransaction(@PathVariable String username, @PathVariable String currencySymbol, @PathVariable String destinationAddress, @PathVariable String amount) {
		return userService.transfer(username, destinationAddress, currencySymbol, amount);
	}
}
