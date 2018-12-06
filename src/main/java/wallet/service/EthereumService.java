package wallet.service;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

import wallet.entity.User;
import wallet.repository.UserRepository;

@Service
public class EthereumService {

	Logger logger = LoggerFactory.getLogger(EthereumService.class);

	private Web3j web3j;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RpcService rpcService;

	@Autowired
	private UserService userService;

	@Value("${ethereum.contract.address.kkc}")
	private String konkukCoinContractAddress;

	private static volatile long syncedBlockHeight = 0;

	@Autowired
	public EthereumService(Web3j web3, UserRepository userRepository, UserService userService) {
		this.web3j = web3;
		this.userRepository = userRepository;
		this.userService = userService;
	}

	@PostConstruct
	private void initializeSyncedBlockHeight() {
		this.syncedBlockHeight = rpcService.getCurrentBlockHeight();
	}

	public String getWeb3Version() {
		Web3ClientVersion web3ClientVersion = null;
		try {
			web3ClientVersion = web3j.web3ClientVersion().sendAsync().get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return web3ClientVersion.getWeb3ClientVersion();
	}

	@Scheduled(fixedDelay = 3000)
	public void checkBlocksForDeposits() {
		if (syncedBlockHeight == 0) {
			initializeSyncedBlockHeight();
		}
		logger.debug("Checking for deposits");
		long currentBlockHeight = rpcService.getCurrentBlockHeight();
		logger.debug("Synced height: " + syncedBlockHeight + " Current height: " + currentBlockHeight);
		if (currentBlockHeight > syncedBlockHeight) {
			for (long i = syncedBlockHeight; i <= currentBlockHeight; i++) {
				checkSingleBlock(rpcService.getBlock(i));
			}
		}
		setSyncedBlockHeight(currentBlockHeight);
		logger.debug("Finished deposit checking");
	}

	private void checkSingleBlock(JSONObject block) {
		JSONArray transactions = block.getJSONArray("transactions");
		System.out.println(konkukCoinContractAddress);
		for (int k = 0; k < transactions.length(); k++) {
			JSONObject transaction = transactions.getJSONObject(k);
			Optional<String> toAddressOrNull = Optional.ofNullable(rpcService.getToAddress(transaction));
			if (toAddressOrNull.isPresent()) {
				String toAddress = toAddressOrNull.get();
				if (toAddress.equals(konkukCoinContractAddress)) {
					logger.debug("Found KUC transaction");
					String dataField = transaction.getString("input");
					String userAddress = "0x" + dataField.substring(34, 74);
					logger.debug("formatted address: " + userAddress);
					Optional<User> optionalUser = userRepository.findByAddress(userAddress);
					if(optionalUser.isPresent()) {
						logger.debug("Found KUC transaction to user.");
						User user = optionalUser.get();
						userService.updateUserBalances(user);
						logger.debug("Updated balances of user with username " + user.getUsername());
					}
				} else if (userRepository.existsByAddress(toAddress)) {
					logger.debug("Found deposit to user with address" + toAddress + "!");
					User user = userRepository.findByAddress(toAddress).get();
					userService.updateUserBalances(user);
					logger.debug("Updated user balance with username " + user.getUsername() + " and address " + user.getAddress());
				}
			}
		}
	}
	
	private void setSyncedBlockHeight(long blockHeight) {
		this.syncedBlockHeight = blockHeight;
	}
}
