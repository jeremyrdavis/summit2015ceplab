package com.redhat.summit2015.ceplab.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Transaction {
	
	private String id;
	
	private TransactionStatus status;
	
	private Account fromAccount;
	private Account toAccount;
	
	private BigDecimal amount;
	
	public Transaction() {
		super();
	}

	public Transaction(Account fromAccount, Account toAccount, BigDecimal amount) {
		super();
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.amount = amount;
		this.status = TransactionStatus.SCHEDULED;
		this.id = String.valueOf(System.nanoTime());
	}
	
	@Override
	public String toString(){
		return new ToStringBuilder(this).append("id", id)
				.append("from", fromAccount.getId())
				.append("to", toAccount.getId())
				.append("amount", amount)
				.append("status", status).toString();
	}
	//--------------------------------------------------------------------------
	//	generated getters and setters
	//--------------------------------------------------------------------------
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result
				+ ((fromAccount == null) ? 0 : fromAccount.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((toAccount == null) ? 0 : toAccount.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (fromAccount == null) {
			if (other.fromAccount != null)
				return false;
		} else if (!fromAccount.equals(other.fromAccount))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (status != other.status)
			return false;
		if (toAccount == null) {
			if (other.toAccount != null)
				return false;
		} else if (!toAccount.equals(other.toAccount))
			return false;
		return true;
	}

	//--------------------------------------------------------------------------
	//	generated getters and setters
	//--------------------------------------------------------------------------
	public TransactionStatus getStatus() {
		return status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public Account getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(Account fromAccount) {
		this.fromAccount = fromAccount;
	}

	public Account getToAccount() {
		return toAccount;
	}

	public void setToAccount(Account toAccount) {
		this.toAccount = toAccount;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
