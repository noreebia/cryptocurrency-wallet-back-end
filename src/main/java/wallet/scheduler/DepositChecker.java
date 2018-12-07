package wallet.scheduler;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;

import wallet.pojo.User;
import wallet.pojo.UserDepositNotification;
import wallet.repository.UserRepository;
import wallet.service.RpcService;
import wallet.service.UserService;

@Service
public class DepositChecker {

	Logger logger = LoggerFactory.getLogger(DepositChecker.class);

	@Autowired
	private SimpMessagingTemplate template;
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
	public DepositChecker(Web3j web3, UserRepository userRepository, UserService userService) {
		this.userRepository = userRepository;
		this.userService = userService;
	}

	@PostConstruct
	private void initializeSyncedBlockHeight() {
		this.syncedBlockHeight = rpcService.getCurrentBlockHeight();
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
			for (long i = syncedBlockHeight+1; i <= currentBlockHeight; i++) {
				checkSingleBlock(rpcService.getBlock(i));
			}
		}
		setSyncedBlockHeight(currentBlockHeight);
		logger.debug("Finished deposit checking");
	}

	private void checkSingleBlock(JSONObject block) {
		JSONArray transactions = block.getJSONArray("transactions");
		for (int k = 0; k < transactions.length(); k++) {
			JSONObject transaction = transactions.getJSONObject(k);
			logger.debug(transaction.toString());
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
						notifyDeposit(user);
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
	
	private void notifyDeposit(User user) {
		UserDepositNotification notification = new UserDepositNotification(user.getUsername(), user.getBalances());
		template.convertAndSend("topic/deposits", notification);
	}
}
