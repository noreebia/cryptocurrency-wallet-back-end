package wallet.pojo;

public class UserDepositNotification {

	public String username;
	public String symbol;
	public String transactionHash;
	
	
	public UserDepositNotification(String username, String transactionHash, String symbol) {
		super();
		this.username = username;
		this.symbol = symbol;
		this.transactionHash = transactionHash;
	}

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

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
