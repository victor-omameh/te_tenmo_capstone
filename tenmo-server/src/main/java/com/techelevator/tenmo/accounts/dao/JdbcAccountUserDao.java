package com.techelevator.tenmo.accounts.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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

}
