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

package org.optaplanner.examples.tarostering.persistence;

import java.io.IOException;
import java.util.List;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.tarostering.domain.CourseAssignment;
import org.optaplanner.examples.tarostering.domain.CourseType;
import org.optaplanner.examples.tarostering.domain.TaRoster;
import org.optaplanner.examples.tarostering.domain.Ta;

public class TaRosteringExporter extends AbstractTxtSolutionExporter{
    public TaRosteringExporter() {
        super(new TaRosteringDao());
    }
    
    private class SaveTxtOutputBuilder extends TxtOutputBuilder {
        
        private TaRoster taRoster;

        @Override
        public void setSolution(Solution solution) {
            taRoster = (TaRoster) solution;
        }

        @Override
        public void writeSolution() throws IOException {

            List<CourseAssignment> courseAssign = taRoster.getCourseAssignmentList();

            for (CourseAssignment ca : courseAssign) {
                CourseType courseType = ca.getCourse().getCourseType();
                bufferedWriter.write(courseType.getCode() + ",");
                bufferedWriter.write(courseType.getDepartment() + ",");
                bufferedWriter.write(courseType.getCourseNumber() + ",");
                bufferedWriter.write(courseType.getSectionNumber() + ",");
                bufferedWriter.write(ca.getCourseDay().getDayString() + ",");             
                bufferedWriter.write(courseType.getStartTimeString() + ",");              
                bufferedWriter.write(courseType.getEndTimeString() + ",");     
                bufferedWriter.write(courseType.getBuilding() + ",");
                bufferedWriter.write(courseType.getRoomNumber() + ",");
                bufferedWriter.write(courseType.getCoordinatorName());

//                List<Coordinator> coordinatorList = taRoster.getCoordinatorList();
//                for (Coordinator coord : coordinatorList){
//                    List<CourseType> coordCourseTypeList = coord.getCourseTypes();
//                    for (CourseType ct : coordCourseTypeList){
//                         if (Objects.equals(courseType.getId(), ct.getId())){
//                             bufferedWriter.write(coord.getName() + ",");
//                             break;
//                        }
//                    }
//                }

                Ta ta = ca.getTa();
                bufferedWriter.write(ta.getName() + ",");               
                bufferedWriter.write(ta.getEmail());
                bufferedWriter.write(System.getProperty("line.separator"));
            }
        }
    }

    @Override
    public TxtOutputBuilder createTxtOutputBuilder() {
        SaveTxtOutputBuilder saveTextOutputBuilder = new SaveTxtOutputBuilder();
        return saveTextOutputBuilder;
    }   
}
