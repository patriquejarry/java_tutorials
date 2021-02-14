package com.di;

import com.di.annotations.CustomApplication;

@CustomApplication
public class MainApplicationClass {
	public static void main(String[] args) {
		DIApplication.run(MainApplicationClass.class, args);
	}
}