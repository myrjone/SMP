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

import java.util.Collection;
import java.util.Collections;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.impl.heuristic.move.AbstractMove;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.nurserostering.domain.CourseAssignment;
import org.optaplanner.examples.nurserostering.domain.Ta;

public class TaChangeMove extends AbstractMove {

    private CourseAssignment courseAssignment;
    private Ta toTa;

    public TaChangeMove(CourseAssignment courseAssignment, Ta toTa) {
        this.courseAssignment = courseAssignment;
        this.toTa = toTa;
    }

    public boolean isMoveDoable(ScoreDirector scoreDirector) {
        return !ObjectUtils.equals(courseAssignment.getTa(), toTa);
    }

    public Move createUndoMove(ScoreDirector scoreDirector) {
        return new TaChangeMove(courseAssignment, courseAssignment.getTa());
    }

    public void doMove(ScoreDirector scoreDirector) {
        NurseRosteringMoveHelper.moveTa(scoreDirector, courseAssignment, toTa);
    }

    public Collection<? extends Object> getPlanningEntities() {
        return Collections.singletonList(courseAssignment);
    }

    public Collection<? extends Object> getPlanningValues() {
        return Collections.singletonList(toTa);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof TaChangeMove) {
            TaChangeMove other = (TaChangeMove) o;
            return new EqualsBuilder()
                    .append(courseAssignment, other.courseAssignment)
                    .append(toTa, other.toTa)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(courseAssignment)
                .append(toTa)
                .toHashCode();
    }

    public String toString() {
        return courseAssignment + " => " + toTa;
    }

}
