/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.siue.smp;

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
public class CoordinatorTest {
    
    public CoordinatorTest() {
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
     * Test of getFirstName method, of class Coordinator.
     */
    @Test
    public void testGetFirstName() {
        System.out.println("getFirstName");
        Coordinator instance = new Coordinator();
        String expResult = "";
        String result = instance.getFirstName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLastName method, of class Coordinator.
     */
    @Test
    public void testGetLastName() {
        System.out.println("getLastName");
        Coordinator instance = new Coordinator();
        String expResult = "";
        String result = instance.getLastName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getEmail method, of class Coordinator.
     */
    @Test
    public void testGetEmail() {
        System.out.println("getEmail");
        Coordinator instance = new Coordinator();
        String expResult = "";
        String result = instance.getEmail();
        assertEquals(expResult, result);
    }

    /**
     * Test of setFirstName method, of class Coordinator.
     */
    @Test
    public void testSetFirstName() {
        System.out.println("setFirstName");
        String firstName = "";
        Coordinator instance = new Coordinator();
        instance.setFirstName(firstName);
        String expResult = "";
        String result = instance.getFirstName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLastName method, of class Coordinator.
     */
    @Test
    public void testSetLastName() {
        System.out.println("setLastName");
        String lastName = "";
        Coordinator instance = new Coordinator();
        instance.setLastName(lastName);
        String expResult = "";
        String result = instance.getLastName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setEmail method, of class Coordinator.
     */
    @Test
    public void testSetEmail() {
        System.out.println("setEmail");
        String email = "";
        Coordinator instance = new Coordinator();
        instance.setEmail(email);
        String expResult = "";
        String result = instance.getEmail();
        assertEquals(expResult, result);
    }
    
}
