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

package org.optaplanner.examples.tarostering.solver.drools;

import java.io.Serializable;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.examples.tarostering.domain.CourseDay;
import org.optaplanner.examples.tarostering.domain.Ta;
import org.optaplanner.examples.tarostering.domain.contract.Contract;

public class TaConsecutiveAssignmentEnd implements Comparable<TaConsecutiveAssignmentEnd>, Serializable {

    private Ta ta;
    private CourseDay courseDay;

    public TaConsecutiveAssignmentEnd(Ta ta, CourseDay courseDay) {
        this.ta = ta;
        this.courseDay = courseDay;
    }

    public Ta getTa() {
        return ta;
    }

    public void setTa(Ta ta) {
        this.ta = ta;
    }

    public CourseDay getCourseDay() {
        return courseDay;
    }

    public void setCourseDay(CourseDay courseDay) {
        this.courseDay = courseDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof TaConsecutiveAssignmentEnd) {
            TaConsecutiveAssignmentEnd other = (TaConsecutiveAssignmentEnd) o;
            return new EqualsBuilder()
                    .append(ta, other.ta)
                    .append(courseDay, other.courseDay)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(ta)
                .append(courseDay)
                .toHashCode();
    }

    @Override
    public int compareTo(TaConsecutiveAssignmentEnd other) {
        return new CompareToBuilder()
                .append(ta, other.ta)
                .append(courseDay, other.courseDay)
                .toComparison();
    }

    @Override
    public String toString() {
        return ta + " ... - " + courseDay;
    }

    public Contract getContract() {
        return ta.getContract();
    }

    public int getCourseDayDayIndex() {
        return courseDay.getDayIndex();
    }
}
