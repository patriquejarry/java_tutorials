package com.useraccount;

import com.useraccount.di.framework.annotations.CustomAutowired;
import com.useraccount.di.framework.annotations.CustomComponent;
import com.useraccount.di.framework.annotations.CustomQualifier;
import com.useraccount.services.AccountService;
import com.useraccount.services.UserService;

/**
 * Client class, having userService and accountService expected to initialized
 * by CustomInjector.java
 */
@CustomComponent
public class UserAccountClientComponent {

	@CustomAutowired
	private UserService userService;

	@CustomAutowired
	@CustomQualifier(value = "accountServiceImpl")
	private AccountService accountService;

	public void displayUserAccount() {

		final String username = userService.getUserName();
		final Long accountNumber = accountService.getAccountNumber(username);

		System.out.println("User Name: " + username + "Account Number: " + accountNumber);
	}
}
