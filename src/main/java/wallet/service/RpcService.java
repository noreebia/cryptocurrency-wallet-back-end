package wallet.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

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

@Service
public class RpcService {
	
	@Value("${ethereum.server.address}")
	private String serverAddress;
	
	public String createAddress() throws ParseException, IOException {
			System.out.println("server address: " + serverAddress);
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(serverAddress);
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("jsonrpc", "2.0");
			jsonObject.put("method", "personal_newAccount");
			
			JSONArray jsonArray = new JSONArray();
			jsonArray.put("1234");
			jsonObject.put("params", jsonArray);
			jsonObject.put("id", 1);
			
			System.out.println(jsonObject.toString());
			StringEntity entity = new StringEntity(jsonObject.toString());
			httpPost.setEntity(entity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			
			CloseableHttpResponse response = httpClient.execute(httpPost);
			HttpEntity responseEntity = response.getEntity();
			String responseString = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
			System.out.println(responseString);
			httpClient.close();
			
			JSONObject object = new JSONObject(responseString);
			try {
				object.getString("result");
			} catch(Exception e) {
				throw new ParseException();
			}
			return object.getString("result");
	}
}
