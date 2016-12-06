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
        user = new User();
    }

    private User user = new User();
    private int selectedBand = -1;

    public User getUser() {
        return user;
    }

    public Band[] getBands() {
        return user.getBands();
    }

    public void selectBand(int index){
        selectedBand = index;
    }

    public void setUser(User newUser){
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setEmail(newUser.getEmail());
        user.setPassword(newUser.getPassword());
        user.setBands(newUser.getBands());
        user.setInstruments(newUser.getInstruments());
        user.setPictureThumbnail(newUser.getPictureThumbnail());
    }

    public void resetUser(){
        user = new User();
    }

    public void setUserCredentials(String email, String password){
        user.setEmail(email);
        user.setPassword(password);
    }

    public void setUserCredentials(String name, String email, String password){
        user.setFirstName(name);
        user.setEmail(email);
        user.setPassword(password);
    }

    public Band getSelectedBand(){
        if(selectedBand >= 0)
            return user.getBands()[selectedBand];
        else
            return null;
    }

    public Update[] getUpdates(){
        Band b = getSelectedBand();
        if(b != null)
            return b.getUpdates();
        else
            return null;
    }

    public Event[] getEvents(){
        Band b = getSelectedBand();
        if(b != null)
            return b.getEvents();
        else
            return null;
    }

    public Transaction[] getTransactions(){
        Band b = getSelectedBand();
        if(b != null)
            return b.getTransactions();
        else
            return null;
    }

    public Song[] getSongs(){
        Band b = getSelectedBand();
        if(b != null)
            return b.getSongs();
        else
            return null;
    }
}
