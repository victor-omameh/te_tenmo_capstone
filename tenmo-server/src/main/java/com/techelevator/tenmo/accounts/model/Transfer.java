package com.techelevator.tenmo.accounts.model;

public class Transfer {
	
	private int transferId;
	private int accountFromId;
	private int accountToId;
	private double transferAmount;
	private String transferType;
	private String transferStatus;
	
	
	
	public int getTransferId() {
		return transferId;
	}
	public void setTransferId(int transferId) {
		this.transferId = transferId;
	}
	public int getAccountFromId() {
		return accountFromId;
	}
	public void setAccountFromId(int accountFromId) {
		this.accountFromId = accountFromId;
	}
	public int getAccountToId() {
		return accountToId;
	}
	public void setAccountToId(int accountToId) {
		this.accountToId = accountToId;
	}
	public double getTransferAmount() {
		return transferAmount;
	}
	public void setTransferAmount(double transferAmount) {
		this.transferAmount = transferAmount;
	}
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	public String getTransferStatus() {
		return transferStatus;
	}
	public void setTransferStatus(String transferStatus) {
		this.transferStatus = transferStatus;
	}
	
	
	
	

}
