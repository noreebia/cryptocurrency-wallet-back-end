package wallet.dto;

import java.util.Optional;

public class UserCreationResponse {
	boolean successful;
	Optional<String> message;
	
	public boolean isSuccessful() {
		return successful;
	}
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
	public Optional<String> getMessage() {
		return message;
	}
	public void setMessage(Optional<String> message) {
		this.message = message;
	}
}
