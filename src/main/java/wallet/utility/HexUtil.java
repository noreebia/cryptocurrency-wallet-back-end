package wallet.utility;

import java.math.BigInteger;

public class HexUtil {
	
	public static String decimalToHexString(String number) {
		BigInteger stringToBigInteger = new BigInteger(number);
		return stringToBigInteger.toString(16);
	}
	
	public static String decimalToHexString(int number) {
		return Integer.toHexString(number);
	}
	
	public static String decimalToHexString(long number) {
		return Long.toHexString(number);
	}
	
	public static String decimalToHexString(BigInteger number) {
		return number.toString(16);
	}

	public static BigInteger hexStringToDecimalBigInt (String hexString) {
		String formattedHexString = hexString; 
		if(hexString.startsWith("0x")) {
			formattedHexString = hexString.substring(2);
		}
		
		return new BigInteger(formattedHexString, 16);
	}
}
