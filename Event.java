package org.kurthen.myband;

import java.sql.Time;

/**
 * Created by Leonhard on 12.09.2016.
 */
public class Event extends Update {
    private String location;
    private Time starttime;
    private Time endtime;
    private String note;
    private int reminder;
    private boolean participation;
    private float pay;

    public Event(){
    }
}
