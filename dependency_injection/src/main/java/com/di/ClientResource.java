package com.di;

import com.di.annotations.CustomAutowired;
import com.di.annotations.CustomComponent;
import com.di.annotations.CustomQualifier;
import com.di.services.AccountService;
import com.di.services.UserService;

/**
 * Client class, having userService and accountService expected to be
 * initialized by CustomInjector.java
 */
@CustomComponent
public class ClientResource {

	@CustomAutowired
	private UserService userService;

	@CustomAutowired
	@CustomQualifier(value = "accountServiceImpl")
	private AccountService accountService;

	public void displayUserAccount() {
		final String username = userService.getUserName();
		final Long accountNumber = accountService.getAccountNumber(username);
		System.out.println("\nUser Name: " + username + " Account Number: " + accountNumber);
	}
}
