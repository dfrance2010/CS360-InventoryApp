package com.zybooks.inventoryapp;

public class AuthenticatedUserManager {

    private AuthenticatedUser user;
    private static AuthenticatedUserManager instance;

    // Singleton class to only allow one authenticated user at a time
    private AuthenticatedUserManager() {

    }

    public static AuthenticatedUserManager getInstance() {
        if (instance == null) {
            instance = new AuthenticatedUserManager();
        }

        return instance;
    }

    /**
     * Get current application user
     * @return - AuthenticatedUser
     */
    public AuthenticatedUser getUser() {
        return user;
    }

    /**
     * Set current user
     * @param user - AuthenticatedUser
     */
    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }
}
