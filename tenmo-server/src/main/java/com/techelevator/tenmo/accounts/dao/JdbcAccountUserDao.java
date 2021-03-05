package com.techelevator.tenmo.accounts.dao;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.accounts.model.AccountUser;

@Component
public class JdbcAccountUserDao implements AccountUserDao{

	
	  
	private JdbcTemplate jdbcTemplate;
	public JdbcAccountUserDao (JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public double getAccountBalance(String username) {
		
		String sql = "SELECT balance FROM accounts " + 
				"JOIN users on users.user_id = accounts.user_id " + 
				"WHERE username = ?";
		double accountBalance = jdbcTemplate.queryForObject(sql, double.class, username);
		
		return accountBalance;
	}

	@Override
	public List<AccountUser> getListOfAllUsers() {
		String sql = "SELECT users.user_id, balance, username, account_id FROM accounts " + 
				"JOIN users on users.user_id = accounts.user_id";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
		
		
		List<AccountUser> listOfUsers = new ArrayList<AccountUser>();
		
		while(rows.next()) {
			String storedUserName = rows.getString("username");
			
				AccountUser user = mapAccountUser(rows);
				listOfUsers.add(user);
		}
		return listOfUsers;
	}
	
	
	@Override
	public List<AccountUser> getListOfUsers(String username) {
		String sql = "SELECT users.user_id, balance, username, account_id FROM accounts " + 
				"JOIN users on users.user_id = accounts.user_id";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
		
		
		List<AccountUser> listOfUsers = new ArrayList<AccountUser>();
		
		while(rows.next()) {
			String storedUserName = rows.getString("username");
			if(!(storedUserName.equals(username))) {
				AccountUser user = mapAccountUser(rows);
				listOfUsers.add(user);
			}
		}
		return listOfUsers;
	}
	

	@Override
	public AccountUser updateAccountBalance(AccountUser accountUser) {
		
		//get account ID's
		String getAccountIdFrom = "SELECT account_id FROM accounts WHERE user_id = ?";
		int accountIdFrom = jdbcTemplate.queryForObject(getAccountIdFrom, int.class, accountUser.getUserId());
		
		
		
		String sql = "UPDATE accounts SET balance = ? WHERE account_id = ? RETURNING balance";
		jdbcTemplate.queryForRowSet(sql, accountUser.getAccountBalance(), accountIdFrom);

		return accountUser;
		
	}
/*
	@Override
	public AccountUser addMoney(AccountUser accountUser) {
		
		String getAccountIdTo = "SELECT account_id FROM accounts WHERE user_id = ?";
		int accountIdTo = jdbcTemplate.queryForObject(getAccountIdTo, int.class,accountUser.getUserId());
		
		String sql = "UPDATE accounts SET balance = ? WHERE account_id = ? RETURNING balance";
		jdbcTemplate.queryForRowSet(sql, accountUser.getAccountBalance(), accountIdTo);
		
		return accountUser;
		
	}
	*/
	
	private AccountUser mapAccountUser(SqlRowSet rows) {
		AccountUser accountUser = new AccountUser();
		accountUser.setUserId(rows.getInt("user_id"));
		accountUser.setAccountId(rows.getInt("account_id"));
		accountUser.setUsername(rows.getString("username"));
		accountUser.setAccountBalance(rows.getDouble("balance"));
		
		return accountUser;
	}


}
