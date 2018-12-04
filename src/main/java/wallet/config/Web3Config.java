package wallet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class Web3Config {
	
	@Value("${ethereum.server.address}")
	private String ethServer;
	
	@Bean
	public Web3j web3() {
		return Web3j.build(new HttpService(ethServer));
	}
}
