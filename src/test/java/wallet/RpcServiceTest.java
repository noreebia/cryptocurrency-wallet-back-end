package wallet;

import java.math.BigInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import wallet.service.RpcService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RpcServiceTest {
	
	@Autowired
	RpcService rpcService;
	
	@Test
	public void getEthBalance() {
		BigInteger test = rpcService.getBalance("eth", "0x7ffc57839b00206d1ad20c69a1981b489f772031");
		System.out.println(test.toString());
	}
	
	@Test
	public void getTokenbalance() {
		BigInteger test = rpcService.getBalance("kkc", "0x580a89eb8aa6dBa5Bf78f7F921225C990a9Ef7d3");
		System.out.println(test.toString());
	}
}
