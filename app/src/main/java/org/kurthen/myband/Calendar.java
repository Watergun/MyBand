package org.kurthen.myband;

/**
 * Created by Leonhard on 12.09.2016.
 */
public class Calendar{
    private Event[] events;

    public Calendar(){

    }

    public void setEvents(Event[] events){
        this.events = events;
    }

    public Event[] getEvents(){
        return events;
    }
}
