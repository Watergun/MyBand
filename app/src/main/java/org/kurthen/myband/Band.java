package org.kurthen.myband;

import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by Leonhard on 12.09.2016.
 */
public class Band {

    private int id;
    private String name;
    private float transactionSum;

    private Drawable pictureThumbnail;
    private Drawable pictureFullscreen;

    private User[] members;
    private Setlist[] setlists;
    private Song[] songs;
    private Event[] events;
    private Transaction[] transactions;
    private Update[] updates;

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

    public Setlist[] getSetlists() {
        return setlists;
    }

    public void setSetlists(Setlist[] setlists) {
        this.setlists = setlists;
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

    public Song[] getSongs() {
        return songs;
    }

    public void setSongs(Song[] songs) {
        this.songs = songs;
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
