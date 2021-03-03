package com.techelevator.tenmo.accounts.dao;

import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTransferDao implements TransferDao{

	private JdbcTemplate jdbcTemplate;
	public JdbcTransferDao (JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
}
