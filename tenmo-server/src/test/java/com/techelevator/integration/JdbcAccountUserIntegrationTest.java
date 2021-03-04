package com.techelevator.integration;

import java.sql.SQLException;
import java.util.List;

import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;


import com.techelevator.tenmo.accounts.dao.AccountUserDao;
import com.techelevator.tenmo.accounts.dao.JdbcAccountUserDao;
import com.techelevator.tenmo.accounts.model.AccountUser;


public class JdbcAccountUserIntegrationTest {

	private AccountUserDao dao;
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
		dao = new JdbcAccountUserDao(jdbcTemplate);
	}
	
	
	@Test
	public void gettingAccountBalance() {
		
		createTestUser("testName");
		double result = dao.getAccountBalance("testName");
		
		Assert.assertEquals(1000.00, result, 0.0);
		
	}
	
	@Test
	public void getListOfUsers() {
		createTestUser("testname1");
		createTestUser("testname2");
		
		List<AccountUser> result = dao.getListOfUsers("testname1");
		AccountUser testResultUser = new AccountUser();
		for(AccountUser user: result) {
			if(user.getUsername().equals("testname2")) {
				testResultUser = user;
			}
		}
		
		Assert.assertEquals("testname2", testResultUser.getUsername());
	}
	
	
	
	private void createTestUser(String testName) {
		
		String sql = "INSERT INTO users (user_id, username, password_hash) VALUES (DEFAULT, ? , ? ) RETURNING user_id";
		
		int userID = jdbcTemplate.queryForObject(sql, int.class, testName, "test1");
		
		createAccount(userID);
		
	}
	
	private void createAccount(int userId) {
		
		String sql = "INSERT INTO accounts (account_id, user_id, balance) VALUES (DEFAULT, ?, 1000) RETURNING account_id";
		jdbcTemplate.queryForRowSet(sql, userId);
		
	}
	
}
