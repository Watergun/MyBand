package org.kurthen.myband;

import android.text.TextUtils;

/**
 * Created by Leonhard on 16.09.2016.
 */
public class DataObjectManager {
    private static DataObjectManager instance = new DataObjectManager();

    public static DataObjectManager getInstance() {
        return instance;
    }

    private DataObjectManager() {
    }

    public User createUser(String name, String email, String password){
        return new User(name, email, password);
    }

    public Band createBand(String name){
        return new Band();
    }

    public Event createEvent(String title){
        return new Event();
    }

    public Transaction createTransaction(String title){
        return new Transaction();
    }

}
