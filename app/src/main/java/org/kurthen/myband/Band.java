package org.kurthen.myband;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.text.TextUtils;

/**
 * Created by Leonhard on 12.09.2016.
 */
public class Band {

    private int id;
    private String name;
    private float transactionSum;

    private Drawable pictureThumbnail;
    private String pictureHash;
    private Drawable pictureFullscreen;

    private User[] members;
    private Event[] events;
    private Transaction[] transactions;
    private Update[] updates;
    private Contact[] contacts;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public User[] getMembers() {
        return members;
    }

    public void setMembers(User[] members) {
        this.members = members;
    }

    public Drawable getPicture() {
        return pictureThumbnail;
    }

    public void setPictureThumbnail(Drawable picture) {
        this.pictureThumbnail = picture;
    }

    public boolean comparePictureHash(String hash){
        return TextUtils.equals(pictureHash, hash);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getTransactionSum() {
        return transactionSum;
    }

    public void setTransactionSum(float transactionSum) {
        this.transactionSum = transactionSum;
    }

    public Contact[] getContacts() {
        return contacts;
    }

    public void setSongs(Contact[] songs) {
        this.contacts = songs;
    }

    public Transaction[] getTransactions() {
        return transactions;
    }

    public void setTransactions(Transaction[] transactions) {
        this.transactions = transactions;
    }

    public Update[] getUpdates(){
        return updates;
    }

    public void setUpdates(Update[] updates){
        this.updates = updates;
    }

    public Event[] getEvents(){
        return events;
    }

    public void setEvents(Event[] events){
        this.events = events;
    }
}
