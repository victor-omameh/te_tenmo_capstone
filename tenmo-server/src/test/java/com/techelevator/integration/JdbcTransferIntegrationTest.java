package com.techelevator.integration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.tenmo.accounts.dao.JdbcAccountUserDao;
import com.techelevator.tenmo.accounts.dao.JdbcTransferDao;
import com.techelevator.tenmo.accounts.dao.TransferDao;
import com.techelevator.tenmo.accounts.model.Transfer;
public class JdbcTransferIntegrationTest {

	private TransferDao dao;
	private JdbcTemplate jdbcTemplate;
	private static SingleConnectionDataSource dataSource;
	
	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");

		dataSource.setAutoCommit(false);
	}
	
	@AfterClass
	public static void closeDataSource() throws SQLException {
		dataSource.destroy();
	}
	
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}
	
	@Before
	public void setup() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		dao = new JdbcTransferDao(jdbcTemplate);
	}
	
	
	@Test
	public void correctAmountSending() {
		
		int userID = createTestUser("testToUser");
		createTestUser("testFromUser");
		
		Transfer result = dao.intiatingSendTransfer(userID, "testFromUser", 50);
		
		Assert.assertEquals(50, result.getTransferAmount(), 0.0);
		
	}
	
	@Test
	public void correctUserDetailsSendTransfer() {
		
		int userID = createTestUser("testToUser");
		createTestUser("testFromUser");
		
		Transfer result = dao.intiatingSendTransfer(userID, "testFromUser", 50);
		
		Assert.assertEquals(userID, result.getUserToId());
		
	}
	
	
	@Test 
	public void get_list_of_transfers() {
		
		int userID = createTestUser("testToUser");
		createTestUser("testFromUser");
		Transfer test1 = dao.intiatingSendTransfer(userID, "testFromUser", 50);
		Transfer test2 = dao.intiatingSendTransfer(userID, "testFromUser", 100);
		
		List<Transfer> testList = new ArrayList<Transfer>();
		testList.add(test1);
		testList.add(test2);
		
		List<Transfer> result = dao.getListOfTransfers("testFromUser");
		
		Assert.assertEquals(testList.size(), result.size());
		
	}
	
	@Test
	public void correctUserDetailsRequestTransfer() {
		
		createTestUser("testToUser");
		int userID = createTestUser("testFromUser");
		
		Transfer result = dao.intiatingRequestTransfer(userID, "testToUser", 50);
		
		Assert.assertEquals(userID, result.getUserFromId());
		
	}
	
	@Test
	public void updating_request_status_to_approved() {
		createTestUser("testToUser");
		int userID = createTestUser("testFromUser");
		Transfer test = dao.intiatingRequestTransfer(userID, "testToUser", 50);
		test.setTransferStatusId(2);
		
		int result = dao.approveOrRejectTransferRequest(test);
		
		Assert.assertEquals(2, result);
		
	}
	
	
	@Test
	public void updating_request_status_to_rejected() {
		createTestUser("testToUser");
		int userID = createTestUser("testFromUser");
		Transfer test = dao.intiatingRequestTransfer(userID, "testToUser", 50);
		test.setTransferStatusId(3);
		
		int result = dao.approveOrRejectTransferRequest(test);
		
		Assert.assertEquals(3, result);
	}
	
	
	
	private int createTestUser(String testName) {
		
		String sql = "INSERT INTO users (user_id, username, password_hash) VALUES (DEFAULT, ? , ? ) RETURNING user_id";
		
		int userID = jdbcTemplate.queryForObject(sql, int.class, testName, "test1");
		
		createAccount(userID);
		
		return userID;
		
	}
	
	private void createAccount(int userId) {
		
		String sql = "INSERT INTO accounts (account_id, user_id, balance) VALUES (DEFAULT, ?, 1000) RETURNING account_id";
		jdbcTemplate.queryForRowSet(sql, userId);
		
	}
	
	
	
	
}
