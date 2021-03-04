package com.techelevator.tenmo.accounts.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.accounts.model.Transfer;

@Component
public class JdbcTransferDao implements TransferDao{

	private JdbcTemplate jdbcTemplate;
	public JdbcTransferDao (JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public Transfer intiatingSendTransfer(int userIdTo, String username, double amountToTransfer) {
		
		//get user ID for logged in User
		String getUserID = "SELECT user_id FROM users WHERE username = ?";
		int userFromId = jdbcTemplate.queryForObject(getUserID, int.class, username);
		
		//get account ID's
		String getAccountIdFrom = "SELECT account_id FROM accounts WHERE user_id = ?";
		int accountIdFrom = jdbcTemplate.queryForObject(getAccountIdFrom, int.class, userFromId);
		
		String getAccountIdTo = "SELECT account_id FROM accounts WHERE user_id = ?";
		int accountIdTo = jdbcTemplate.queryForObject(getAccountIdTo, int.class, userIdTo);
		
		//inserting transfer into database
		String insertTransfer = "INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (DEFAULT, 2, 1, ?, ?, ?) RETURNING transfer_id";
		int transferID = jdbcTemplate.queryForObject(insertTransfer, int.class, accountIdFrom, accountIdTo, amountToTransfer);
		
		//pull first transfer details to create object
		String sql = "SELECT transfer_id, transfers.transfer_type_id, transfer_type_desc, transfers.transfer_status_id, transfer_status_desc, account_from, account_to, amount " + 
				"FROM transfers " + 
				"JOIN transfer_types tt ON tt.transfer_type_id = transfers.transfer_type_id " + 
				"JOIN transfer_statuses ts ON ts.transfer_status_id = transfers.transfer_status_id " + 
				"WHERE transfer_id = ?";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, transferID);
		
		Transfer intiatingSendTransfer = new Transfer();
		
		//mapping first details
		row.next();
		intiatingSendTransfer = mapTransferDetailsToTransfer(row);
		
		
		//pulling user info and mapping
		String gettingAccountFromDetails = "SELECT account_id, accounts.user_id, username, balance FROM accounts " + 
				"JOIN users on users.user_id = accounts.user_id " + 
				"WHERE account_id = ?";
		SqlRowSet accountFromRow = jdbcTemplate.queryForRowSet(gettingAccountFromDetails, accountIdFrom);
		accountFromRow.next();
		intiatingSendTransfer.setUserFromId(accountFromRow.getInt("user_id"));
		intiatingSendTransfer.setUsernameFrom(accountFromRow.getString("username"));
		//intiatingSendTransfer.setAccountFromCurrentBalance(accountFromRow.getDouble("balance"));
		
		String gettingAccountToDetails = "SELECT account_id, accounts.user_id, username, balance FROM accounts " + 
				"JOIN users on users.user_id = accounts.user_id " + 
				"WHERE account_id = ?";
		SqlRowSet accountToRow = jdbcTemplate.queryForRowSet(gettingAccountToDetails, accountIdTo);
		accountToRow.next();
		intiatingSendTransfer.setUserToId(accountToRow.getInt("user_id"));
		intiatingSendTransfer.setUsernameTo(accountToRow.getString("username"));
		//intiatingSendTransfer.setAccountToCurrentBalance(accountToRow.getDouble("balance"));
		
		
		return intiatingSendTransfer;
	}
	
	
	
	@Override
	public Transfer intiatingRequestTransfer(int userFrom, String username, double amountToTransfer) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public List<Transfer> getListOfTransfers(String username) {
		
		String gettingAccountId = "SELECT account_id FROM users " + 
				"JOIN accounts ON accounts.user_id = users.user_id " + 
				"WHERE username = ?";
		int accountId = jdbcTemplate.queryForObject(gettingAccountId, int.class, username);
		
		String sql = "SELECT transfer_id, transfers.transfer_type_id, transfer_type_desc, transfers.transfer_status_id, transfer_status_desc, account_from, account_to, amount " + 
				"FROM transfers " + 
				"JOIN transfer_types tt ON tt.transfer_type_id = transfers.transfer_type_id " + 
				"JOIN transfer_statuses ts ON ts.transfer_status_id = transfers.transfer_status_id " + 
				"WHERE account_from = ? OR account_to = ?";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
		
		
		List<Transfer> listOfTransfers = new ArrayList<Transfer>();
		Transfer transfer = new Transfer();
		
		while (row.next()) {
			transfer = mapTransferDetailsToTransfer(row);
			//int transferId = transfer.getTransferId();
			
			int accountFromId = transfer.getAccountFromId();
			int accountToId = transfer.getAccountToId();
			
			String gettingAccountFromDetails = "SELECT account_id, accounts.user_id, username, balance FROM accounts " + 
					"JOIN users on users.user_id = accounts.user_id " + 
					"WHERE account_id = ?";
			SqlRowSet accountFromRow = jdbcTemplate.queryForRowSet(gettingAccountFromDetails, accountFromId);
			accountFromRow.next();
			transfer.setUserFromId(accountFromRow.getInt("user_id"));
			transfer.setUsernameFrom(accountFromRow.getString("username"));
			//transfer.setAccountFromCurrentBalance(accountFromRow.getDouble("balance"));
			
			String gettingAccountToDetails = "SELECT account_id, accounts.user_id, username, balance FROM accounts " + 
					"JOIN users on users.user_id = accounts.user_id " + 
					"WHERE account_id = ?";
			SqlRowSet accountToRow = jdbcTemplate.queryForRowSet(gettingAccountToDetails, accountToId);
			accountToRow.next();
			transfer.setUserToId(accountToRow.getInt("user_id"));
			transfer.setUsernameTo(accountToRow.getString("username"));
			//transfer.setAccountToCurrentBalance(accountToRow.getDouble("balance"));
			
			listOfTransfers.add(transfer);
			
		}
		
		return listOfTransfers;
	}
	
	
	private Transfer mapTransferDetailsToTransfer(SqlRowSet row) {
		Transfer transfer = new Transfer();
		
		transfer.setAccountFromId(row.getInt("account_from"));
		transfer.setAccountToId(row.getInt("account_to"));
		transfer.setTransferAmount(row.getDouble("amount"));
		transfer.setTransferId(row.getInt("transfer_id"));
		transfer.setTransferStatusId(row.getInt("transfer_status_id"));
		transfer.setTransferStatus(row.getString("transfer_status_desc"));
		transfer.setTransferTypeId(row.getInt("transfer_type_id"));
		transfer.setTransferType(row.getString("transfer_type_desc"));
		
		return transfer;
		
	}
	
	
}
