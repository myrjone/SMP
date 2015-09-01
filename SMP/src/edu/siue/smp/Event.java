package edu.siue.smp;

import java.util.UUID;

public class Event {
    private UUID id; 

    public UUID getId() {
        return id;
    }
    
public Event () {
this.id = UUID.randomUUID();
}

}



