/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prototype;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 *
 * @author alex
 */
public class Master_Frame<T> extends JFrame { 
    //here are the sizes used for the different panels
    private static final Dimension frameSize = new Dimension(700, 700);
    private static final Dimension leftNavSize = new Dimension(150, 700);
    private static final Dimension rightBodySize = new Dimension(500, 700);
    
    private static JPanel rightBodyPanel;
    private static JFrame masterFrame;
    
    private static void initComponents() {
        setSystemLookAndFeel();
        masterFrame = new JFrame();
        
        //Button instantiation
        JButton homeButton = new JButton("Home");
        JButton scheduleButton = new JButton("Schedule");
        JButton reportsButton = new JButton("Reports");
        JButton coursesButton = new JButton("Courses");
        JButton studentsButton = new JButton("Students");
        JButton facultyButton = new JButton("Faculty");
        JButton adminButton = new JButton("Admin");
        
        //Action Listeners go here
        scheduleButton.addActionListener((ActionEvent e) -> {
            scheduleButtonActionPerformed(e);
        });
        //End action Listeners
        
        
        JPanel buttonPanel = new JPanel(new GridLayout(7, 1));
        JPanel leftNavBar = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        leftNavBar.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
        leftNavBar.setPreferredSize(leftNavSize);
        
        buttonPanel.add(homeButton,gbc);
        buttonPanel.add(scheduleButton, gbc);
        buttonPanel.add(reportsButton, gbc);
        buttonPanel.add(coursesButton, gbc);
        buttonPanel.add(studentsButton, gbc);
        buttonPanel.add(facultyButton, gbc);
        buttonPanel.add(adminButton, gbc);
        leftNavBar.add(buttonPanel, gbc);
        
        masterFrame.add(leftNavBar, BorderLayout.WEST);
        masterFrame.setSize(frameSize);
        masterFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        masterFrame.setVisible(true);
    }    
    
    private static void scheduleButtonActionPerformed (ActionEvent evt) {
        rightBodyPanel = new Schedule_Panel();
        rightBodyPanel.setPreferredSize(rightBodySize);
        masterFrame.getContentPane().add(rightBodyPanel);
        masterFrame.pack();
    }
    
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


