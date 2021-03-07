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
	public Transfer intiatingSendTransfer(int userToId, String userName, double amountToTransfer) {
		
		int userFromId = getUserId(userName);
		int accountFromId = getAccountId(userFromId);
		int accountToId = getAccountId(userToId);
		
		//inserting transfer into database
		String insertTransfer = "INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (DEFAULT, 2, 2, ?, ?, ?) RETURNING transfer_id";
		int transferID = jdbcTemplate.queryForObject(insertTransfer, int.class, accountFromId, accountToId, amountToTransfer);
		
		//pull first transfer details to create object
		String sql = "SELECT transfer_id, transfers.transfer_type_id, transfer_type_desc, transfers.transfer_status_id, transfer_status_desc, account_from, account_to, amount " + 
				"FROM transfers " + 
				"JOIN transfer_types tt ON tt.transfer_type_id = transfers.transfer_type_id " + 
				"JOIN transfer_statuses ts ON ts.transfer_status_id = transfers.transfer_status_id " + 
				"WHERE transfer_id = ?";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, transferID);
		row.next();

		return createTransfer(row);
	}
	

	@Override
	public Transfer intiatingRequestTransfer(int userFromId, String userName, double amountToTransfer) {
		
		int userToId = getUserId(userName);
		int accountIdTo = getAccountId(userToId);
		int accountIdFrom = getAccountId(userFromId);
				
		String insertTransfer = "INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (DEFAULT, 1, 1, ?, ?, ?) RETURNING transfer_id";
		int transferID = jdbcTemplate.queryForObject(insertTransfer, int.class, accountIdFrom, accountIdTo, amountToTransfer);
				
				
		String sql = "SELECT transfer_id, transfers.transfer_type_id, transfer_type_desc, transfers.transfer_status_id, transfer_status_desc, account_from, account_to, amount " + 
				"FROM transfers " + 
				"JOIN transfer_types tt ON tt.transfer_type_id = transfers.transfer_type_id " + 
				"JOIN transfer_statuses ts ON ts.transfer_status_id = transfers.transfer_status_id " + 
				"WHERE transfer_id = ?";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, transferID);
		row.next();
		return createTransfer(row);
	}
	

	@Override
	public List<Transfer> getListOfTransfers(String userName) {
		
		int accountId = getAccountIdWithUserName(userName);
		
		String sql = "SELECT transfer_id, transfers.transfer_type_id, transfer_type_desc, transfers.transfer_status_id, transfer_status_desc, account_from, account_to, amount " + 
				"FROM transfers " + 
				"JOIN transfer_types tt ON tt.transfer_type_id = transfers.transfer_type_id " + 
				"JOIN transfer_statuses ts ON ts.transfer_status_id = transfers.transfer_status_id " + 
				"WHERE account_from = ? OR account_to = ?";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
		
		List<Transfer> listOfTransfers = new ArrayList<Transfer>();
		
		while (row.next()) {
			Transfer transfer = createTransfer(row);	
			listOfTransfers.add(transfer);
		}
		return listOfTransfers;
	}
	
	
	@Override
	public int approveOrRejectTransferRequest(Transfer transfer) {
		
		String sql = "UPDATE transfers SET transfer_status_id = ? WHERE transfer_id = ? RETURNING transfer_status_id";
		 int updatedStatusId = jdbcTemplate.queryForObject(sql, int.class, transfer.getTransferStatusId(), transfer.getTransferId());
		
		return updatedStatusId;
	}
	
	
	/*
	 * 
	 * PRIVATE WORKER METHODS
	 * 
	 */
	
	private int getUserId(String userName) {
		String getUserID = "SELECT user_id FROM users WHERE username = ?";
		return jdbcTemplate.queryForObject(getUserID, int.class, userName);
	}
	
	private int getAccountId(int userId) {
		String getAccountId = "SELECT account_id FROM accounts WHERE user_id = ?";
		return jdbcTemplate.queryForObject(getAccountId, int.class, userId);	
	}
	
	private int getAccountIdWithUserName(String userName){
		String gettingAccountId = "SELECT account_id FROM users " + 
				"JOIN accounts ON accounts.user_id = users.user_id " + 
				"WHERE username = ?";
		return jdbcTemplate.queryForObject(gettingAccountId, int.class, userName);
	}
	
	private Transfer createTransfer(SqlRowSet row) {
		
		Transfer transfer = mapTransferDetailsToTransfer(row);
		int accountFromId = transfer.getAccountFromId();
		int accountToId = transfer.getAccountToId();
	
		String gettingAccountFromDetails = "SELECT account_id, accounts.user_id, username, balance FROM accounts " + 
				"JOIN users on users.user_id = accounts.user_id " + 
				"WHERE account_id = ?";
		SqlRowSet accountFromRow = jdbcTemplate.queryForRowSet(gettingAccountFromDetails, accountFromId);
		accountFromRow.next();
		transfer.setUserFromId(accountFromRow.getInt("user_id"));
		transfer.setUsernameFrom(accountFromRow.getString("username"));
		
		String gettingAccountToDetails = "SELECT account_id, accounts.user_id, username, balance FROM accounts " + 
				"JOIN users on users.user_id = accounts.user_id " + 
				"WHERE account_id = ?";
		SqlRowSet accountToRow = jdbcTemplate.queryForRowSet(gettingAccountToDetails, accountToId);
		accountToRow.next();
		transfer.setUserToId(accountToRow.getInt("user_id"));
		transfer.setUsernameTo(accountToRow.getString("username"));
		
		return transfer;
		
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
