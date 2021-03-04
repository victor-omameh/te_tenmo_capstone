package com.techelevator.tenmo.accounts.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.accounts.dao.TransferDao;
import com.techelevator.tenmo.accounts.model.Transfer;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

	private TransferDao dao;
	
	public TransferController (TransferDao dao) {
		this.dao = dao;
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping (path = "/transfers", method = RequestMethod.POST)
	public Transfer intiatingSendTransfer(@RequestBody Transfer transfer) {
		return dao.intiatingSendTransfer(transfer.getUserToId(), transfer.getUsernameFrom(), transfer.getTransferAmount());
	}
	
	
	@RequestMapping (path = "/transfers", method = RequestMethod.GET)
	public List<Transfer> getListOfTransfers (Principal principal){
		return dao.getListOfTransfers(principal.getName());
	}
}
