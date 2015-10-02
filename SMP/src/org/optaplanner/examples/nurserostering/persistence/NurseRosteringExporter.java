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

package org.optaplanner.examples.nurserostering.persistence;

import java.io.IOException;
import org.jdom2.Element;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractXmlSolutionExporter;
import org.optaplanner.examples.nurserostering.domain.Course;
import org.optaplanner.examples.nurserostering.domain.CourseAssignment;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;

public class NurseRosteringExporter extends AbstractXmlSolutionExporter {

    public static void main(String[] args) {
        new NurseRosteringExporter().convertAll();
    }

    public NurseRosteringExporter() {
        super(new NurseRosteringDao());
    }

    @Override
    public XmlOutputBuilder createXmlOutputBuilder() {
        return new NurseRosteringOutputBuilder();
    }

    public static class NurseRosteringOutputBuilder extends XmlOutputBuilder {

        private NurseRoster nurseRoster;

        @Override
        public void setSolution(Solution solution) {
            nurseRoster = (NurseRoster) solution;
        }

        @Override
        public void writeSolution() throws IOException {
            Element solutionElement = new Element("Solution");
            document.setRootElement(solutionElement);

            Element schedulingPeriodIDElement = new Element("SchedulingPeriodID");
            schedulingPeriodIDElement.setText(nurseRoster.getCode());
            solutionElement.addContent(schedulingPeriodIDElement);

            Element competitorElement = new Element("Competitor");
            competitorElement.setText("Geoffrey De Smet with OptaPlanner");
            solutionElement.addContent(competitorElement);

            Element softConstraintsPenaltyElement = new Element("SoftConstraintsPenalty");
            softConstraintsPenaltyElement.setText(Integer.toString(nurseRoster.getScore().getSoftScore()));
            solutionElement.addContent(softConstraintsPenaltyElement);

            for (CourseAssignment courseAssignment : nurseRoster.getCourseAssignmentList()) {
                Course course = courseAssignment.getCourse();
                if (course != null) {
                    Element assignmentElement = new Element("Assignment");
                    solutionElement.addContent(assignmentElement);

                    Element dayElement = new Element("Day");
                    dayElement.setText(course.getCourseDay().getDayString());
                    assignmentElement.addContent(dayElement);

                    Element taElement = new Element("Ta");
                    taElement.setText(courseAssignment.getTa().getCode());
                    assignmentElement.addContent(taElement);

                    Element courseTypeElement = new Element("CourseType");
                    courseTypeElement.setText(course.getCourseType().getCode());
                    assignmentElement.addContent(courseTypeElement);
                }
            }
        }
    }

}
