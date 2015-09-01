/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.siue.smp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
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
public class TATest {
    
    public TATest() {
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
     * Test of getFirstName method, of class TA.
     */
    @Test
    public void testGetFirstName() {
        System.out.println("getFirstName");
        TA instance = new TA();
        String expResult = "";
        String result = instance.getFirstName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLastName method, of class TA.
     */
    @Test
    public void testGetLastName() {
        System.out.println("getLastName");
        TA instance = new TA();
        String expResult = "";
        String result = instance.getLastName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEmail method, of class TA.
     */
    @Test
    public void testGetEmail() {
        System.out.println("getEmail");
        TA instance = new TA();
        String expResult = "";
        String result = instance.getEmail();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCoursePreferences method, of class TA.
     */
    @Test
    public void testGetCoursePreferences() {
        System.out.println("getCoursePreferences");
        TA instance = new TA();
        ArrayList<String> expResult = new ArrayList<String>();
        ArrayList<String> result = instance.getCoursePreferences();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTimeAvailable method, of class TA.
     */
    @Test
    public void testGetTimeAvailable() {
        System.out.println("getTimeAvailable");
        TA instance = new TA();
        HashMap<Integer, Boolean> expResult = new HashMap<Integer, Boolean>();
        HashMap<Integer, Boolean> result = instance.getTimeAvailable();
        assertEquals(expResult, result);
    }

    /**
     * Test of setFirstName method, of class TA.
     */
    @Test
    public void testSetFirstName() {
        System.out.println("setFirstName");
        String firstName = "";
        TA instance = new TA();
        instance.setFirstName(firstName);
        String expResult = "";
        String result = instance.getFirstName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLastName method, of class TA.
     */
    @Test
    public void testSetLastName() {
        System.out.println("setLastName");
        String lastName = "";
        TA instance = new TA();
        instance.setLastName(lastName);
        String expResult = "";
        String result = instance.getLastName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEmail method, of class TA.
     */
    @Test
    public void testSetEmail() {
        System.out.println("setEmail");
        String email = "";
        TA instance = new TA();
        instance.setEmail(email);
        String expResult = "";
        String result = instance.getFirstName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCoursePreferences method, of class TA.
     */
    @Test
    public void testSetCoursePreferences() {
        System.out.println("setCoursePreferences");
        ArrayList<String> coursePreferences = null;
        TA instance = new TA();
        instance.setCoursePreferences(coursePreferences);
        ArrayList<String> expResult = null;
        ArrayList<String> result = instance.getCoursePreferences();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTimeAvailable method, of class TA.
     */
    @Test
    public void testSetTimeAvailable() {
        System.out.println("setTimeAvailable");
        HashMap<Integer, Boolean> timeAvailable = null;
        TA instance = new TA();
        instance.setTimeAvailable(timeAvailable);
        HashMap<Integer, Boolean> expResult = null;
        HashMap<Integer, Boolean> result = instance.getTimeAvailable();
        assertEquals(expResult, result);
    }
    
}
