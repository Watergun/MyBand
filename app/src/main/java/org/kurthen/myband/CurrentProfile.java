package org.kurthen.myband;

/**
 * Created by Leonhard on 01.10.2016.
 */
public class CurrentProfile {
    private static CurrentProfile ourInstance = new CurrentProfile();

    public static CurrentProfile getInstance() {
        return ourInstance;
    }

    private CurrentProfile() {
    }

    private static User user = new User();

    public User getUser() {
        return user;
    }

    public void setUser(User newUser){
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setEmail(newUser.getEmail());
        user.setPassword(newUser.getPassword());
        user.setBands(newUser.getBands());
        user.setInstruments(newUser.getInstruments());
        user.setPicture(newUser.getPicture());
    }
}
