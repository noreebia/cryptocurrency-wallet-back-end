package wallet;

import java.math.BigInteger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import wallet.utility.HexUtil;

@RunWith(SpringRunner.class)
@SpringBootTest

public class HexUtilTest {
	
	@Test
	public void hexToDecimalTest() {
		String hexString = "0x34df78";
		System.out.println(HexUtil.hexStringToDecimalBigInt(hexString));
		System.out.println(HexUtil.hexStringToDecimalBigInt(hexString).toString());
	}
	
	@Test
	public void decimalToHexTest() {
		String stringNumber = "10000";
		int intNumber = 10000;
		Long longNumber = 10000L;
		BigInteger bigIntNumber = new BigInteger("10000");
		System.out.println(HexUtil.decimalToHexString(intNumber));
		System.out.println(HexUtil.decimalToHexString(longNumber));
		System.out.println(HexUtil.decimalToHexString(stringNumber));
		System.out.println(HexUtil.decimalToHexString(bigIntNumber));
	}
}
