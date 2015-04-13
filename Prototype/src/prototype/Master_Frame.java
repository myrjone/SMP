/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototype;

import prototype.Faculty.Faculty_Panel;
import prototype.Courses.Courses_Panel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 *
 * @author alex
 */
public class Master_Frame extends JFrame { 
    //here are the sizes used for the different panels
    private static final Dimension frameSize = new Dimension(900, 700);
    private static final Dimension leftNavSize = new Dimension(150, 700);
    private static final Dimension rightBodySize = new Dimension(750, 700);
    
    private static JPanel rightBodyPanel;
    public static JFrame masterFrame;
    private static JPanel TA_Edit;
    
    private static void initComponents() {
        setSystemLookAndFeel();
        masterFrame = new JFrame();
        TA_Edit = new JPanel();
        
        //Button instantiation
        JButton homeButton = new JButton("Home");
        JButton scheduleButton = new JButton("Schedule");
        JButton reportsButton = new JButton("Reports");
        JButton coursesButton = new JButton("Courses");
        JButton studentsButton = new JButton("Assistants");
        JButton facultyButton = new JButton("Faculty");
        JButton settingsButton = new JButton("Settings");
        
        //Action Listeners go here
        homeButton.addActionListener((ActionEvent e) -> {
            homeButtonActionPerformed(e);
        });
        scheduleButton.addActionListener((ActionEvent e) -> {
            scheduleButtonActionPerformed(e);
        });
        reportsButton.addActionListener((ActionEvent e) -> {
            reportsButtonActionPerformed(e);
        });
        coursesButton.addActionListener((ActionEvent e) -> {
            coursesButtonActionPerformed(e);
        });
        studentsButton.addActionListener((ActionEvent e) -> {
            studentsButtonActionPerformed(e);
        });
        facultyButton.addActionListener((ActionEvent e) -> {
            facultyButtonActionPerformed(e);
        });
        settingsButton.addActionListener((ActionEvent e) -> {
            settingsButtonActionPerformed(e);
        });
        //End action Listeners
        
        /**
         * This snippet arranges the buttons vertically, centered,
         * and sizes them evenly
         */
        JPanel buttonPanel = new JPanel(new GridLayout(7, 1,20,20));
        JPanel leftNavBar = new JPanel(new GridBagLayout());
        
       
        
        leftNavBar.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        leftNavBar.setPreferredSize(leftNavSize);
        
        
        buttonPanel.add(homeButton);
        buttonPanel.add(scheduleButton);
        buttonPanel.add(reportsButton);
        buttonPanel.add(coursesButton);
        buttonPanel.add(studentsButton);
        buttonPanel.add(facultyButton);
        buttonPanel.add(settingsButton);
        leftNavBar.add(buttonPanel);
        
        rightBodyPanel = new Home_Panel();
        rightBodyPanel.setPreferredSize(rightBodySize);
        
        
        masterFrame.add(leftNavBar, BorderLayout.WEST);
        masterFrame.getContentPane().add(rightBodyPanel);
        masterFrame.pack();
        //masterFrame.setSize(frameSize);
        masterFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        masterFrame.setVisible(true);
    }    
    
    
    //Here start the action handlers for the buttons
    private static void homeButtonActionPerformed (ActionEvent evt) {
        masterFrame.remove(rightBodyPanel);
        rightBodyPanel = new Home_Panel();
        rightBodyPanel.setPreferredSize(rightBodySize);
        masterFrame.getContentPane().add(rightBodyPanel);
        masterFrame.pack();
    }
    
    private static void scheduleButtonActionPerformed (ActionEvent evt) {
        masterFrame.remove(rightBodyPanel);
        rightBodyPanel = new Schedule_Panel();
        rightBodyPanel.setPreferredSize(rightBodySize);
        masterFrame.getContentPane().add(rightBodyPanel);
        masterFrame.pack();
    }
    
    private static void reportsButtonActionPerformed (ActionEvent evt) {
        masterFrame.remove(rightBodyPanel);
        rightBodyPanel = new Reports_Panel();
        rightBodyPanel.setPreferredSize(rightBodySize);
        masterFrame.getContentPane().add(rightBodyPanel);
        masterFrame.pack();
    }
    
    private static void coursesButtonActionPerformed (ActionEvent evt) {
        masterFrame.remove(rightBodyPanel);
        rightBodyPanel = new Courses_Panel();
        rightBodyPanel.setPreferredSize(rightBodySize);
        masterFrame.getContentPane().add(rightBodyPanel);
        masterFrame.pack();
    }
    
    private static void studentsButtonActionPerformed (ActionEvent evt) {
        masterFrame.remove(rightBodyPanel);
        rightBodyPanel = new Students_Panel();
        rightBodyPanel.setPreferredSize(rightBodySize);
        masterFrame.getContentPane().add(rightBodyPanel);
        masterFrame.pack();
    }
    
    private static void facultyButtonActionPerformed (ActionEvent evt) {
        masterFrame.remove(rightBodyPanel);
        rightBodyPanel = new Faculty_Panel();
        rightBodyPanel.setPreferredSize(rightBodySize);
        masterFrame.getContentPane().add(rightBodyPanel);
        masterFrame.pack();
    }
    
    private static void settingsButtonActionPerformed (ActionEvent evt) {
        masterFrame.remove(rightBodyPanel);
        rightBodyPanel = new Settings_Panel();
        rightBodyPanel.setPreferredSize(rightBodySize);
        masterFrame.getContentPane().add(rightBodyPanel);
        masterFrame.pack();
    }
    //End action handlers
    
    private static void setSystemLookAndFeel() { 
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) { 
            System.err.println("Exception: " + ex); 
        } 
    } 
    
    public static void main(String[] args) {
        initComponents();
    }
    
}


