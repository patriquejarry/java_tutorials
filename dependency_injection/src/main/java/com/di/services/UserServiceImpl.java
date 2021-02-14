package com.di.services;

import com.di.annotations.CustomComponent;

@CustomComponent
public class UserServiceImpl implements UserService {
   @Override
   public String getUserName() {
     return "shital.devalkar";
   }
}