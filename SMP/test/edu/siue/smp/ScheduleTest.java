/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.siue.smp;

import java.time.LocalDateTime;
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
public class ScheduleTest {
    
    public ScheduleTest() {
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
     * Test of getName method, of class Schedule.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        Schedule instance = new Schedule("");
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEvents method, of class Schedule.
     */
    @Test
    public void testGetEvents() {
        System.out.println("getEvents");
        Schedule instance = new Schedule("");
        ArrayList<Event> expResult = new ArrayList<Event>();;
        ArrayList<Event> result = instance.getEvents();
        assertEquals(expResult, result);
    }

    /**
     * Test of setName method, of class Schedule.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "";
        Schedule instance = new Schedule("");
        instance.setName(name);
        String expResult = "";
        String result = instance.getName();
        assertEquals(expResult,result);
    }

    /**
     * Test of setEvents method, of class Schedule.
     */
    @Test
    public void testSetEvents() {
        System.out.println("setEvents");
        ArrayList<Event> events = new ArrayList<Event>();
        Schedule instance = new Schedule("");
        instance.setEvents(events);
        ArrayList<Event> expResult = new ArrayList<>();
        ArrayList<Event> result = instance.getEvents();
        assertEquals(expResult, result);
    }
    
}
