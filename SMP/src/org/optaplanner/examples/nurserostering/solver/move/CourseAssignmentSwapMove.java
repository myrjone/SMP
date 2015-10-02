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

package org.optaplanner.examples.nurserostering.solver.move;

import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.nurserostering.domain.CourseAssignment;
import org.optaplanner.examples.nurserostering.domain.Ta;

public class CourseAssignmentSwapMove extends AbstractMove {

    private CourseAssignment leftCourseAssignment;
    private CourseAssignment rightCourseAssignment;

    public CourseAssignmentSwapMove(CourseAssignment leftCourseAssignment, CourseAssignment rightCourseAssignment) {
        this.leftCourseAssignment = leftCourseAssignment;
        this.rightCourseAssignment = rightCourseAssignment;
    }

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return !ObjectUtils.equals(leftCourseAssignment.getTa(), rightCourseAssignment.getTa());
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new CourseAssignmentSwapMove(rightCourseAssignment, leftCourseAssignment);
    }

    public void doMove(ScoreDirector scoreDirector) {
        Ta oldLeftTa = leftCourseAssignment.getTa();
        Ta oldRightTa = rightCourseAssignment.getTa();
        NurseRosteringMoveHelper.moveTa(scoreDirector, leftCourseAssignment, oldRightTa);
        NurseRosteringMoveHelper.moveTa(scoreDirector, rightCourseAssignment, oldLeftTa);
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Arrays.asList(leftCourseAssignment, rightCourseAssignment);
    }

    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList(leftCourseAssignment.getTa(), rightCourseAssignment.getTa());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CourseAssignmentSwapMove) {
            CourseAssignmentSwapMove other = (CourseAssignmentSwapMove) o;
            return new EqualsBuilder()
                    .append(leftCourseAssignment, other.leftCourseAssignment)
                    .append(rightCourseAssignment, other.rightCourseAssignment)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftCourseAssignment)
                .append(rightCourseAssignment)
                .toHashCode();
    }

    public String toString() {
        return leftCourseAssignment + " <=> " + rightCourseAssignment;
    }

}
