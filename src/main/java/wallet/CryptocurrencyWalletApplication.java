package wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptocurrencyWalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptocurrencyWalletApplication.class, args);
	}
}
