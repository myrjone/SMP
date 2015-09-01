package edu.siue.smp;

public class Course {
   private String crn;
   private String dept;
   private String crs;  
   private String sec;  
   
   
   public Course(){
   this.crn = "";
   this.dept = "";
   this.crs = "";
   this.sec = "";
   }
  public Course (String crn, String dept, String crs, String sec){
       this.crn = crn;
       this.dept = dept;
       this.crs = crs;
       this.sec = sec;
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
 
   }
   
   

