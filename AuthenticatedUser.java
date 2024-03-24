package com.zybooks.inventoryapp;

public class AuthenticatedUser {

    private String userName;

    /**
     * Create object of user whose password has been authorized.
     * @param userName - username of user
     */
    public AuthenticatedUser(String userName) {
        this.userName = userName;
    }

    /**
     * Get user name of authorized user
     * @return - username
     */
    public String getUserName() {
        return userName;
    }

}
