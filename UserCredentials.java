package com.zybooks.inventoryapp;

public class UserCredentials {

    private String username;
    private String password;

    /**
     * Creates object that holds username and password for logged in user.
     * @param username - username of logged in user
     * @param password - password of logged in user
     */
    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @return - username of logged in user
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return  -password of logged in user
     */
    public String getPassword() {
        return password;
    }
}
