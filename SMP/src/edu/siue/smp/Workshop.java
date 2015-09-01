package edu.siue.smp;

import java.util.ArrayList;

public class Workshop extends Event{
    private ArrayList<String> days;
    private int start;
    private int end;
    private String bldg;
    private ArrayList<String> instructorPreference;
    private ArrayList<TA> assignedTA;
    private Coordinator coordinator;
    
    public Workshop (int start, int end, Coordinator coordinator) {
        super();
        this.days = new ArrayList();
        this.start = start;
        this.end = end;
        this.bldg = "";
        this.instructorPreference = new ArrayList<String>();
        this.assignedTA = new ArrayList<TA>();
        this.coordinator = coordinator;
    }
    
    public Workshop (ArrayList<String> days,
                        int start, 
                        int end,
                        String bldg,
                        ArrayList<String> instructorPreferencec,
                        ArrayList<TA> assignedTA,
                        Coordinator coordinator) {
        super();
        this.days = days;
        this.start = start;
        this.end = end;
        this.bldg = bldg;
        this.instructorPreference = instructorPreference;
        this.assignedTA = assignedTA;
        this.coordinator = coordinator;
    }

    public ArrayList<String> getDays() {
        return days;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getBldg() {
        return bldg;
    }

    public ArrayList<String> getInstructorPreference() {
        return instructorPreference;
    }

    public ArrayList<TA> getAssignedTA() {
        return assignedTA;
    }

    public Coordinator getCoordinator() {
        return coordinator;
    }

    public void setDays(ArrayList<String> days) {
        this.days = days;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setBldg(String bldg) {
        this.bldg = bldg;
    }

    public void setInstructorPreference(ArrayList<String> instructorPreference) {
        this.instructorPreference = instructorPreference;
    }

    public void setAssignedTA(ArrayList<TA> assignedTA) {
        this.assignedTA = assignedTA;
    }

    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }
}
