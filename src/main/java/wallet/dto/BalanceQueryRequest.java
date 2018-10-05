package wallet.dto;

import java.util.List;

import wallet.entity.Balance;

public class BalanceQueryRequest {
	List<Balance> balances;

	public List<Balance> getBalances() {
		return balances;
	}

	public void setBalances(List<Balance> balances) {
		this.balances = balances;
	}
}
