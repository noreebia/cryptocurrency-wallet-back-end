package wallet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import wallet.scheduler.DepositChecker;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EthereumServiceTest {

	@Autowired
	DepositChecker ethService;
	
}
