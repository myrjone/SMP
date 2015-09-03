package edu.siue.smp;

import java.util.UUID;

public abstract class Event {
    private final UUID id; 

    public UUID getId() {
        return id;
    }
    
    public Event () {
        this.id = UUID.randomUUID();
    }

}



