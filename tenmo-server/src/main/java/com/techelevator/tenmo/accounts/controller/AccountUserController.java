package com.techelevator.tenmo.accounts.controller;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.accounts.dao.AccountUserDao;


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
	
	
	
}
