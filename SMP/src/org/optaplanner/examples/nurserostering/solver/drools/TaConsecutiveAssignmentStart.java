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

package org.optaplanner.examples.nurserostering.solver.drools;

import java.io.Serializable;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.examples.nurserostering.domain.CourseDay;
import org.optaplanner.examples.nurserostering.domain.Ta;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;

public class TaConsecutiveAssignmentStart implements Comparable<TaConsecutiveAssignmentStart>,
        Serializable {

    private Ta ta;
    private CourseDay courseDay;

    public TaConsecutiveAssignmentStart(Ta ta, CourseDay courseDay) {
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
        } else if (o instanceof TaConsecutiveAssignmentStart) {
            TaConsecutiveAssignmentStart other = (TaConsecutiveAssignmentStart) o;
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
    public int compareTo(TaConsecutiveAssignmentStart other) {
        return new CompareToBuilder()
                .append(ta, other.ta)
                .append(courseDay, other.courseDay)
                .toComparison();
    }

    @Override
    public String toString() {
        return ta + " " + courseDay + " - ...";
    }

    public Contract getContract() {
        return ta.getContract();
    }

    public int getCourseDayDayIndex() {
        return courseDay.getDayIndex();
    }

}
