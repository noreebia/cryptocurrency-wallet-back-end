package wallet.entity;

import java.math.BigDecimal;

public class UserBalance {
	public String currencyName;
	public String currencySymbol;
	public BigDecimal balance;
	
	public UserBalance(String currencyName, String currencySymbol, BigDecimal balance) {
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

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	
}
