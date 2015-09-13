/*
 * Copyright 2010 JBoss Inc
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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.nurserostering.domain.Ta;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.NurseRosterParametrization;
import org.optaplanner.examples.nurserostering.domain.Course;
import org.optaplanner.examples.nurserostering.domain.CourseAssignment;
import org.optaplanner.examples.nurserostering.domain.CourseDate;

public class NurseRosteringPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/optaplanner/examples/nurserostering/swingui/nurseRosteringLogo.png";

    private final ImageIcon taIcon;
    private final ImageIcon deleteTaIcon;

    private JPanel taListPanel;

    private JTextField planningWindowStartField;
    private AbstractAction advancePlanningWindowStartAction;
    private TaPanel unassignedPanel;
    private Map<Ta, TaPanel> taToPanelMap;

    public NurseRosteringPanel() {
        taIcon = new ImageIcon(getClass().getResource("ta.png"));
        deleteTaIcon = new ImageIcon(getClass().getResource("deleteTa.png"));
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        createTaListPanel();
        JPanel headerPanel = createHeaderPanel();
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(headerPanel).addComponent(taListPanel));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                .addComponent(taListPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE));
    }

    public ImageIcon getTaIcon() {
        return taIcon;
    }

    public ImageIcon getDeleteTaIcon() {
        return deleteTaIcon;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        JPanel planningWindowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        planningWindowPanel.add(new JLabel("Planning window start:"));
        planningWindowStartField = new JTextField(10);
        planningWindowStartField.setEditable(false);
        planningWindowPanel.add(planningWindowStartField);
        advancePlanningWindowStartAction = new AbstractAction("Advance 1 day into the future") {
            @Override
            public void actionPerformed(ActionEvent e) {
                advancePlanningWindowStart();
            }
        };
        advancePlanningWindowStartAction.setEnabled(false);
        planningWindowPanel.add(new JButton(advancePlanningWindowStartAction));
        headerPanel.add(planningWindowPanel, BorderLayout.WEST);
        JLabel courseTypeExplanation = new JLabel("E = Early course, L = Late course, ...");
        headerPanel.add(courseTypeExplanation, BorderLayout.CENTER);
        return headerPanel;
    }

    private void createTaListPanel() {
        taListPanel = new JPanel();
        taListPanel.setLayout(new BoxLayout(taListPanel, BoxLayout.Y_AXIS));
        unassignedPanel = new TaPanel(this, Collections.<CourseDate>emptyList(), Collections.<Course>emptyList(),
                null);
        taListPanel.add(unassignedPanel);
        taToPanelMap = new LinkedHashMap<Ta, TaPanel>();
        taToPanelMap.put(null, unassignedPanel);
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    public NurseRoster getNurseRoster() {
        return (NurseRoster) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        NurseRoster nurseRoster = (NurseRoster) solution;
        for (TaPanel taPanel : taToPanelMap.values()) {
            if (taPanel.getTa() != null) {
                taListPanel.remove(taPanel);
            }
        }
        taToPanelMap.clear();
        taToPanelMap.put(null, unassignedPanel);
        unassignedPanel.clearCourseAssignments();
        List<CourseDate> courseDateList = nurseRoster.getCourseDateList();
        List<Course> courseList = nurseRoster.getCourseList();
        unassignedPanel.setCourseDateListAndCourseList(courseDateList, courseList);
        updatePanel(nurseRoster);
        advancePlanningWindowStartAction.setEnabled(true);
        planningWindowStartField.setText(nurseRoster.getNurseRosterParametrization().getPlanningWindowStart().getLabel());
    }

    @Override
    public void updatePanel(Solution solution) {
        NurseRoster nurseRoster = (NurseRoster) solution;
        List<CourseDate> courseDateList = nurseRoster.getCourseDateList();
        List<Course> courseList = nurseRoster.getCourseList();
        Set<Ta> deadTaSet = new LinkedHashSet<Ta>(taToPanelMap.keySet());
        deadTaSet.remove(null);
        for (Ta ta : nurseRoster.getTaList()) {
            deadTaSet.remove(ta);
            TaPanel taPanel = taToPanelMap.get(ta);
            if (taPanel == null) {
                taPanel = new TaPanel(this, courseDateList, courseList, ta);
                taListPanel.add(taPanel);
                taToPanelMap.put(ta, taPanel);
            }
            taPanel.clearCourseAssignments();
        }
        unassignedPanel.clearCourseAssignments();
        for (CourseAssignment courseAssignment : nurseRoster.getCourseAssignmentList()) {
            Ta ta = courseAssignment.getTa();
            TaPanel taPanel = taToPanelMap.get(ta);
            taPanel.addCourseAssignment(courseAssignment);
        }
        for (Ta deadTa : deadTaSet) {
            TaPanel deadTaPanel = taToPanelMap.remove(deadTa);
            taListPanel.remove(deadTaPanel);
        }
        for (TaPanel taPanel : taToPanelMap.values()) {
            taPanel.update();
        }
    }

    private void advancePlanningWindowStart() {
        logger.info("Advancing planningWindowStart.");
        if (solutionBusiness.isSolving()) {
            JOptionPane.showMessageDialog(this.getTopLevelAncestor(),
                    "The GUI does not support this action yet during solving.\nOptaPlanner itself does support it.\n"
                    + "\nTerminate solving first and try again.",
                    "Unsupported in GUI", JOptionPane.ERROR_MESSAGE);
            return;
        }
        doProblemFactChange(new ProblemFactChange() {
            public void doChange(ScoreDirector scoreDirector) {
                NurseRoster nurseRoster = (NurseRoster) scoreDirector.getWorkingSolution();
                NurseRosterParametrization nurseRosterParametrization = nurseRoster.getNurseRosterParametrization();
                List<CourseDate> courseDateList = nurseRoster.getCourseDateList();
                CourseDate planningWindowStart = nurseRosterParametrization.getPlanningWindowStart();
                int windowStartIndex = courseDateList.indexOf(planningWindowStart);
                if (windowStartIndex < 0) {
                    throw new IllegalStateException("The planningWindowStart ("
                            + planningWindowStart + ") must be in the courseDateList ("
                            + courseDateList +").");
                }
                CourseDate oldLastCourseDate = courseDateList.get(courseDateList.size() - 1);
                CourseDate newCourseDate = new CourseDate();
                newCourseDate.setId(oldLastCourseDate.getId() + 1L);
                newCourseDate.setDayIndex(oldLastCourseDate.getDayIndex() + 1);
                newCourseDate.setDateString(oldLastCourseDate.determineNextDateString());
                newCourseDate.setDayOfWeek(oldLastCourseDate.getDayOfWeek().determineNextDayOfWeek());
                List<Course> refCourseList = planningWindowStart.getCourseList();
                List<Course> newCourseList = new ArrayList<Course>(refCourseList.size());
                newCourseDate.setCourseList(newCourseList);
                nurseRoster.getCourseDateList().add(newCourseDate);
                scoreDirector.afterProblemFactAdded(newCourseDate);
                Course oldLastCourse = nurseRoster.getCourseList().get(nurseRoster.getCourseList().size() - 1);
                long courseId = oldLastCourse.getId() + 1L;
                int courseIndex = oldLastCourse.getIndex() + 1;
                long courseAssignmentId = nurseRoster.getCourseAssignmentList().get(
                        nurseRoster.getCourseAssignmentList().size() - 1).getId() + 1L;
                for (Course refCourse : refCourseList) {
                    Course newCourse = new Course();
                    newCourse.setId(courseId);
                    courseId++;
                    newCourse.setCourseDate(newCourseDate);
                    newCourse.setCourseType(refCourse.getCourseType());
                    newCourse.setIndex(courseIndex);
                    courseIndex++;
                    newCourse.setRequiredTaSize(refCourse.getRequiredTaSize());
                    newCourseList.add(newCourse);
                    nurseRoster.getCourseList().add(newCourse);
                    scoreDirector.afterProblemFactAdded(newCourse);
                    for (int indexInCourse = 0; indexInCourse < newCourse.getRequiredTaSize(); indexInCourse++) {
                        CourseAssignment newCourseAssignment = new CourseAssignment();
                        newCourseAssignment.setId(courseAssignmentId);
                        courseAssignmentId++;
                        newCourseAssignment.setCourse(newCourse);
                        newCourseAssignment.setIndexInCourse(indexInCourse);
                        nurseRoster.getCourseAssignmentList().add(newCourseAssignment);
                        scoreDirector.afterEntityAdded(newCourseAssignment);
                    }
                }
                windowStartIndex++;
                CourseDate newPlanningWindowStart = courseDateList.get(windowStartIndex);
                nurseRosterParametrization.setPlanningWindowStart(newPlanningWindowStart);
                nurseRosterParametrization.setLastCourseDate(newCourseDate);
                scoreDirector.afterProblemFactChanged(nurseRosterParametrization);
            }
        }, true);
    }

    public void deleteTa(final Ta ta) {
        logger.info("Scheduling delete of ta ({}).", ta);
        doProblemFactChange(new ProblemFactChange() {
            public void doChange(ScoreDirector scoreDirector) {
                NurseRoster nurseRoster = (NurseRoster) scoreDirector.getWorkingSolution();
                // First remove the planning fact from all planning entities that use it
                for (CourseAssignment courseAssignment : nurseRoster.getCourseAssignmentList()) {
                    if (ObjectUtils.equals(courseAssignment.getTa(), ta)) {
                        scoreDirector.beforeVariableChanged(courseAssignment, "ta");
                        courseAssignment.setTa(null);
                        scoreDirector.afterVariableChanged(courseAssignment, "ta");
                    }
                }
                // A SolutionCloner does not clone problem fact lists (such as taList)
                // Shallow clone the taList so only workingSolution is affected, not bestSolution or guiSolution
                nurseRoster.setTaList(new ArrayList<Ta>(nurseRoster.getTaList()));
                // Remove it the planning fact itself
                for (Iterator<Ta> it = nurseRoster.getTaList().iterator(); it.hasNext(); ) {
                    Ta workingTa = it.next();
                    if (ObjectUtils.equals(workingTa, ta)) {
                        scoreDirector.beforeProblemFactRemoved(workingTa);
                        it.remove(); // remove from list
                        scoreDirector.beforeProblemFactRemoved(ta);
                        break;
                    }
                }
            }
        });
    }

    public void moveCourseAssignmentToTa(CourseAssignment courseAssignment, Ta toTa) {
        solutionBusiness.doChangeMove(courseAssignment, "ta", toTa);
        solverAndPersistenceFrame.resetScreen();
    }

}
