package com.redhat.summit2015.ceplab.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Account {

	private String id;

	private AccountStatus status;

	private BigDecimal balance = BigDecimal.ZERO;

	public Account(AccountStatus status, BigDecimal balance) {
		super();
		this.status = status;
		this.balance = balance;
		this.id = String.valueOf(System.nanoTime());
	}

	public Account() {
		super();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", id)
				.append("status", status)
				.append("balance", balance).toString();
	};

	// -------------------------------------------------------------------------
	// generated methods
	// -------------------------------------------------------------------------
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Account other = (Account) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	// -------------------------------------------------------------------------
	// generated getters and setters
	// -------------------------------------------------------------------------
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

}
