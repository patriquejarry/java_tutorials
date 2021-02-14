package com.di.services;

import com.di.annotations.CustomComponent;

@CustomComponent
public class AccountServiceImpl implements AccountService {
	@Override
	public Long getAccountNumber(String userName) {
		return 12345689L;
	}
}