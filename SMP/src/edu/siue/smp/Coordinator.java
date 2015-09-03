package edu.siue.smp;

import java.util.UUID;

public class Coordinator {
    private String firstName;
    private String lastName;
    private String email;
    private final UUID id;
    
    public Coordinator () {
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.id = UUID.randomUUID();
    }
    
    public Coordinator (String firstName,
                            String lastName,
                            String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.id = UUID.randomUUID();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public UUID getId() {
        return id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
