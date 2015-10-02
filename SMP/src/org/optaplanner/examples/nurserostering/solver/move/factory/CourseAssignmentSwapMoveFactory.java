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
import java.util.List;
import java.util.ListIterator;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.examples.nurserostering.domain.CourseAssignment;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.solver.move.CourseAssignmentSwapMove;

public class CourseAssignmentSwapMoveFactory implements MoveListFactory<NurseRoster> {

    @Override
    public List<Move> createMoveList(NurseRoster nurseRoster) {
        // Filter out every immovable CourseAssignment
        List<CourseAssignment> courseAssignmentList = new ArrayList<>(
                nurseRoster.getCourseAssignmentList());

        List<Move> moveList = new ArrayList<>();
        for (ListIterator<CourseAssignment> leftIt = courseAssignmentList.listIterator(); leftIt.hasNext();) {
            CourseAssignment leftCourseAssignment = leftIt.next();
            for (ListIterator<CourseAssignment> rightIt = courseAssignmentList.listIterator(leftIt.nextIndex()); rightIt.hasNext();) {
                CourseAssignment rightCourseAssignment = rightIt.next();
                moveList.add(new CourseAssignmentSwapMove(leftCourseAssignment, rightCourseAssignment));
            }
        }
        return moveList;
    }

}
