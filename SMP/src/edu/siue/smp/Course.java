package edu.siue.smp;

import java.util.ArrayList;

public class Course extends Event {

    private String crn;
    private String dept;
    private String crs;
    private String sec;
    private ArrayList<String> days;
    private int start;
    private int end;
    private String bldg;
    private ArrayList<String> instructorPreference;
    private ArrayList<TA> assignedTA;
    private Coordinator coordinator;

    public Course(int start, int end, Coordinator coordinator) {
        super();
        this.crn = "";
        this.dept = "";
        this.crs = "";
        this.sec = "";

        this.days = new ArrayList();
        this.start = start;
        this.end = end;
        this.bldg = "";
        this.instructorPreference = new ArrayList<>();
        this.assignedTA = new ArrayList<>();
        this.coordinator = coordinator;
    }

    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public Course(String crn, String dept, String crs, String sec, ArrayList<String> days,
            int start,
            int end,
            String bldg,
            ArrayList<String> instructorPreference,
            ArrayList<TA> assignedTA,
            Coordinator coordinator) {
        this.crn = crn;
        this.dept = dept;
        this.crs = crs;
        this.sec = sec;
        this.days = days;
        this.start = start;
        this.end = end;
        this.bldg = bldg;
        this.instructorPreference = instructorPreference;
        this.assignedTA = assignedTA;
        this.coordinator = coordinator;
    }

    public String getCrn() {
        return crn;
    }

    public void setCrn(String crn) {
        this.crn = crn;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public ArrayList<String> getDays() {
        return days;
    }

    public void setDays(ArrayList<String> days) {
        this.days = days;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public ArrayList<String> getInstructorPreference() {
        return instructorPreference;
    }

    public void setInstructorPreference(ArrayList<String> instructorPreference) {
        this.instructorPreference = instructorPreference;
    }

    public ArrayList<TA> getAssignedTA() {
        return assignedTA;
    }

    public void setAssignedTA(ArrayList<TA> assignedTA) {
        this.assignedTA = assignedTA;
    }

    public Coordinator getCoordinator() {
        return coordinator;
    }

    public String getBldg() {
        return bldg;
    }

    public void setBldg(String bldg) {
        this.bldg = bldg;
    }

}
