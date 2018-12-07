package wallet.pojo;

public class GenericResponse {
	boolean successful;
	Object data;
	
	public GenericResponse() {
	}
	
	public GenericResponse(boolean successful, Object data) {
		this.successful = successful;
		this.data = data;
	}
	public boolean isSuccessful() {
		return successful;
	}
	
	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
}
