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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.optaplanner.core.impl.heuristic.move.CompositeMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.examples.nurserostering.domain.CourseAssignment;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.Ta;
import org.optaplanner.examples.nurserostering.solver.drools.TaWorkSequence;
import org.optaplanner.examples.nurserostering.solver.move.TaMultipleChangeMove;

public class CourseAssignmentPillarPartSwapMoveFactory implements MoveListFactory<NurseRoster> {

    @Override
    public List<Move> createMoveList(NurseRoster nurseRoster) {
        List<Ta> taList = nurseRoster.getTaList();
        // This code assumes the courseAssignmentList is sorted
        // Filter out every immovable CourseAssignment
        List<CourseAssignment> courseAssignmentList = new ArrayList<>(
                nurseRoster.getCourseAssignmentList());

        // Hash the assignments per ta
        Map<Ta, List<AssignmentSequence>> taToAssignmentSequenceListMap
                = new HashMap<>(taList.size());
        int assignmentSequenceCapacity = nurseRoster.getCourseDayList().size() + 1 / 2;
        for (Ta ta : taList) {
            taToAssignmentSequenceListMap.put(ta,
                    new ArrayList<AssignmentSequence>(assignmentSequenceCapacity));
        }
        for (CourseAssignment courseAssignment : courseAssignmentList) {
            Ta ta = courseAssignment.getTa();
            List<AssignmentSequence> assignmentSequenceList = taToAssignmentSequenceListMap.get(ta);
            if (assignmentSequenceList.isEmpty()) {
                AssignmentSequence assignmentSequence = new AssignmentSequence(ta, courseAssignment);
                assignmentSequenceList.add(assignmentSequence);
            } else {
                AssignmentSequence lastAssignmentSequence = assignmentSequenceList // getLast()
                        .get(assignmentSequenceList.size() - 1);
                if (lastAssignmentSequence.belongsHere(courseAssignment)) {
                    lastAssignmentSequence.add(courseAssignment);
                } else {
                    AssignmentSequence assignmentSequence = new AssignmentSequence(ta, courseAssignment);
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
                List<AssignmentSequence> rightAssignmentSequenceList = taToAssignmentSequenceListMap.get(
                        rightTa);

                LowestDayIndexAssignmentSequenceIterator lowestIt = new LowestDayIndexAssignmentSequenceIterator(
                        leftAssignmentSequenceList, rightAssignmentSequenceList);
                // For every pillar part duo
                while (lowestIt.hasNext()) {
                    AssignmentSequence pillarPartAssignmentSequence = lowestIt.next();
                    // Note: the initialCapacity is probably to high,
                    // which is bad for memory, but the opposite is bad for performance (which is worse)
                    List<Move> moveListByPillarPartDuo = new ArrayList<>(
                            leftAssignmentSequenceList.size() + rightAssignmentSequenceList.size());
                    int lastDayIndex = pillarPartAssignmentSequence.getLastDayIndex();
                    Ta otherTa;
                    int leftMinimumFirstDayIndex = Integer.MIN_VALUE;
                    int rightMinimumFirstDayIndex = Integer.MIN_VALUE;
                    if (lowestIt.isLastNextWasLeft()) {
                        otherTa = rightTa;
                        leftMinimumFirstDayIndex = lastDayIndex;
                    } else {
                        otherTa = leftTa;
                        rightMinimumFirstDayIndex = lastDayIndex;
                    }
                    moveListByPillarPartDuo.add(new TaMultipleChangeMove(
                            pillarPartAssignmentSequence.getTa(),
                            pillarPartAssignmentSequence.getCourseAssignmentList(),
                            otherTa));
                    // For every AssignmentSequence in that pillar part duo
                    while (lowestIt.hasNextWithMaximumFirstDayIndexes(
                            leftMinimumFirstDayIndex, rightMinimumFirstDayIndex)) {
                        pillarPartAssignmentSequence = lowestIt.next();
                        lastDayIndex = pillarPartAssignmentSequence.getLastDayIndex();
                        if (lowestIt.isLastNextWasLeft()) {
                            otherTa = rightTa;
                            leftMinimumFirstDayIndex = Math.max(leftMinimumFirstDayIndex, lastDayIndex);
                        } else {
                            otherTa = leftTa;
                            rightMinimumFirstDayIndex = Math.max(rightMinimumFirstDayIndex, lastDayIndex);
                        }
                        moveListByPillarPartDuo.add(new TaMultipleChangeMove(
                                pillarPartAssignmentSequence.getTa(),
                                pillarPartAssignmentSequence.getCourseAssignmentList(),
                                otherTa));
                    }
                    moveList.add(CompositeMove.buildMove(moveListByPillarPartDuo));
                }
            }
        }
        return moveList;
    }

    /**
     * TODO DRY with {@link TaWorkSequence}
     */
    private static class AssignmentSequence {

        private final Ta ta;
        private final List<CourseAssignment> courseAssignmentList;
        private final int firstDayIndex;
        private int lastDayIndex;

        private AssignmentSequence(Ta ta, CourseAssignment courseAssignment) {
            this.ta = ta;
            courseAssignmentList = new ArrayList<>();
            courseAssignmentList.add(courseAssignment);
            firstDayIndex = courseAssignment.getCourseDayDayIndex();
            lastDayIndex = firstDayIndex;
        }

        public Ta getTa() {
            return ta;
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
            int dayIndex = courseAssignment.getCourseDayDayIndex();
            if (dayIndex < lastDayIndex) {
                throw new IllegalStateException("The courseAssignmentList is expected to be sorted by courseDay.");
            }
            lastDayIndex = dayIndex;
        }

        private boolean belongsHere(CourseAssignment courseAssignment) {
            return courseAssignment.getCourseDayDayIndex() <= (lastDayIndex + 1);
        }

    }

    private static class LowestDayIndexAssignmentSequenceIterator implements Iterator<AssignmentSequence> {

        private final Iterator<AssignmentSequence> leftIterator;
        private final Iterator<AssignmentSequence> rightIterator;

        private boolean leftHasNext = true;
        private boolean rightHasNext = true;

        private AssignmentSequence nextLeft;
        private AssignmentSequence nextRight;

        private boolean lastNextWasLeft;

        LowestDayIndexAssignmentSequenceIterator(
                List<AssignmentSequence> leftAssignmentList, List<AssignmentSequence> rightAssignmentList) {
            // Buffer the nextLeft and nextRight
            leftIterator = leftAssignmentList.iterator();
            if (leftIterator.hasNext()) {
                nextLeft = leftIterator.next();
            } else {
                leftHasNext = false;
                nextLeft = null;
            }
            rightIterator = rightAssignmentList.iterator();
            if (rightIterator.hasNext()) {
                nextRight = rightIterator.next();
            } else {
                rightHasNext = false;
                nextRight = null;
            }
        }

        @Override
        public boolean hasNext() {
            return leftHasNext || rightHasNext;
        }

        public boolean hasNextWithMaximumFirstDayIndexes(
                int leftMinimumFirstDayIndex, int rightMinimumFirstDayIndex) {
            if (!hasNext()) {
                return false;
            }
            boolean nextIsLeft = nextIsLeft();
            if (nextIsLeft) {
                int firstDayIndex = nextLeft.getFirstDayIndex();
                // It should not be conflict in the same pillar and it should be in conflict with the other pillar
                return firstDayIndex > leftMinimumFirstDayIndex && firstDayIndex <= rightMinimumFirstDayIndex;
            } else {
                int firstDayIndex = nextRight.getFirstDayIndex();
                // It should not be conflict in the same pillar and it should be in conflict with the other pillar
                return firstDayIndex > rightMinimumFirstDayIndex && firstDayIndex <= leftMinimumFirstDayIndex;
            }
        }

        @Override
        public AssignmentSequence next() {
            lastNextWasLeft = nextIsLeft();
            // Buffer the nextLeft or nextRight
            AssignmentSequence lowest;
            if (lastNextWasLeft) {
                lowest = nextLeft;
                if (leftIterator.hasNext()) {
                    nextLeft = leftIterator.next();
                } else {
                    leftHasNext = false;
                    nextLeft = null;
                }
            } else {
                lowest = nextRight;
                if (rightIterator.hasNext()) {
                    nextRight = rightIterator.next();
                } else {
                    rightHasNext = false;
                    nextRight = null;
                }
            }
            return lowest;
        }

        private boolean nextIsLeft() {
            boolean returnLeft;
            if (leftHasNext) {
                if (rightHasNext) {
                    int leftFirstDayIndex = nextLeft.getFirstDayIndex();
                    int rightFirstDayIndex = nextRight.getFirstDayIndex();
                    returnLeft = leftFirstDayIndex <= rightFirstDayIndex;
                } else {
                    returnLeft = true;
                }
            } else {
                if (rightHasNext) {
                    returnLeft = false;
                } else {
                    throw new NoSuchElementException();
                }
            }
            return returnLeft;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("The optional operation remove() is not supported.");
        }

        public boolean isLastNextWasLeft() {
            return lastNextWasLeft;
        }

    }

}
