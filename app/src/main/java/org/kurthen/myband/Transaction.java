package org.kurthen.myband;

/**
 * Created by Leonhard on 12.09.2016.
 */
public class Transaction extends Update {
    private float value;
    private String description;
    private Event event;

    public Transaction(){

    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
