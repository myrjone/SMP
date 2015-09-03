/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.siue.smp;

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lupeter
 */
public class CourseTest {
    
    public CourseTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setCoordinator method, of class Course.
     */
    @Test
    public void testSetCoordinator() {
          System.out.println("setCoordinator");
        Coordinator coordinator = null;
        Workshop instance = new Workshop(0,0,null);
        instance.setCoordinator(coordinator);
        Coordinator expResult = null;
        Coordinator result = instance.getCoordinator();
        assertEquals(expResult,result);
    }
    

    /**
     * Test of getCrn method, of class Course.
     */
    @Test
    public void testGetCrn() {
        System.out.println("getCrn");
        Course instance = new Course(0,0,null);
        String expResult = "";
        String result = instance.getCrn();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCrn method, of class Course.
     */
    @Test
    public void testSetCrn() {
        System.out.println("setCrn");
        String crn = "";
        Course instance = new Course(0,0,null);
        int expResult = 0;
        int result = instance.getEnd();
        assertEquals(expResult,result);
    }

    /**
     * Test of getDept method, of class Course.
     */
    @Test
    public void testGetDept() {
        System.out.println("getDept");
        Course instance = new Course(0,0,null);
        String expResult = "";
        String result = instance.getDept();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDept method, of class Course.
     */
    @Test
    public void testSetDept() {
        System.out.println("setDept");
        String dept = "";
        Course instance = new Course(0,0,null);
        int expResult = 0;
        int result = instance.getEnd();
        assertEquals(expResult,result);
    }

    /**
     * Test of getCrs method, of class Course.
     */
    @Test
    public void testGetCrs() {
        System.out.println("getCrs");
        Course instance = new Course(0,0,null);
        String expResult = "";
        String result = instance.getCrs();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCrs method, of class Course.
     */
    @Test
    public void testSetCrs() {
        System.out.println("setCrs");
        String crs = "";
        Course instance = new Course(0,0,null);
        int expResult = 0;
        int result = instance.getEnd();
        assertEquals(expResult,result);
    }

    /**
     * Test of getSec method, of class Course.
     */
    @Test
    public void testGetSec() {
       System.out.println("getSec");
        Course instance = new Course(0,0,null);
        String expResult = "";
        String result = instance.getSec();
        assertEquals(expResult, result);
    }

    /**
     * Test of setSec method, of class Course.
     */
    @Test
    public void testSetSec() {
         System.out.println("setSec");
        String sec = "";
        Course instance = new Course(0,0,null);
        int expResult = 0;
        int result = instance.getEnd();
        assertEquals(expResult,result);
    }

    /**
     * Test of getDays method, of class Course.
     */
    @Test
    public void testGetDays() {
         System.out.println("getDays");
        Course instance = new Course(0,0,null);
        ArrayList<String> expResult = new ArrayList<String>();
        ArrayList<String> result = instance.getDays();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDays method, of class Course.
     */
    @Test
    public void testSetDays() {
        System.out.println("setDays");
        ArrayList<String> days = null;
        Course instance = new Course(0,0,null);
        instance.setDays(days);
        ArrayList<String> expResult = null;
        ArrayList<String> result = instance.getDays();
        assertEquals(expResult,result);
    }

    /**
     * Test of getStart method, of class Course.
     */
    @Test
    public void testGetStart() {
        System.out.println("getStart");
        Course instance = new Course(0,0,null);
        int expResult = 0;
        int result = instance.getStart();
        assertEquals(expResult, result);
    }

    /**
     * Test of setStart method, of class Course.
     */
    @Test
    public void testSetStart() {
        System.out.println("setStart");
        int start = 0;
        Course instance = new Course(0,0,null);
        instance.setStart(start);
        int expResult = 0;
        int result = instance.getStart();
        assertEquals(expResult,result);
    }

    /**
     * Test of getEnd method, of class Course.
     */
    @Test
    public void testGetEnd() {
        System.out.println("getEnd");
        Course instance = new Course(0,0,null);
        int expResult = 0;
        int result = instance.getEnd();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEnd method, of class Course.
     */
    @Test
    public void testSetEnd() {
        System.out.println("setEnd");
        int end = 0;
        Course instance = new Course(0,0,null);
        int expResult = 0;
        int result = instance.getEnd();
        assertEquals(expResult,result);
    }

    /**
     * Test of getInstructorPreference method, of class Course.
     */
    @Test
    public void testGetInstructorPreference() {
         System.out.println("getInstructorPreference");
        Course instance = new Course(0,0,null);
        ArrayList<String> expResult = new ArrayList<String>();
        ArrayList<String> result = instance.getInstructorPreference();
        assertEquals(expResult, result);
    }

    /**
     * Test of setInstructorPreference method, of class Course.
     */
    @Test
    public void testSetInstructorPreference() {
       System.out.println("setInstructorPreference");
        ArrayList<String> instructorPreference = null;
        Course instance = new Course(0,0,null);
        instance.setInstructorPreference(instructorPreference);
        ArrayList<String> expResult = null;
        ArrayList<String> result = instance.getInstructorPreference();
        assertEquals(expResult,result);
    }

    /**
     * Test of getAssignedTA method, of class Course.
     */
    @Test
    public void testGetAssignedTA() {
      System.out.println("getAssignedTA");
        Course instance = new Course(0,0,null);
        ArrayList<TA> expResult = new ArrayList<TA>();
        ArrayList<TA> result = instance.getAssignedTA();
        assertEquals(expResult, result);
    }

    /**
     * Test of setAssignedTA method, of class Course.
     */
    @Test
    public void testSetAssignedTA() {
        System.out.println("setAssignedTA");
        ArrayList<TA> assignedTA = null;
        Course instance = new Course(0,0,null);
        instance.setAssignedTA(assignedTA);
        ArrayList<TA> expResult = null;
        ArrayList<TA> result = instance.getAssignedTA();
        assertEquals(expResult,result);
    }

    /**
     * Test of getCoordinator method, of class Course.
     */
    @Test
    public void testGetCoordinator() {
        System.out.println("getCoordinator");
        Course instance = new Course(0,0,null);
        Coordinator expResult = null;
        Coordinator result = instance.getCoordinator();
        assertEquals(expResult, result);
    }
    
}
