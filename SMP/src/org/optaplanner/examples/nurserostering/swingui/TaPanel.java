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

package org.optaplanner.examples.nurserostering.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import org.optaplanner.examples.nurserostering.domain.Ta;
import org.optaplanner.examples.nurserostering.domain.Course;
import org.optaplanner.examples.nurserostering.domain.CourseAssignment;
import org.optaplanner.examples.nurserostering.domain.CourseDate;
import org.optaplanner.examples.nurserostering.domain.CourseType;
import org.optaplanner.examples.nurserostering.domain.WeekendDefinition;

public class TaPanel extends JPanel {

    public static final int WEST_HEADER_WIDTH = 160;
    public static final int EAST_HEADER_WIDTH = 130;

    private final NurseRosteringPanel nurseRosteringPanel;
    private List<CourseDate> courseDateList;
    private List<Course> courseList;
    private Ta ta;

    private JLabel taLabel;
    private JButton deleteButton;
    private JPanel courseDateListPanel = null;
    private Map<CourseDate,JPanel> courseDatePanelMap;
    private Map<Course, JPanel> coursePanelMap;
    private JLabel numberOfCourseAssignmentsLabel;

    private Map<CourseAssignment, JButton> courseAssignmentButtonMap = new HashMap<CourseAssignment, JButton> ();

    public TaPanel(NurseRosteringPanel nurseRosteringPanel, List<CourseDate> courseDateList, List<Course> courseList,
            Ta ta) {
        super(new BorderLayout());
        this.nurseRosteringPanel = nurseRosteringPanel;
        this.courseDateList = courseDateList;
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

    public void setCourseDateListAndCourseList(List<CourseDate> courseDateList, List<Course> courseList) {
        this.courseDateList = courseDateList;
        this.courseList = courseList;
        resetCourseListPanel();
    }

    private void createUI() {
        JPanel labelAndDeletePanel = new JPanel(new BorderLayout(5, 0));
        if (ta != null) {
            labelAndDeletePanel.add(new JLabel(nurseRosteringPanel.getTaIcon()), BorderLayout.WEST);
        }
        taLabel = new JLabel(getTaLabel());
        taLabel.setEnabled(false);
        labelAndDeletePanel.add(taLabel, BorderLayout.CENTER);
        if (ta != null) {
            JPanel deletePanel = new JPanel(new BorderLayout());
            deleteButton = new JButton(nurseRosteringPanel.getDeleteTaIcon());
            deleteButton.setToolTipText("Delete");
            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    nurseRosteringPanel.deleteTa(ta);
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
        if (courseDateListPanel != null) {
            remove(courseDateListPanel);
        }
        WeekendDefinition weekendDefinition = (ta == null) ? WeekendDefinition.SATURDAY_SUNDAY
                : ta.getContract().getWeekendDefinition();
        courseDateListPanel = new JPanel(new GridLayout(1, 0));
        courseDatePanelMap = new LinkedHashMap<CourseDate, JPanel>(courseDateList.size());
        for (CourseDate courseDate : courseDateList) {
            JPanel courseDatePanel = new JPanel(new GridLayout(1, 0));
            Color backgroundColor = weekendDefinition.isWeekend(courseDate.getDayOfWeek())
                    ? TangoColorFactory.ALUMINIUM_2 : courseDatePanel.getBackground();
            if (ta != null) {
                if (ta.getDayOffRequestMap().containsKey(courseDate)) {
                    backgroundColor = TangoColorFactory.ALUMINIUM_4;
                } else if (ta.getDayOnRequestMap().containsKey(courseDate)) {
                    backgroundColor = TangoColorFactory.SCARLET_1;
                }
            }
            courseDatePanel.setBackground(backgroundColor);
            boolean inPlanningWindow = nurseRosteringPanel.getNurseRoster().getNurseRosterParametrization()
                    .isInPlanningWindow(courseDate);
            courseDatePanel.setEnabled(inPlanningWindow);
            courseDatePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(inPlanningWindow ? TangoColorFactory.ALUMINIUM_6 : TangoColorFactory.ALUMINIUM_3),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            courseDatePanelMap.put(courseDate, courseDatePanel);
            if (ta == null) {
                // TODO HACK should be in NurseRosterPanel.createHeaderPanel
                JPanel wrappingCourseDatePanel = new JPanel(new BorderLayout());
                JLabel courseDateLabel = new JLabel(courseDate.getLabel(), JLabel.CENTER);
                courseDateLabel.setEnabled(courseDatePanel.isEnabled());
                wrappingCourseDatePanel.add(courseDateLabel, BorderLayout.NORTH);
                wrappingCourseDatePanel.add(courseDatePanel, BorderLayout.CENTER);
                courseDateListPanel.add(wrappingCourseDatePanel);
            } else {
                courseDateListPanel.add(courseDatePanel);
            }
        }
        coursePanelMap = new LinkedHashMap<Course, JPanel>(courseList.size());
        for (Course course : courseList) {
            JPanel courseDatePanel = courseDatePanelMap.get(course.getCourseDate());
            JPanel coursePanel = new JPanel();
            coursePanel.setEnabled(courseDatePanel.isEnabled());
            coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.Y_AXIS));
            Color backgroundColor = courseDatePanel.getBackground();
            if (ta != null) {
                if (ta.getCourseOffRequestMap().containsKey(course)) {
                    backgroundColor = TangoColorFactory.ALUMINIUM_4;
                } else if (ta.getCourseOnRequestMap().containsKey(course)) {
                    backgroundColor = TangoColorFactory.SCARLET_1;
                }
            }
            coursePanel.setBackground(backgroundColor);
            coursePanel.setToolTipText("<html>Date: " + course.getCourseDate().getLabel() + "<br/>"
                    + "Ta: " + (ta == null ? "unassigned" : ta.getLabel())
                    + "</html>");
            coursePanelMap.put(course, coursePanel);
            courseDatePanel.add(coursePanel);
        }
        add(courseDateListPanel, BorderLayout.CENTER);
    }

    public void addCourseAssignment(CourseAssignment courseAssignment) {
        Course course = courseAssignment.getCourse();
        JPanel coursePanel = coursePanelMap.get(course);
        JButton courseAssignmentButton = new JButton(new CourseAssignmentAction(courseAssignment));
        courseAssignmentButton.setEnabled(coursePanel.isEnabled());
        courseAssignmentButton.setMargin(new Insets(0, 0, 0, 0));
        if (ta != null) {
            if (ta.getDayOffRequestMap().containsKey(course.getCourseDate())
                    || ta.getCourseOffRequestMap().containsKey(course)) {
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

        private CourseAssignment courseAssignment;

        public CourseAssignmentAction(CourseAssignment courseAssignment) {
            super(courseAssignment.getCourse().getCourseType().getCode());
            this.courseAssignment = courseAssignment;
            Course course = courseAssignment.getCourse();
            CourseType courseType = course.getCourseType();
            // Tooltip
            putValue(SHORT_DESCRIPTION, "<html>Date: " + course.getCourseDate().getLabel() + "<br/>"
                    + "Course type: " + courseType.getLabel() + " (from " + courseType.getStartTimeString()
                    + " to " + courseType.getEndTimeString() + ")<br/>"
                    + "Ta: " + (ta == null ? "unassigned" : ta.getLabel())
                    + "</html>");
        }

        public void actionPerformed(ActionEvent e) {
            List<Ta> taList = nurseRosteringPanel.getNurseRoster().getTaList();
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox taListField = new JComboBox(
                    taList.toArray(new Object[taList.size() + 1]));
            taListField.setRenderer(new LabeledComboBoxRenderer());
            taListField.setSelectedItem(courseAssignment.getTa());
            int result = JOptionPane.showConfirmDialog(TaPanel.this.getRootPane(), taListField,
                    "Select ta", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Ta toTa = (Ta) taListField.getSelectedItem();
                nurseRosteringPanel.moveCourseAssignmentToTa(courseAssignment, toTa);
            }
        }

    }
    
}
