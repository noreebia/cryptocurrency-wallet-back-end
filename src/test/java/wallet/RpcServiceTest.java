package wallet;

import java.math.BigDecimal;
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
		BigDecimal test = rpcService.getBalance("eth", "0x4b40e8d11300c9904854f06095a1460f8136434e");
		System.out.println(test.toString());
	}
	
	@Test
	public void getTokenbalance() {
		BigDecimal test = rpcService.getBalance("kkc", "0x580a89eb8aa6dBa5Bf78f7F921225C990a9Ef7d3");
		System.out.println(test.toString());
	}
	
	@Test
	public void transferEth() {
		String sender = "0x62a7511960e1fb1060e8a828029a0cfcaee59dea";
		String receiver = "0x580a89eb8aa6dBa5Bf78f7F921225C990a9Ef7d3";
		String symbol = "eth";
		String amount = "0.1";
		try {
			System.out.println(rpcService.transfer(sender, receiver, symbol, amount));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getBlockHeight() {
		System.out.println(rpcService.getCurrentBlockHeight());
	}
	
	@Test
	public void getBlock() {
		System.out.println(rpcService.getBlock(3465448));
	}
}
