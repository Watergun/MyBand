package org.kurthen.myband;

/**
 * Created by Leonhard on 12.09.2016.
 */
public class Update {

    private int id;
    private String title;
    private User creator;
    private String[] comments;
    private int notficationCounter;

    public Update(){

    }

    public String getTitle() {
        return title;
    }

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String[] getComments() {
        return comments;
    }

    public void setComments(String[] comments) {
        this.comments = comments;
    }

    public int getNotficationCounter(){
        return notficationCounter;
    }

    public void setNotficationCounter(int nC){
        notficationCounter = nC;
    }
}
