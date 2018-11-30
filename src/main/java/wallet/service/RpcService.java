package wallet.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

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

	public String createAddress() throws RpcException {
		JSONObject requestBody = new JSONObject();
		requestBody.put("jsonrpc", "2.0");
		requestBody.put("method", "personal_newAccount");

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
		requestBody.put("method", "eth_sendTransaction");

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
		requestBody.put("method", "personal_unlockAccount");

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
		requestBody.put("method", "personal_lockAccount");

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
}
