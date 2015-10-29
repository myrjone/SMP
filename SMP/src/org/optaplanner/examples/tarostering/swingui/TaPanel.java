/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.tarostering.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.optaplanner.examples.common.swingui.TangoColorFactory;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.examples.tarostering.domain.Course;
import org.optaplanner.examples.tarostering.domain.CourseAssignment;
import org.optaplanner.examples.tarostering.domain.CourseDay;
import org.optaplanner.examples.tarostering.domain.CourseType;
import org.optaplanner.examples.tarostering.domain.Ta;
import org.optaplanner.examples.tarostering.domain.TaRoster;
import org.optaplanner.examples.tarostering.domain.contract.MinMaxContractLine;

public class TaPanel extends JPanel {

    public static final int WEST_HEADER_WIDTH = 160;
    public static final int EAST_HEADER_WIDTH = 130;

    private final TaRosteringPanel taRosteringPanel;
    private List<CourseDay> courseDayList;
    private List<Course> courseList;
    private final Ta ta;

    private JLabel taLabel;
    private JButton deleteButton;
    private JPanel courseDayListPanel = null;
    private Map<CourseDay,JPanel> courseDayPanelMap;
    private Map<Course, JPanel> coursePanelMap;
    private JLabel numberOfCourseAssignmentsLabel;

    private final TaRoster taRoster;

    private final Map<CourseAssignment, JButton> courseAssignmentButtonMap = new HashMap<> ();

    public TaPanel(TaRosteringPanel taRosteringPanel, List<CourseDay> courseDayList, List<Course> courseList,
            Ta ta, TaRoster taRoster) {
        super(new BorderLayout());
        this.taRoster = taRoster;
        this.taRosteringPanel = taRosteringPanel;
        this.courseDayList = courseDayList;
        this.courseList = courseList;
        this.ta = ta;
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(1, 2, 1, 2),
                        BorderFactory.createLineBorder(Color.BLACK)),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        createUI();
    }

    public Ta getTa() {
        return ta;
    }

    private String getTaLabel() {
        return ta == null ? "Unassigned" : ta.getLabel();
    }

    public void setCourseDayListAndCourseList(List<CourseDay> courseDayList, List<Course> courseList) {
        this.courseDayList = courseDayList;
        this.courseList = courseList;
        resetCourseListPanel();
    }

    private void createUI() {
        JPanel labelAndDeletePanel = new JPanel(new BorderLayout(5, 0));
        if (ta != null) {
            JLabel taJLabel = new JLabel(taRosteringPanel.getTaIcon());

            taJLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    //String result = JOptionPane.showInputDialog(deleteButton, ta.getName());
                    new ConstraintFrame((MinMaxContractLine) taRoster.getContractLineList().get(0));
                }
            });
            labelAndDeletePanel.add(taJLabel, BorderLayout.WEST);

        }
        taLabel = new JLabel(getTaLabel());
        taLabel.setEnabled(true);
        labelAndDeletePanel.add(taLabel, BorderLayout.CENTER);
        if (ta != null) {
            JPanel deletePanel = new JPanel(new BorderLayout());

            taLabel.setToolTipText("<html>Name: " + ta.getName() + "<br/>"
                    + "Email: " + ta.getEmail()
                    + "</html>");


            deleteButton = new JButton(taRosteringPanel.getDeleteTaIcon());
            deleteButton.setToolTipText("Delete");
            deleteButton.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    taRosteringPanel.deleteTa(ta);
                }
            });
            deleteButton.setMargin(new Insets(0, 0, 0, 0));
            deletePanel.add(deleteButton, BorderLayout.NORTH);
            labelAndDeletePanel.add(deletePanel, BorderLayout.EAST);
        }
        labelAndDeletePanel.setPreferredSize(new Dimension(WEST_HEADER_WIDTH,
                (int) labelAndDeletePanel.getPreferredSize().getHeight()));
        add(labelAndDeletePanel, BorderLayout.WEST);
        resetCourseListPanel();
        numberOfCourseAssignmentsLabel = new JLabel("0 assignments", JLabel.RIGHT);
        numberOfCourseAssignmentsLabel.setPreferredSize(new Dimension(EAST_HEADER_WIDTH, 20));
        numberOfCourseAssignmentsLabel.setEnabled(false);
        add(numberOfCourseAssignmentsLabel, BorderLayout.EAST);
    }

    public void resetCourseListPanel() {
        if (courseDayListPanel != null) {
            remove(courseDayListPanel);
        }
        courseDayListPanel = new JPanel(new GridLayout(1, 0));
        courseDayPanelMap = new LinkedHashMap<>(courseDayList.size());
        for (CourseDay courseDay : courseDayList) {
            JPanel courseDayPanel = new JPanel(new GridLayout(0, 2));
            Color backgroundColor = courseDayPanel.getBackground();
            courseDayPanel.setBackground(backgroundColor);
            courseDayPanel.setEnabled(true);
            courseDayPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(TangoColorFactory.ALUMINIUM_6),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            courseDayPanelMap.put(courseDay, courseDayPanel);
            if (ta == null) {
                // TODO HACK should be in TaRosterPanel.createHeaderPanel
                JPanel wrappingCourseDayPanel = new JPanel(new BorderLayout());
                JLabel courseDayLabel = new JLabel(courseDay.getLabel(), JLabel.CENTER);
                courseDayLabel.setEnabled(courseDayPanel.isEnabled());
                wrappingCourseDayPanel.add(courseDayLabel, BorderLayout.NORTH);
                wrappingCourseDayPanel.add(courseDayPanel, BorderLayout.CENTER);
                courseDayListPanel.add(wrappingCourseDayPanel);
            } else {
                courseDayListPanel.add(courseDayPanel);
            }
        }
        coursePanelMap = new LinkedHashMap<>(courseList.size());
        for (Course course : courseList) {
            JPanel courseDayPanel = courseDayPanelMap.get(course.getCourseDay());
            JPanel coursePanel = new JPanel();
            coursePanel.setEnabled(courseDayPanel.isEnabled());
            coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.Y_AXIS));
            Color backgroundColor = courseDayPanel.getBackground();
            if (ta != null) {
                if (ta.getCourseOffRequestMap().containsKey(course)) {
                    backgroundColor = TangoColorFactory.ALUMINIUM_4;
                } else if (ta.getCourseOnRequestMap().containsKey(course)) {
                    backgroundColor = TangoColorFactory.SCARLET_1;
                }
            }
            coursePanel.setBackground(backgroundColor);
            coursePanel.setToolTipText("<html>Day: " + course.getCourseDay().getLabel() + "<br/>"
                    + "Ta: " + (ta == null ? "unassigned" : ta.getLabel())
                    + "</html>");
            coursePanelMap.put(course, coursePanel);
            courseDayPanel.add(coursePanel);
        }
        add(courseDayListPanel, BorderLayout.CENTER);
    }

    public void addCourseAssignment(CourseAssignment courseAssignment) {
        Course course = courseAssignment.getCourse();
        JPanel coursePanel = coursePanelMap.get(course);
        JButton courseAssignmentButton = new JButton(new CourseAssignmentAction(courseAssignment));
        courseAssignmentButton.setEnabled(coursePanel.isEnabled());
        courseAssignmentButton.setMargin(new Insets(0, 0, 0, 0));
        if (ta != null) {
            if (ta.getCourseOffRequestMap().containsKey(course)) {
                courseAssignmentButton.setForeground(TangoColorFactory.SCARLET_1);
            }
        }
        int colorIndex = course.getCourseType().getIndex() % TangoColorFactory.SEQUENCE_1.length;
        courseAssignmentButton.setBackground(TangoColorFactory.SEQUENCE_1[colorIndex]);
        coursePanel.add(courseAssignmentButton);
        coursePanel.repaint();
        courseAssignmentButtonMap.put(courseAssignment, courseAssignmentButton);
    }

    public void removeCourseAssignment(CourseAssignment courseAssignment) {
        JPanel coursePanel = coursePanelMap.get(courseAssignment.getCourse());
        JButton courseAssignmentButton = courseAssignmentButtonMap.remove(courseAssignment);
        coursePanel.remove(courseAssignmentButton);
        coursePanel.repaint();
    }

    public void clearCourseAssignments() {
        for (JPanel coursePanel : coursePanelMap.values()) {
            coursePanel.removeAll();
            coursePanel.repaint();
        }
        courseAssignmentButtonMap.clear();
    }

    public void update() {
        numberOfCourseAssignmentsLabel.setText(courseAssignmentButtonMap.size() + " assignments");
    }

    private class CourseAssignmentAction extends AbstractAction {

        private final CourseAssignment courseAssignment;

        CourseAssignmentAction(CourseAssignment courseAssignment) {
            super(courseAssignment.getCourse().getCourseType().getCode());
            this.courseAssignment = courseAssignment;
            Course course = courseAssignment.getCourse();
            CourseType courseType = course.getCourseType();
            // Tooltip
            putValue(SHORT_DESCRIPTION, "<html>Day: " + course.getCourseDay().getLabel() + "<br/>"
                    + "Course type: " + courseType.getLabel() + " (from " + courseType.getStartTimeString()
                    + " to " + courseType.getEndTimeString() + ")<br/>"
                    + "Ta: " + (ta == null ? "unassigned" : ta.getLabel())
                    + "</html>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<Ta> taList = taRosteringPanel.getTaRoster().getTaList();
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox taListField = new JComboBox(
                    taList.toArray(new Object[taList.size() + 1]));
            taListField.setRenderer(new LabeledComboBoxRenderer());
            taListField.setSelectedItem(courseAssignment.getTa());
            int result = JOptionPane.showConfirmDialog(TaPanel.this.getRootPane(), taListField,
                    "Select ta", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Ta toTa = (Ta) taListField.getSelectedItem();
                taRosteringPanel.moveCourseAssignmentToTa(courseAssignment, toTa);
            }
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone(); //To change body of generated methods, choose Tools | Templates.
        }

    }

}
