package wallet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import wallet.dto.Credentials;
import wallet.dto.GenericResponse;
import wallet.entity.User;
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

	@GetMapping("{username}/balances/{currencySymbol}")
	public GenericResponse getBalancesOfUser(@PathVariable String username, @PathVariable String currencySymbol) {
		return userService.getBalanceOf(username, currencySymbol);
	}
	
	@GetMapping("/{username}/addresses")
	public GenericResponse getAddressOfUser(@PathVariable String username) {
		return userService.getAddressOfUser(username);
	}

	@PostMapping("/{username}/addresses")
	public GenericResponse createAddressForUser(@PathVariable String username) {
		return userService.createAddressForUser(username);
	}
	
//	@GetMapping("/{username}/balances/{currencySymbol}")
//	public GenericResponse getBalance(@PathVariable String username, @PathVariable String currencySymbol) {
//		return null;
//	}
}
