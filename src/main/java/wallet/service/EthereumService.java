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
	
	ExecutorService executorService = Executors.newFixedThreadPool(3);

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

//	@Scheduled(fixedDelay = 3000)
//	public void checkForDeposit () {
//		if(syncedBlockHeight == 0) {
//			initializeSyncedBlockHeight();
//		}
//		logger.debug("Checking for deposits");
//		long currentBlockHeight = rpcService.getCurrentBlockHeight();
//		logger.debug("Synced height: " + syncedBlockHeight + "current height: " + currentBlockHeight);
//		if(currentBlockHeight > syncedBlockHeight) {
//			for(long i=syncedBlockHeight; i <= currentBlockHeight; i++) {
//				
//				class DepositChecker implements Runnable {
//			        JSONObject block;
//			        DepositChecker(JSONObject block) { this.block = block; }
//			        public void run() {
//						JSONObject block = rpcService.getBlock(i);
//						System.out.println(block);
//						JSONArray transactions = block.getJSONArray("transactions");
//						for(int k= 0; k < transactions.length(); k++) {
//							JSONObject transaction = transactions.getJSONObject(k);
//							System.out.println(transaction);
//							Optional<String> toAddressOrNull = Optional.ofNullable(rpcService.getToAddress(transaction));
//							if(toAddressOrNull.isPresent()) {
//								String toAddress = toAddressOrNull.get();
//								if(userRepository.existsByAddress(toAddress)) {
//									logger.debug("Found deposit to user with address" + toAddress + "!");
//									User user = userRepository.findByAddress(toAddress).get();
//									userService.updateUserBalances(user);
//									logger.debug("Updated user balance");
//								} else if(toAddressOrNull.equals(konkukCoinContractAddress)) {
//									logger.debug("Found konkukcoin transaction");
//								}						
//							}
//
//						}			        }
//			    }
//				
//				executorService.execute( () -> {});
//				JSONObject block = rpcService.getBlock(i);
//				System.out.println(block);
//				JSONArray transactions = block.getJSONArray("transactions");
//				for(int k= 0; k < transactions.length(); k++) {
//					JSONObject transaction = transactions.getJSONObject(k);
//					System.out.println(transaction);
//					Optional<String> toAddressOrNull = Optional.ofNullable(rpcService.getToAddress(transaction));
//					if(toAddressOrNull.isPresent()) {
//						String toAddress = toAddressOrNull.get();
//						if(userRepository.existsByAddress(toAddress)) {
//							logger.debug("Found deposit to user with address" + toAddress + "!");
//							User user = userRepository.findByAddress(toAddress).get();
//							userService.updateUserBalances(user);
//							logger.debug("Updated user balance");
//						} else if(toAddressOrNull.equals(konkukCoinContractAddress)) {
//							logger.debug("Found konkukcoin transaction");
//						}						
//					}
//
//				}
//			}			
//		}
//
//		syncedBlockHeight = currentBlockHeight;
//		logger.debug("Finished deposit checking");
//	}
}
