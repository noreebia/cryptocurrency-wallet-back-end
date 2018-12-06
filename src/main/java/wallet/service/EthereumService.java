package wallet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock.TransactionResult;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

import rx.Subscription;

@Service
public class EthereumService {

	private Web3j web3j;

	@Autowired
	public EthereumService(Web3j web3) {
		this.web3j = web3;
//		 getTransactions();
//		 getBlocks();
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

	public void getTransactions() {
		//// Disposable subscription = web3.transactionFlowable().subscribe(tx -> {
		//// System.out.println(tx.toString()); });
		// System.out.println("I'm working!");
		//// Disposable subscription = web3.blockFlowable(false).subscribe(block -> {
		//// System.out.println(block.toString());
		//// });
		//
		//
		// Subscription subscription = ((Object) web3j).replayTransactionsFlowable()
		// .subscribe(tx -> {});
		// }

		 Subscription subscription = web3j.transactionObservable().subscribe(tx -> {
		 System.out.println(tx.getTo());
		 });
	}
	
	public void getBlocks() {
		Subscription subscription2 = web3j.blockObservable(true).subscribe(block -> {
			System.out.println("hi!");
			System.out.println(block.getBlock().toString());
			for(TransactionResult transaction: block.getBlock().getTransactions()) {
				System.out.println(transaction.get().toString());
			}
		});
	}
}
