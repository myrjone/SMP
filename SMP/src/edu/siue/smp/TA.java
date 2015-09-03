package edu.siue.smp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class TA {
    private String firstName;
    private String lastName;
    private String email;
    private ArrayList<String> coursePreferences;
    private HashMap<Integer,Boolean> timeAvailable;
    private final UUID id;
    
    public TA () {
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.coursePreferences = new ArrayList<>();
        this.timeAvailable = new HashMap<>();
        this.id = UUID.randomUUID();
    }
    
    public TA (String firstName,
                String lastName,
                String email,
                ArrayList<String> coursePreferences,
                HashMap<Integer,Boolean> timeAvailable) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.coursePreferences = coursePreferences;
        this.timeAvailable = timeAvailable;
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

    public ArrayList<String> getCoursePreferences() {
        return coursePreferences;
    }

    public HashMap<Integer, Boolean> getTimeAvailable() {
        return timeAvailable;
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

    public void setCoursePreferences(ArrayList<String> coursePreferences) {
        this.coursePreferences = coursePreferences;
    }

    public void setTimeAvailable(HashMap<Integer, Boolean> timeAvailable) {
        this.timeAvailable = timeAvailable;
    }
}
