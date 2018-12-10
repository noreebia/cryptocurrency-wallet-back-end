package wallet.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import wallet.exception.RpcException;
import wallet.utility.HexUtil;

@Service
public class RpcService {

	@Value("${ethereum.server.address}")
	private String serverAddress;
	@Value("${ethereum.user.account.password}")
	private String userAccountPassword;
	@Value("${ethereum.contract.address.kuc}")
	private String konkukCoinContractAddress;

	private static String TRANSFER_FUNCTION_ID = "0xa9059cbb";
	private static String BALANCEOF_FUNCTION_ID = "0x70a08231";
	private static String GET_ETH_BALANCE = "eth_getBalance";
	private static String INVOKE_TOKEN_FUNCTION = "eth_call";
	private static String CREATE_ACCOUNT = "personal_newAccount";
	private static String SEND_TRANSACTION = "eth_sendTransaction";
	private static String UNLOCK_ACCOUNT = "personal_unlockAccount";
	private static String LOCK_ACCOUNT = "personal_lockAccount";
	private static String GET_BLOCK_HEIGHT = "eth_blockNumber";
	private static String GET_BLOCK = "eth_getBlockByNumber";

	@Value("${active.currencies}")
	public String[] activeCurrency;
	public String[] activeCurrencySymbols;

	BigDecimal decimals = new BigDecimal(new BigInteger("1000000000000000000"));
	
	@PostConstruct
	public void setActiveCurrencySymbols() {
		activeCurrencySymbols = new String[] {activeCurrency[0].split("@")[1], activeCurrency[1].split("@")[1]};
	}

	public String createAddress() throws RpcException {
		JSONObject request = buildBasicRequest();

		JSONArray jsonArray = new JSONArray();
		jsonArray.put(userAccountPassword);
		request.put("params", jsonArray);

		request.put("method", CREATE_ACCOUNT);

		JSONObject responseFromNode = sendRequest(request);
		if (responseFromNode.has("error")) {
			throw new RpcException(responseFromNode.getJSONObject("error").getString("message"));
		}
		return responseFromNode.getString("result");
	}

	public String transfer(String addressOfSender, String addressOfRecipient, String currencySymbol, String amount)
			throws RpcException {

		unlockAccount(addressOfSender);
		JSONObject request = buildBasicRequest();
		JSONArray jsonArray = new JSONArray();
		JSONObject parameterObject = new JSONObject();
		if(currencySymbol.equals(activeCurrencySymbols[0])) {
			parameterObject.put("from", addressOfSender);
			parameterObject.put("to", addressOfRecipient);
			BigInteger ethAmountToTransfer = new BigDecimal(amount).multiply(decimals).toBigInteger();
			parameterObject.put("value", "0x" + ethAmountToTransfer.toString(16));

			jsonArray.put(parameterObject);
			request.put("params", jsonArray);

			request.put("method", SEND_TRANSACTION);
		} else if(currencySymbol.equals(activeCurrencySymbols[1])) {
			parameterObject.put("from", addressOfSender);
			parameterObject.put("to", konkukCoinContractAddress);
			parameterObject.put("value", "0x0");

			String addressDataField = String.format("%64s", addressOfRecipient.substring(2)).replace(' ', '0');
			System.out.println(addressDataField);
			BigInteger kucAmountToTransfer = new BigDecimal(amount).multiply(decimals).toBigInteger();
			String hexString = HexUtil.decimalToHexString(kucAmountToTransfer);
			String amountDataField = String.format("%64s", hexString).replace(' ', '0');
			System.out.println(amountDataField);
			String dataField = TRANSFER_FUNCTION_ID + addressDataField + amountDataField;
			System.out.println(dataField);
			parameterObject.put("data", dataField);
			jsonArray.put(parameterObject);
			request.put("params", jsonArray);
			request.put("method", SEND_TRANSACTION);
		} else {
			throw new RpcException("No matching symbol");
		}
		JSONObject responseFromNode = sendRequest(request);
		if (responseFromNode.has("error")) {
			throw new RpcException(responseFromNode.getJSONObject("error").getString("message"));
		}
		lockAccount(addressOfSender);
		System.out.println(responseFromNode.toString());
		return responseFromNode.getString("result");
	}

	private void unlockAccount(String addressToUnlock) throws RpcException {
		JSONObject request = buildBasicRequest();

		JSONArray jsonArray = new JSONArray();
		jsonArray.put(addressToUnlock);
		jsonArray.put(userAccountPassword);
		request.put("params", jsonArray);

		request.put("method", UNLOCK_ACCOUNT);

		JSONObject responseFromNode = sendRequest(request);
		if (responseFromNode.has("error")) {
			throw new RpcException(responseFromNode.getJSONObject("error").getString("message"));
		}
	}

	private void lockAccount(String addressToLock) throws RpcException {
		JSONObject request = buildBasicRequest();
		request.put("method", LOCK_ACCOUNT);

		JSONArray jsonArray = new JSONArray();
		jsonArray.put(addressToLock);
		request.put("params", jsonArray);

		JSONObject responseFromNode = sendRequest(request);
		if (responseFromNode.has("error")) {
			throw new RpcException(responseFromNode.getJSONObject("error").getString("message"));
		}
	}

	/* keys and values that every request has in common */
	private JSONObject buildBasicRequest() {
		JSONObject basicRequest = new JSONObject();
		basicRequest.put("jsonrpc", "2.0");
		basicRequest.put("id", 1);
		return basicRequest;
	}

	public BigDecimal getBalance(String symbol, String address) {

		JSONObject request = buildBasicRequest();
		JSONArray parameters = new JSONArray();
		
		if(symbol.equals(activeCurrencySymbols[0])) {
			parameters.put(address);
			parameters.put("latest");

			request.put("params", parameters);
			request.put("method", GET_ETH_BALANCE);
	
		} else if(symbol.equals(activeCurrencySymbols[1])) {
			String formattedAddress = address.substring(2);
			String dataField = String.format("%64s", formattedAddress).replace(' ', '0');
			String dataString = BALANCEOF_FUNCTION_ID + dataField;

			JSONObject invocationDetails = new JSONObject();
			invocationDetails.put("to", konkukCoinContractAddress);
			invocationDetails.put("data", dataString);

			parameters.put(invocationDetails);
			parameters.put("latest");

			request.put("params", parameters);
			request.put("method", INVOKE_TOKEN_FUNCTION);

		} else {
			throw new RpcException("No matching symbols");
		}
		JSONObject response = sendRequest(request);
		if (response.has("error")) {
			throw new RpcException(response.getJSONObject("error").getString("message"));
		}
		String balanceInHex = response.getString("result");
		if (balanceInHex.equals("0x")) {
			return BigDecimal.valueOf(0);
		}
		BigInteger balanceInHexFormat = new BigInteger(balanceInHex.substring(2), 16);
		BigDecimal bigDecimalBalance = new BigDecimal(balanceInHexFormat);
		return bigDecimalBalance.divide(decimals);
	}

	private JSONObject sendRequest(JSONObject httpBody) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(serverAddress);

		StringEntity entity = null;
		try {
			entity = new StringEntity(httpBody.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");

		try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
			HttpEntity responseEntity = response.getEntity();
			String responseString = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
			return new JSONObject(responseString);
		} catch (ParseException | IOException e) {
			throw new RpcException(e);
		}
	}

	public long getCurrentBlockHeight() {
		JSONObject request = buildBasicRequest();
		request.put("method", GET_BLOCK_HEIGHT);

		JSONArray parameters = new JSONArray();
		request.put("params", parameters);

		JSONObject response = sendRequest(request);
		if (hasError(response)) {
			throw new RpcException(getErrorMessage(response));
		}
		BigInteger blockHeight = HexUtil.hexStringToDecimalBigInt(getResultString(response));
		return blockHeight.longValue();

	}

	public JSONObject getBlock(long blockNumber) {
		JSONObject request = buildBasicRequest();
		request.put("method", GET_BLOCK);
		JSONArray parameters = new JSONArray();
		parameters.put("0x" + HexUtil.decimalToHexString(blockNumber));
		parameters.put(true);
		request.put("params", parameters);
		JSONObject response = sendRequest(request);
		if (hasError(response)) {
			throw new RpcException(getErrorMessage(response));
		}
		return getResultObject(response);
	}

	public boolean isContractTransaction(JSONObject transaction) {
		if (transaction.getString("input").equals("0x")) {
			return true;
		}
		return false;
	}

	public String getToAddress(JSONObject transaction) {
		if (transaction.isNull("to")) {
			return null;
		}
		return transaction.getString("to");
	}

	public boolean isSmartContractTransaction(JSONObject transaction) {
		if (transaction.getString("input").equals("0x")) {
			return false;
		}
		return true;
	}

	private boolean hasError(JSONObject response) {
		if (response.has("error")) {
			return true;
		}
		return false;
	}

	private String getErrorMessage(JSONObject response) {
		return response.getJSONObject("error").getString("message");
	}

	private String getResultString(JSONObject response) {
		return response.getString("result");
	}

	private JSONObject getResultObject(JSONObject response) {
		return response.getJSONObject("result");
	}
}
