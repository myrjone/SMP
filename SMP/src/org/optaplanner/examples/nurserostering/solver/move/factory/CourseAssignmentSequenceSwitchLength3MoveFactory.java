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

package org.optaplanner.examples.nurserostering.solver.move.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.optaplanner.core.impl.heuristic.move.CompositeMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.examples.nurserostering.domain.CourseAssignment;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.Ta;
import org.optaplanner.examples.nurserostering.solver.drools.TaWorkSequence;
import org.optaplanner.examples.nurserostering.solver.move.TaChangeMove;

public class CourseAssignmentSequenceSwitchLength3MoveFactory implements MoveListFactory<NurseRoster> {

    public List<Move> createMoveList(NurseRoster nurseRoster) {
        List<Ta> taList = nurseRoster.getTaList();
        // This code assumes the courseAssignmentList is sorted
        // Filter out every immovable CourseAssignment
        List<CourseAssignment> courseAssignmentList = new ArrayList<>(
                nurseRoster.getCourseAssignmentList());

        // Hash the assignments per ta
        Map<Ta, List<AssignmentSequence>> taToAssignmentSequenceListMap
                = new HashMap<>(taList.size());
        int assignmentSequenceCapacity = nurseRoster.getCourseDateList().size() + 1 / 2;
        for (Ta ta : taList) {
            taToAssignmentSequenceListMap.put(ta,
                    new ArrayList<AssignmentSequence>(assignmentSequenceCapacity));
        }
        for (CourseAssignment courseAssignment : courseAssignmentList) {
            Ta ta = courseAssignment.getTa();
            List<AssignmentSequence> assignmentSequenceList = taToAssignmentSequenceListMap.get(ta);
            if (assignmentSequenceList.isEmpty()) {
                AssignmentSequence assignmentSequence = new AssignmentSequence(courseAssignment);
                assignmentSequenceList.add(assignmentSequence);
            } else {
                AssignmentSequence lastAssignmentSequence = assignmentSequenceList // getLast()
                        .get(assignmentSequenceList.size() - 1);
                if (lastAssignmentSequence.belongsHere(courseAssignment)) {
                    lastAssignmentSequence.add(courseAssignment);
                } else {
                    AssignmentSequence assignmentSequence = new AssignmentSequence(courseAssignment);
                    assignmentSequenceList.add(assignmentSequence);
                }
            }
        }

        // The create the move list
        List<Move> moveList = new ArrayList<>();
        // For every 2 distinct tas
        for (ListIterator<Ta> leftTaIt = taList.listIterator(); leftTaIt.hasNext();) {
            Ta leftTa = leftTaIt.next();
            List<AssignmentSequence> leftAssignmentSequenceList
                    = taToAssignmentSequenceListMap.get(leftTa);
            for (ListIterator<Ta> rightTaIt = taList.listIterator(leftTaIt.nextIndex());
                    rightTaIt.hasNext();) {
                Ta rightTa = rightTaIt.next();
                List<AssignmentSequence> rightAssignmentSequenceList
                        = taToAssignmentSequenceListMap.get(rightTa);

                final int SWITCH_LENGTH = 3;
                for (AssignmentSequence leftAssignmentSequence : leftAssignmentSequenceList) {
                    List<CourseAssignment> leftCourseAssignmentList = leftAssignmentSequence.getCourseAssignmentList();
                    for (int leftIndex = 0; leftIndex <= leftCourseAssignmentList.size() - SWITCH_LENGTH; leftIndex++) {

                        for (AssignmentSequence rightAssignmentSequence : rightAssignmentSequenceList) {
                            List<CourseAssignment> rightCourseAssignmentList = rightAssignmentSequence.getCourseAssignmentList();
                            for (int rightIndex = 0; rightIndex <= rightCourseAssignmentList.size() - SWITCH_LENGTH; rightIndex++) {

                                List<Move> subMoveList = new ArrayList<>(SWITCH_LENGTH * 2);
                                for (CourseAssignment leftCourseAssignment : leftCourseAssignmentList
                                        .subList(leftIndex, leftIndex + SWITCH_LENGTH)) {
                                    subMoveList.add(new TaChangeMove(leftCourseAssignment, rightTa));
                                }
                                for (CourseAssignment rightCourseAssignment : rightCourseAssignmentList
                                        .subList(rightIndex, rightIndex + SWITCH_LENGTH)) {
                                    subMoveList.add(new TaChangeMove(rightCourseAssignment, leftTa));
                                }
                                moveList.add(CompositeMove.buildMove(subMoveList));
                            }
                        }
                    }
                }
            }
        }
        return moveList;
    }

    /**
     * TODO DRY with {@link TaWorkSequence}
     */
    private static class AssignmentSequence {

        private List<CourseAssignment> courseAssignmentList;
        private int firstDayIndex;
        private int lastDayIndex;

        private AssignmentSequence(CourseAssignment courseAssignment) {
            courseAssignmentList = new ArrayList<>();
            courseAssignmentList.add(courseAssignment);
            firstDayIndex = courseAssignment.getCourseDateDayIndex();
            lastDayIndex = firstDayIndex;
        }

        public List<CourseAssignment> getCourseAssignmentList() {
            return courseAssignmentList;
        }

        public int getFirstDayIndex() {
            return firstDayIndex;
        }

        public int getLastDayIndex() {
            return lastDayIndex;
        }

        private void add(CourseAssignment courseAssignment) {
            courseAssignmentList.add(courseAssignment);
            int dayIndex = courseAssignment.getCourseDateDayIndex();
            if (dayIndex < lastDayIndex) {
                throw new IllegalStateException("The courseAssignmentList is expected to be sorted by courseDate.");
            }
            lastDayIndex = dayIndex;
        }

        private boolean belongsHere(CourseAssignment courseAssignment) {
            return courseAssignment.getCourseDateDayIndex() <= (lastDayIndex + 1);
        }

    }

}
