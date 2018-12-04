package wallet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import wallet.service.EthereumService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EthereumServiceTest {

	@Autowired
	EthereumService ethService;
	
	@Test
	public void getWeb3jVersionWorks() {
		System.out.println(ethService.getWeb3Version());
	}
}
