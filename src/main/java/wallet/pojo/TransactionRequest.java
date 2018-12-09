package wallet.pojo;

public class TransactionRequest {
	public String username;
	public String currencySymbol;
	public String destinationAddress;
	public String amount;
	
	public TransactionRequest() {
		super();
	}
	
	public TransactionRequest(String username, String currencySymbol, String destinationAddress, String amount) {
		super();
		this.username = username;
		this.currencySymbol = currencySymbol;
		this.destinationAddress = destinationAddress;
		this.amount = amount;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	public String getDestinationAddress() {
		return destinationAddress;
	}
	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	
}
