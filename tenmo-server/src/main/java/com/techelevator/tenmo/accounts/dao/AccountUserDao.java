package com.techelevator.tenmo.accounts.dao;

import java.util.List;

import com.techelevator.tenmo.accounts.model.AccountUser;

public interface AccountUserDao {
	
	public double getAccountBalance(String username);
	
	public List<AccountUser> getListOfUsers(String username);

}
