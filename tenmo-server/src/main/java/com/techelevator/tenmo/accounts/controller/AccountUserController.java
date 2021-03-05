package com.techelevator.tenmo.accounts.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.accounts.dao.AccountUserDao;
import com.techelevator.tenmo.accounts.model.AccountUser;
import com.techelevator.tenmo.accounts.model.Transfer;


@RestController
@PreAuthorize("isAuthenticated()")
public class AccountUserController {

	private AccountUserDao dao;
	
	public AccountUserController(AccountUserDao dao) {
		this.dao = dao;
	}
	

	
	@RequestMapping(path = "/users/accounts", method = RequestMethod.GET)
	public double getAccountBalance(Principal principal) {
		
		return dao.getAccountBalance(principal.getName());
	}
	
	@RequestMapping(path = "/users", method = RequestMethod.GET)
	public List<AccountUser> getListOfUserNames(Principal principal) {
		return dao.getListOfAllUsers();
	}
	
	
	@RequestMapping(path="/user/{id}/accounts", method=RequestMethod.PUT)
	public AccountUser updateBalance(@RequestBody AccountUser accountUser, @PathVariable int id) {
		return dao.updateAccountBalance(accountUser);
	} 
	
}
