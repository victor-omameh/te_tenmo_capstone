package com.techelevator.tenmo.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AccountUser;
import com.techelevator.tenmo.models.AuthenticatedUser;

public class AccountUserService {
	
	private String baseUrl;
	private AuthenticatedUser user;
	private RestTemplate restTemplate = new RestTemplate();
	
	public AccountUserService (String baseUrl, AuthenticatedUser user) {
		this.baseUrl = baseUrl;
		this.user  = user;
	}
	
	public double getAccountBalance() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(user.getToken());
		HttpEntity<Double> entity = new HttpEntity(headers);
		
		double accountBalance = restTemplate.exchange(baseUrl + "users/accounts", HttpMethod.GET, entity, double.class).getBody();
		return accountBalance;
	}
	
	public List<AccountUser> getListOfUsers() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(user.getToken());
		HttpEntity<AccountUser> entity = new HttpEntity(headers);
		AccountUser[] users = restTemplate.exchange(baseUrl + "users", HttpMethod.GET, entity, AccountUser[].class).getBody();
		return Arrays.asList(users);
	}
}
