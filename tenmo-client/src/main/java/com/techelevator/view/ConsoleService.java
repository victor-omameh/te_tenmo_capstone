package com.techelevator.view;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import com.techelevator.tenmo.models.AccountUser;
import com.techelevator.tenmo.models.Transfer;

public class ConsoleService {

	private PrintWriter out;
	private Scanner in;

	public ConsoleService(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output, true);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		out.println();
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) {
			out.println(System.lineSeparator() + "*** " + userInput + " is not a valid option ***" + System.lineSeparator());
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		out.print(System.lineSeparator() + "Please choose an option >>> ");
		out.flush();
	}

	public String getUserInput(String prompt) {
		out.print(prompt+": ");
		out.flush();
		return in.nextLine();
	}

	public Integer getUserInputInteger(String prompt) {
		Integer result = null;
		do {
			out.print(prompt+": ");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Integer.parseInt(userInput);
			} catch(NumberFormatException e) {
				out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
			}
		} while(result == null);
		return result;
	}
	
	public void displayAccountBalance(double accountBalance) {
		out.println("Your current account balance is: " + accountBalance);
	}
	
	public void displayListOfUsers(List<AccountUser> users) {
		String lineFormat = "%-10s %-50s %n";
		out.println("--------------------------");
		out.println("Users");
		out.printf(lineFormat,"ID","Name");
		out.println("--------------------------");
		
		for(AccountUser user : users) {
			out.printf(lineFormat,user.getUserId(),user.getUsername() );
			
		} out.println("----------");
	}
	
	public double getAmountToTransfer() {
		double amount = 0.0;
		while(true) {
			out.print("Enter amount: ");
			out.flush();
			String userInput = in.nextLine();
			try {
				amount = Double.parseDouble(userInput);
				break;
			} catch(NumberFormatException e) {
				out.println(System.lineSeparator() + "*** " + userInput + " is not valid ***" + System.lineSeparator());
			}
		}
		return amount;
	} 
	
	public void prompt(String prompt) {
		out.println(prompt);
	}
	
	public boolean validatingUserIdInput(List<AccountUser> userList, int userSelection) {
		
		boolean selectionValid = false;
		for (AccountUser users : userList) {
			if (users.getUserId() == userSelection) {
				selectionValid = true;
				break;
			}
		}
		
		return selectionValid;
		
	}
	public boolean validatingTransferIdInput(List<Transfer> transferList, int userSelection) {
		
		boolean selectionValid = false;
		for ( Transfer transfer: transferList) {
			if (transfer.getTransferId() == userSelection) {
				selectionValid = true;
				break;
			}
		}
		
		return selectionValid;
		
	}
	
	
	public void viewTransfers(List<Transfer> transfers, String username) {
		String lineFormat = "%-10s %-15s %-15s %n";
		out.println("-----------------------------------------");
		out.println("Transfers");
		out.printf(lineFormat,"ID","From/To", "Amount");
		out.println("-----------------------------------------");
		
		String user = "";
		
		for(Transfer transfer : transfers) {
			
			String userNameFrom = transfer.getUsernameFrom();
			String userNameTo = transfer.getUsernameTo();	
			
			if(transfer.getTransferType().equals("Send")) {
				if(transfer.getUsernameTo().equals(username)) {
					user = "From: " + transfer.getUsernameFrom();
				} else if(!(transfer.getUsernameTo().equals(username))) {
					user = "To: " + transfer.getUsernameTo();
				}
			}
			out.printf(lineFormat, transfer.getTransferId(), user, transfer.getTransferAmount());
		}
		out.println("----------");
		
	}
	
	public void viewTransferDetails(Transfer selectedTransfer) {
		out.println("-----------------------------------------");
		out.println("Transfers Details");
		out.println("-----------------------------------------");
		out.println("ID: " + selectedTransfer.getTransferId());
		out.println("From: " + selectedTransfer.getUsernameFrom());
		out.println("To: " + selectedTransfer.getUsernameTo());
		out.println("Type: " + selectedTransfer.getTransferType());
		out.println("Status: " + selectedTransfer.getTransferStatus());
		out.println("Amount: $" + selectedTransfer.getTransferAmount());
		
	}
}
