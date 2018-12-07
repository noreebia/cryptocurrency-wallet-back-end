package wallet.pojo;

public class Credentials {
	String username;
	String password;
	
	public Credentials() {
		super();
	}

	public Credentials(String username) {
		super();
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
