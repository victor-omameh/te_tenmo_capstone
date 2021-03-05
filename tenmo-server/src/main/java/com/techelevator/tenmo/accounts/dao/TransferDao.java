package com.techelevator.tenmo.accounts.dao;

import java.util.List;

import com.techelevator.tenmo.accounts.model.Transfer;

public interface TransferDao {

	public Transfer intiatingSendTransfer(int userIdTo, String username, double amountToTransfer);
	
	public Transfer intiatingRequestTransfer(int userFrom, String username, double amountToTransfer);
	
	public List<Transfer> getListOfTransfers(String username);
	
	public int approveOrRejectTransferRequest(Transfer transfer);
	
	
}
