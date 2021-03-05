package com.techelevator.tenmo.models;

public class AccountUser {
	private int accountId;
	private int userId;
	private double accountBalance;
	private String username;
	
	
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public double getAccountBalance() {
		return accountBalance;
	}
	public void setAccountBalance(double accountBalance) {
		this.accountBalance = accountBalance;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public double addAccountBalance(double transferAmount) {
		return this.accountBalance = accountBalance + transferAmount;
	}
	
	public double subtractAccountBalance(double transferAmount) {
		return this.accountBalance = accountBalance - transferAmount;
	}
}
