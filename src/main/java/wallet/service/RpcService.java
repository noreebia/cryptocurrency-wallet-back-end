package wallet.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

@Service
public class RpcService {

	@Value("${ethereum.server.address}")
	private String serverAddress;

	@Value("${ethereum.user.account.password}")
	private String userAccountPassword;
	
	@Value("${ethereum.contract.address.kkc}")
	private String konkukCoinContractAddress;
	
	private static String BALANCEOF_FUNCTION_ID = "0x70a08231";

	private static String GET_ETH_BALANCE = "eth_getBalance";
	private static String INVOKE_TOKEN_FUNCTION = "eth_call";
	private static String CREATE_ACCOUNT = "personal_newAccount";
	private static String SEND_TRANSACTION = "eth_sendTransaction";
	private static String UNLOCK_ACCOUNT = "personal_unlockAccount";
	private static String LOCK_ACCOUNT = "personal_lockAccount";

	public String createAddress() throws RpcException {
		JSONObject requestBody = new JSONObject();
		requestBody.put("jsonrpc", "2.0");
		requestBody.put("method", CREATE_ACCOUNT);

		JSONArray jsonArray = new JSONArray();
		jsonArray.put(userAccountPassword);
		requestBody.put("params", jsonArray);
		requestBody.put("id", 1);

		JSONObject responseFromNode = sendRequestToNode(requestBody);
		if (responseFromNode.has("error")) {
			throw new RpcException(responseFromNode.getJSONObject("error").getString("message"));
		}
		return responseFromNode.getString("result");
	}

	public String createTransaction(String addressOfSender, String addressOfRecipient) throws RpcException {

		unlockAccount(addressOfSender);

		JSONObject requestBody = new JSONObject();
		requestBody.put("jsonrpc", "2.0");
		requestBody.put("method", SEND_TRANSACTION);

		JSONArray jsonArray = new JSONArray();
		jsonArray.put(userAccountPassword);
		requestBody.put("params", jsonArray);
		requestBody.put("id", 1);

		JSONObject responseFromNode = sendRequestToNode(requestBody);
		if (responseFromNode.has("error")) {
			throw new RpcException(responseFromNode.getJSONObject("error").getString("message"));
		}
		lockAccount(addressOfSender);
		return responseFromNode.getString("result");
	}

	private void unlockAccount(String addressToUnlock) throws RpcException {
		JSONObject requestBody = new JSONObject();
		requestBody.put("jsonrpc", "2.0");
		requestBody.put("method", UNLOCK_ACCOUNT);

		JSONArray jsonArray = new JSONArray();
		jsonArray.put(addressToUnlock);
		jsonArray.put(userAccountPassword);
		requestBody.put("params", jsonArray);
		requestBody.put("id", 1);

		JSONObject responseFromNode = sendRequestToNode(requestBody);
		if (responseFromNode.has("error")) {
			throw new RpcException(responseFromNode.getJSONObject("error").getString("message"));
		}
	}

	private void lockAccount(String addressToLock) throws RpcException {
		JSONObject requestBody = new JSONObject();
		requestBody.put("jsonrpc", "2.0");
		requestBody.put("method", LOCK_ACCOUNT);

		JSONArray jsonArray = new JSONArray();
		jsonArray.put(addressToLock);
		requestBody.put("params", jsonArray);
		requestBody.put("id", 1);

		JSONObject responseFromNode = sendRequestToNode(requestBody);
		if (responseFromNode.has("error")) {
			throw new RpcException(responseFromNode.getJSONObject("error").getString("message"));
		}
	}

	private JSONObject sendRequestToNode(JSONObject httpBody) {
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

		// CloseableHttpResponse response = null;
		// try {
		// response = httpClient.execute(httpPost);
		// } catch (ClientProtocolException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// HttpEntity responseEntity = response.getEntity();
		// String responseString = EntityUtils.toString(responseEntity,
		// StandardCharsets.UTF_8);
		// httpClient.close();
		// return new JSONObject(responseString);
	}
	
	public Map<String, BigInteger> getCurrentBalances(String address){
		Map<String, BigInteger> map = new HashMap<>();
		map.put("eth", getBalance("eth", address));
		map.put("kkc", getBalance("kkc", address));
		return map;
	}

	public BigInteger getBalance(String symbol, String address) {
		switch (symbol) {
		case "eth":
			JSONObject request = new JSONObject();
			request.put("jsonrpc", "2.0");
			request.put("method", GET_ETH_BALANCE);

			JSONArray arrayOfParameters = new JSONArray();
			arrayOfParameters.put(address);
			arrayOfParameters.put("latest");
			request.put("params", arrayOfParameters);
			request.put("id", 1);
			JSONObject response = sendRequestToNode(request);
			if (response.has("error")) {
				throw new RpcException(response.getJSONObject("error").getString("message"));
			}
			System.out.println(response);
			return new BigInteger(response.getString("result").substring(2), 16);
		case "kkc":
			JSONObject jsonObject2 = new JSONObject();
			jsonObject2.put("jsonrpc", "2.0");
			jsonObject2.put("method", INVOKE_TOKEN_FUNCTION);

			JSONArray jsonArray2 = new JSONArray();
			JSONObject jsonObject21 = new JSONObject();
			
			jsonObject21.put("to", konkukCoinContractAddress);
			
			String addressFormatted = address.substring(2);
//		    String dataField = String.format("%1$" + 64 + "s", addressFormatted);
		    
		    String dataField = String.format("%64s", addressFormatted).replace(' ', '0');
		    System.out.println(dataField);
			String dataString = BALANCEOF_FUNCTION_ID + dataField;
			jsonObject21.put("data", dataString);
			
			jsonArray2.put(jsonObject21);
			jsonArray2.put("latest");
			jsonObject2.put("params", jsonArray2);
			jsonObject2.put("id", 1);
			
			System.out.println(jsonObject2.toString());
			JSONObject jsonResponse2 = sendRequestToNode(jsonObject2);
			System.out.println(jsonResponse2);
			System.out.println(jsonResponse2.getString("result").substring(2));
			if (jsonResponse2.has("error")) {
				throw new RpcException(jsonResponse2.getJSONObject("error").getString("message"));
			}
			return new BigInteger(jsonResponse2.getString("result").substring(2), 16);			
		default:
			break;
		}
		return null;
	}
}
