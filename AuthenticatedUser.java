package com.zybooks.inventoryapp;

public class AuthenticatedUser {

    private String userName;

    public AuthenticatedUser(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

}
