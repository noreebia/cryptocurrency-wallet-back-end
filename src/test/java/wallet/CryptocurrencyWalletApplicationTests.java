package wallet;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
}
