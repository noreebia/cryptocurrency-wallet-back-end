package wallet.pojo;

import java.util.List;

public class UserDepositNotification {

	public String username;
	public List<UserBalance> balances;
	
	public UserDepositNotification(String username, List<UserBalance> balances) {
		super();
		this.username = username;
		this.balances = balances;
	}

	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public List<UserBalance> getBalances() {
		return balances;
	}
	
	public void setBalances(List<UserBalance> balances) {
		this.balances = balances;
	}
}
