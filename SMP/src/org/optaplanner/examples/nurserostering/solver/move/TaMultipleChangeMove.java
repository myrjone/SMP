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
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.nurserostering.domain.CourseAssignment;
import org.optaplanner.examples.nurserostering.domain.Ta;

public class TaMultipleChangeMove extends AbstractMove {

    private Ta fromTa;
    private List<CourseAssignment> courseAssignmentList;
    private Ta toTa;

    public TaMultipleChangeMove(Ta fromTa, List<CourseAssignment> courseAssignmentList, Ta toTa) {
        this.fromTa = fromTa;
        this.courseAssignmentList = courseAssignmentList;
        this.toTa = toTa;
    }

    @Override
    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return !ObjectUtils.equals(fromTa, toTa);
    }

    @Override
    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new TaMultipleChangeMove(toTa, courseAssignmentList, fromTa);
    }

    @Override
    public void doMove(ScoreDirector scoreDirector) {
        for (CourseAssignment courseAssignment : courseAssignmentList) {
            if (!courseAssignment.getTa().equals(fromTa)) {
                throw new IllegalStateException("The courseAssignment (" + courseAssignment + ") should have the same ta ("
                        + fromTa + ") as the fromTa (" + fromTa + ").");
            }
            NurseRosteringMoveHelper.moveTa(scoreDirector, courseAssignment, toTa);
        }
    }

    @Override
    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(courseAssignmentList);
    }

    @Override
    public Collection<? extends Object> getPlanningValues() {
        return Arrays.asList(fromTa, toTa);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof TaMultipleChangeMove) {
            TaMultipleChangeMove other = (TaMultipleChangeMove) o;
            return new EqualsBuilder()
                    .append(fromTa, other.fromTa)
                    .append(courseAssignmentList, other.courseAssignmentList)
                    .append(toTa, other.toTa)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(fromTa)
                .append(courseAssignmentList)
                .append(toTa)
                .toHashCode();
    }

    @Override
    public String toString() {
        return courseAssignmentList + " => " + toTa;
    }

}
