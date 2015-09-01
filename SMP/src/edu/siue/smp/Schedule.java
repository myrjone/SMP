package edu.siue.smp;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Schedule {
    private String name;
    private LocalDateTime created;
    private ArrayList<Event> events;
    
    public Schedule (String name) {
        this.name = name;
        this.created = LocalDateTime.now();
        this.events = new ArrayList<Event>();
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }
}
