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

package org.optaplanner.examples.tarostering.swingui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.tarostering.domain.Course;
import org.optaplanner.examples.tarostering.domain.CourseAssignment;
import org.optaplanner.examples.tarostering.domain.CourseDay;
import org.optaplanner.examples.tarostering.domain.Ta;
import org.optaplanner.examples.tarostering.domain.TaRoster;

public class TaRosteringPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/optaplanner/examples/tarostering/swingui/taRosteringLogo.png";

    private final ImageIcon taIcon;
    private final ImageIcon deleteTaIcon;

    private JPanel taListPanel;

    private TaPanel unassignedPanel;
    private Map<Ta, TaPanel> taToPanelMap;

    public TaRosteringPanel() {
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
        JLabel courseTypeExplanation = new JLabel("Hover over items to get more information");
        headerPanel.add(courseTypeExplanation, BorderLayout.CENTER);
        return headerPanel;
    }

    private void createTaListPanel() {
        taListPanel = new JPanel();
        taListPanel.setLayout(new BoxLayout(taListPanel, BoxLayout.Y_AXIS));
        unassignedPanel = new TaPanel(this, Collections.<CourseDay>emptyList(), Collections.<Course>emptyList(),
                null, (TaRoster) null);
        taListPanel.add(unassignedPanel);
        taToPanelMap = new LinkedHashMap<>();
        taToPanelMap.put(null, unassignedPanel);
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    public TaRoster getTaRoster() {
        return (TaRoster) solutionBusiness.getSolution();
    }

    @Override
    public void resetPanel(Solution solution) {
        TaRoster taRoster = (TaRoster) solution;
        for (TaPanel taPanel : taToPanelMap.values()) {
            if (taPanel.getTa() != null) {
                taListPanel.remove(taPanel);
            }
        }
        taToPanelMap.clear();
        taToPanelMap.put(null, unassignedPanel);
        unassignedPanel.clearCourseAssignments();
        List<CourseDay> courseDayList = taRoster.getCourseDayList();
        List<Course> courseList = taRoster.getCourseList();
        unassignedPanel.setCourseDayListAndCourseList(courseDayList, courseList);
        updatePanel(taRoster);
    }

    @Override
    public void updatePanel(Solution solution) {
        TaRoster taRoster = (TaRoster) solution;
        List<CourseDay> courseDayList = taRoster.getCourseDayList();
        List<Course> courseList = taRoster.getCourseList();
        Set<Ta> deadTaSet = new LinkedHashSet<>(taToPanelMap.keySet());
        deadTaSet.remove(null);
        for (Ta ta : taRoster.getTaList()) {
            deadTaSet.remove(ta);
            TaPanel taPanel = taToPanelMap.get(ta);
            if (taPanel == null) {
                taPanel = new TaPanel(this, courseDayList, courseList, ta, (TaRoster) solutionBusiness.getSolution());
                taListPanel.add(taPanel);
                taToPanelMap.put(ta, taPanel);
            }
            taPanel.clearCourseAssignments();
        }
        unassignedPanel.clearCourseAssignments();
        for (CourseAssignment courseAssignment : taRoster.getCourseAssignmentList()) {
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

    public void deleteTa(final Ta ta) {
        logger.info("Scheduling delete of ta ({}).", ta);
        doProblemFactChange(new ProblemFactChange() {
            @Override
            public void doChange(ScoreDirector scoreDirector) {
                TaRoster taRoster = (TaRoster) scoreDirector.getWorkingSolution();
                // First remove the planning fact from all planning entities that use it
                for (CourseAssignment courseAssignment : taRoster.getCourseAssignmentList()) {
                    if (ObjectUtils.equals(courseAssignment.getTa(), ta)) {
                        scoreDirector.beforeVariableChanged(courseAssignment, "ta");
                        courseAssignment.setTa(null);
                        scoreDirector.afterVariableChanged(courseAssignment, "ta");
                    }
                }
                // A SolutionCloner does not clone problem fact lists (such as taList)
                // Shallow clone the taList so only workingSolution is affected, not bestSolution or guiSolution
                taRoster.setTaList(new ArrayList<>(taRoster.getTaList()));
                // Remove it the planning fact itself
                for (Iterator<Ta> it = taRoster.getTaList().iterator(); it.hasNext(); ) {
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
