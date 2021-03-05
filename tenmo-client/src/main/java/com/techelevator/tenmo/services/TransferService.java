package com.techelevator.tenmo.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;

public class TransferService {
	
	private String baseUrl;
	private AuthenticatedUser user;
	private RestTemplate restTemplate = new RestTemplate();
	
	public TransferService (String baseUrl, AuthenticatedUser user) {
		this.baseUrl = baseUrl;
		this.user  = user;
	}
	
	public List<Transfer> getListOfTransfers (){
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(user.getToken());
		HttpEntity entity = new HttpEntity(headers);
		
		Transfer[] transfers = restTemplate.exchange(baseUrl + "transfers", HttpMethod.GET, entity, Transfer[].class).getBody();
		
		return Arrays.asList(transfers);
	}
	
	public Transfer intiatingSendingTransfer(Transfer transfer) {
		 
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(user.getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Transfer> entity = new HttpEntity<Transfer>(transfer, headers);
		
		return restTemplate.exchange(baseUrl + "transfers", HttpMethod.POST, entity, Transfer.class).getBody();
	}
	
	public Transfer intiatingRequestingTransfer(Transfer transfer) {
		 
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(user.getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Transfer> entity = new HttpEntity<Transfer>(transfer, headers);
		
		return restTemplate.exchange(baseUrl + "transfers", HttpMethod.POST, entity, Transfer.class).getBody();
	}
	
	public void updateTransferRequestStatus(Transfer transfer) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(user.getToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Transfer> entity = new HttpEntity<Transfer>(transfer, headers);
		
		restTemplate.exchange(baseUrl + "transfers", HttpMethod.PUT, entity, Transfer.class).getBody();
		
	}
	
	
}
