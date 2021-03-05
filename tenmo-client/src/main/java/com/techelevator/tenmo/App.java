package com.techelevator.tenmo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.techelevator.tenmo.models.AccountUser;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AccountUserService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.view.ConsoleService;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountUserService accountUserService;
    private TransferService transferService;
    private Transfer transfer;
    
    DecimalFormat df = new DecimalFormat("#,###.##");

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		accountUserService = new AccountUserService(API_BASE_URL, currentUser);
		transferService = new TransferService(API_BASE_URL, currentUser);
		transfer = new Transfer();
		mainMenu();
	}

	
	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				double accountBalance = accountUserService.getAccountBalance();
				console.displayAccountBalance(accountBalance);
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferDetails();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				initiateSendTransfer(filterListOfUsers());
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				initiateRequestTransfer(filterListOfUsers());
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}
	
	private List<AccountUser> filterListOfUsers() {
		
		List<AccountUser> filteredListOfUsers = new ArrayList<AccountUser>();
		List<AccountUser> listOfUsers = accountUserService.getListOfAllUsers();
		
		for(AccountUser accountUser : listOfUsers) {
			if(!(accountUser.getUserId() == currentUser.getUser().getId())) {
				filteredListOfUsers.add(accountUser);
			}
		}
		return filteredListOfUsers;
	}
	
	private AccountUser mappingUser (int userId) {
		AccountUser selectedUser = new AccountUser();
		List<AccountUser> users = accountUserService.getListOfAllUsers();
		
		for(AccountUser user : users) {
			if(user.getUserId() == userId) {
				selectedUser = user; 
				break;
			}
		}	
		return selectedUser;
	}

	
	private void initiateSendTransfer (List<AccountUser> users ) {
		Transfer sendTransfer = new Transfer();
		int receivingUser = 0;
		double amountToTransfer = 0;
		AccountUser loggedInUser = new AccountUser();
		AccountUser receipient = new AccountUser();
		
		while(true) {
			
			while (true) {
				console.displayListOfUsers(filterListOfUsers());
				receivingUser = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)");
				if(receivingUser == 0) {
					return;
				}
				if (!(console.validatingUserIdInput(filterListOfUsers(), receivingUser))) {
					console.prompt("*** Invalid User *** Please select user ID from list");
				} else {
					break;
				}
			}
			
			amountToTransfer = console.getAmountToTransfer();
			
			if((accountUserService.getAccountBalance() < amountToTransfer)) {
				console.prompt("*** Insufficient Funds *** current balance is " + accountUserService.getAccountBalance());
			} else if (amountToTransfer <= 0){
				console.prompt("*** Invalid Amount ***");	
			} else {
				sendTransfer.setUserToId(receivingUser);
				sendTransfer.setUsernameFrom(currentUser.getUser().getUsername());
				sendTransfer.setTransferAmount(amountToTransfer);
				sendTransfer.setTransferTypeId(2);
			
				loggedInUser = mappingUser(currentUser.getUser().getId());
				loggedInUser.subtractAccountBalance(amountToTransfer);
				
				receipient = mappingUser(receivingUser);
				receipient.addAccountBalance(amountToTransfer);
				
				accountUserService.updateAccountBalance(loggedInUser);
				accountUserService.updateAccountBalance(receipient);
				
				transferService.intiatingSendingTransfer(sendTransfer);
				console.prompt("Transfer Complete [Status Approved] Transfer Amount: " + amountToTransfer);
				console.prompt("Your current balance is: " + accountUserService.getAccountBalance());
				break;
			}

		}
		
	}
	
	private void initiateRequestTransfer(List<AccountUser> users) {
		Transfer requestTransfer = new Transfer();
		int userSelection = 0;
		double amountToTransfer = 0;
		
		while(true) {
			while (true) {
				console.displayListOfUsers(filterListOfUsers());
				userSelection = console.getUserInputInteger("Enter ID of user you are requesting from (0 to cancel)");
				if(userSelection == 0) {
					return;
				}
				if (!(console.validatingUserIdInput(filterListOfUsers(), userSelection))) {
					console.prompt("*** Invalid User *** Please select user ID from list");
				} else {
					break;
				}
			}
			amountToTransfer = console.getAmountToTransfer();
			
			if(amountToTransfer <= 0) {
				console.prompt("*** Invalid Amount ***");	
			} else {
				requestTransfer.setUserFromId(userSelection);
				requestTransfer.setUsernameTo(currentUser.getUser().getUsername());
				requestTransfer.setTransferAmount(amountToTransfer);
				requestTransfer.setTransferTypeId(1);
				
				transferService.intiatingRequestingTransfer(requestTransfer);
				console.prompt("Transfer Request Complete [Status Pending] Transfer Amount: " + amountToTransfer);
				break;
			}
		}
		
	}
	
	
	private void viewTransferDetails () {
		int userSelection = 0;
		
		while(true) {
			console.viewTransfers(transferService.getListOfTransfers(), currentUser.getUser().getUsername());
			userSelection = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel)");
			if(userSelection == 0) {
				return;
			}
			if(!(console.validatingTransferIdInput(transferService.getListOfTransfers(), userSelection))) {
				console.prompt("*** Invalid Transfer ID *** Please select Transfer ID from list");
			} else {
				break;	
			}
			;
		}
		console.viewTransferDetails(transfer.matchTransferObjectFromList(transferService.getListOfTransfers(), userSelection));
	}
	
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
