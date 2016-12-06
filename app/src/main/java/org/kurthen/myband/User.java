package org.kurthen.myband;

import android.graphics.drawable.Drawable;
import android.media.Image;

/**
 * Created by Leonhard on 12.09.2016.
 */
public class User {
    private int id;
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private Drawable mPictureThumbnail;
    private Drawable mPictureFullscreen;
    private String mPassword;
    private Band[] mBands;
    private Calendar mCalendar;
    private String[] mInstruments;

    public User(){
    }

    public User(String name, String email, String password){
        mFirstName = name;
        mEmail = email;
        mPassword = password;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public Drawable getPictureThumbnail() {
        return mPictureThumbnail;
    }

    public void setPictureThumbnail(Drawable mPicture) {
        this.mPictureThumbnail = mPicture;
    }

    public Band[] getBands() {
        return mBands;
    }

    public void setBands(Band[] mBands) {
        this.mBands = mBands;
    }

    public String[] getInstruments() {
        return mInstruments;
    }

    public void setInstruments(String[] mInstruments) {
        this.mInstruments = mInstruments;
    }

    public void setEvents(Event[] events){
        if(mCalendar == null){
            mCalendar = new Calendar();
        }
        mCalendar.setEvents(events);
    }
}
