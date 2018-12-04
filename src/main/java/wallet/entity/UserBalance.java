package wallet.entity;

import java.math.BigInteger;

public class UserBalance {
	public String currencyName;
	public String currencySymbol;
	public BigInteger balance;
	
	public UserBalance(String currencyName, String currencySymbol, BigInteger balance) {
		this.currencyName = currencyName;
		this.currencySymbol = currencySymbol;
		this.balance = balance;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public BigInteger getBalance() {
		return balance;
	}

	public void setBalance(BigInteger balance) {
		this.balance = balance;
	}
	
	
}
