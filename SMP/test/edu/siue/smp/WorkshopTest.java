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
 * @author sfurlow
 */
public class WorkshopTest {
    
    public WorkshopTest() {
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
     * Test of getDays method, of class Workshop.
     */
    @Test
    public void testGetDays() {
        System.out.println("getDays");
        Workshop instance = new Workshop(0,0,null);
        ArrayList<String> expResult = new ArrayList<String>();
        ArrayList<String> result = instance.getDays();
        assertEquals(expResult, result);
    }

    /**
     * Test of getStart method, of class Workshop.
     */
    @Test
    public void testGetStart() {
        System.out.println("getStart");
        Workshop instance = new Workshop(0,0,null);
        int expResult = 0;
        int result = instance.getStart();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEnd method, of class Workshop.
     */
    @Test
    public void testGetEnd() {
        System.out.println("getEnd");
        Workshop instance = new Workshop(0,0,null);
        int expResult = 0;
        int result = instance.getEnd();
        assertEquals(expResult, result);
    }

    /**
     * Test of getBldg method, of class Workshop.
     */
    @Test
    public void testGetBldg() {
        System.out.println("getBldg");
        Workshop instance = new Workshop(0,0,null);
        String expResult = "";
        String result = instance.getBldg();
        assertEquals(expResult, result);
    }

    /**
     * Test of getInstructorPreference method, of class Workshop.
     */
    @Test
    public void testGetInstructorPreference() {
        System.out.println("getInstructorPreference");
        Workshop instance = new Workshop(0,0,null);
        ArrayList<String> expResult = new ArrayList<String>();
        ArrayList<String> result = instance.getInstructorPreference();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAssignedTA method, of class Workshop.
     */
    @Test
    public void testGetAssignedTA() {
        System.out.println("getAssignedTA");
        Workshop instance = new Workshop(0,0,null);
        ArrayList<TA> expResult = new ArrayList<TA>();
        ArrayList<TA> result = instance.getAssignedTA();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCoordinator method, of class Workshop.
     */
    @Test
    public void testGetCoordinator() {
        System.out.println("getCoordinator");
        Workshop instance = new Workshop(0,0,null);
        Coordinator expResult = null;
        Coordinator result = instance.getCoordinator();
        assertEquals(expResult, result);
    }

    /**
     * Test of setDays method, of class Workshop.
     */
    @Test
    public void testSetDays() {
        System.out.println("setDays");
        ArrayList<String> days = null;
        Workshop instance = new Workshop(0,0,null);
        instance.setDays(days);
        ArrayList<String> expResult = null;
        ArrayList<String> result = instance.getDays();
        assertEquals(expResult,result);
    }

    /**
     * Test of setStart method, of class Workshop.
     */
    @Test
    public void testSetStart() {
        System.out.println("setStart");
        int start = 0;
        Workshop instance = new Workshop(0,0,null);
        instance.setStart(start);
        int expResult = 0;
        int result = instance.getStart();
        assertEquals(expResult,result);
    }

    /**
     * Test of setEnd method, of class Workshop.
     */
    @Test
    public void testSetEnd() {
        System.out.println("setEnd");
        int end = 0;
        Workshop instance = new Workshop(0,0,null);
        int expResult = 0;
        int result = instance.getEnd();
        assertEquals(expResult,result);
    }

    /**
     * Test of setBldg method, of class Workshop.
     */
    @Test
    public void testSetBldg() {
        System.out.println("setBldg");
        String bldg = "";
        Workshop instance = new Workshop(0,0,null);
        instance.setBldg(bldg);
        String expResult = "";
        String result = instance.getBldg();
        assertEquals(expResult,result);
    }

    /**
     * Test of setInstructorPreference method, of class Workshop.
     */
    @Test
    public void testSetInstructorPreference() {
        System.out.println("setInstructorPreference");
        ArrayList<String> instructorPreference = null;
        Workshop instance = new Workshop(0,0,null);
        instance.setInstructorPreference(instructorPreference);
        ArrayList<String> expResult = null;
        ArrayList<String> result = instance.getInstructorPreference();
        assertEquals(expResult,result);
    }

    /**
     * Test of setAssignedTA method, of class Workshop.
     */
    @Test
    public void testSetAssignedTA() {
        System.out.println("setAssignedTA");
        ArrayList<TA> assignedTA = null;
        Workshop instance = new Workshop(0,0,null);
        instance.setAssignedTA(assignedTA);
        ArrayList<TA> expResult = null;
        ArrayList<TA> result = instance.getAssignedTA();
        assertEquals(expResult,result);
    }

    /**
     * Test of setCoordinator method, of class Workshop.
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
    
}
