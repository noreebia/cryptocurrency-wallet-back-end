package wallet.pojo;

public class UserDepositNotification {

	public String username;
	public String transactionHash;
	
	public UserDepositNotification(String username, String transactionHash) {
		super();
		this.username = username;
		this.transactionHash = transactionHash;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getTransactionHash() {
		return transactionHash;
	}
	
	public void setTransactionHash(String transactionHash) {
		this.transactionHash = transactionHash;
	}
}
