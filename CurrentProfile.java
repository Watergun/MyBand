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
}
