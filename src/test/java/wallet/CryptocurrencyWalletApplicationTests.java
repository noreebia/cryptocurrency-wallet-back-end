package wallet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import wallet.service.RpcService;
import wallet.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CryptocurrencyWalletApplicationTests {

	@Autowired
	UserService userService;

	@Autowired
	RpcService rpcService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void checkForExistence() {
	}

	@Test
	public void httpClientWorks() {
		userService.createUserAddress("testusername");
	}

	@Test
	public void addressCreationWorks() {
		System.out.println(rpcService.createAddress());
	}
}
