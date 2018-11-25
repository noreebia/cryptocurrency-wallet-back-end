package wallet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import wallet.dto.Credentials;
import wallet.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CryptocurrencyWalletApplicationTests {
	
	@Autowired
	UserService userService;

	@Test
	public void contextLoads() {
	}
	
	@Test
	public void checkForExistence() {
	}
	
	@Test
	public void httpClientWorks() {
		userService.createAddressForUser(new Credentials("testusername"));
	}
}
