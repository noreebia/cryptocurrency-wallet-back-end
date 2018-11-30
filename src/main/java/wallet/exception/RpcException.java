package wallet.exception;

public class RpcException extends RuntimeException{

	public RpcException(String errorMessage, Throwable err) {
		super(errorMessage, err);
	}
	
	public RpcException(String errorMessage) {
		super(errorMessage);
	}
	
	public RpcException(Throwable err) {
		super(err);
	}

}
