package wallet.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class RpcService {
	
	public String createAddress() throws ParseException, IOException {
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost("http://localhost:8545");
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("jsonrpc", "2.0");
			jsonObject.put("method", "personal_newAccount");
			
			jsonObject.put("params", new String[] {"1234"});
			jsonObject.put("id", 1);
			
			System.out.println(jsonObject.toString());
//			String httpBody = "{ \"jsonrpc\": \"2.0\", \"method\": \"personal_newAccount\", \"params\": [\"1234\"], \"id\": 1 }";
			StringEntity entity = new StringEntity(jsonObject.toString());
			httpPost.setEntity(entity);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
			
			CloseableHttpResponse response = client.execute(httpPost);
			
			System.out.println(response.getEntity());
			HttpEntity responseEntity = response.getEntity();
			String responseString = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
			client.close();
			
			JSONObject object = new JSONObject(responseString);
			System.out.println(object.getString("result"));
			return object.getString("result");
	}
}
