package wallet.exception;

public class RpcException extends RuntimeException{

	private static final long serialVersionUID = 7576286966959449252L;

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
