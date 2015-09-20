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
import org.optaplanner.examples.nurserostering.domain.DayOfWeek;
import org.optaplanner.examples.nurserostering.domain.Ta;
import org.optaplanner.examples.nurserostering.domain.CourseDate;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;

public class TaConsecutiveAssignmentStart implements Comparable<TaConsecutiveAssignmentStart>,
        Serializable {

    private Ta ta;
    private CourseDate courseDate;

    public TaConsecutiveAssignmentStart(Ta ta, CourseDate courseDate) {
        this.ta = ta;
        this.courseDate = courseDate;
    }

    public Ta getTa() {
        return ta;
    }

    public void setTa(Ta ta) {
        this.ta = ta;
    }

    public CourseDate getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(CourseDate courseDate) {
        this.courseDate = courseDate;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof TaConsecutiveAssignmentStart) {
            TaConsecutiveAssignmentStart other = (TaConsecutiveAssignmentStart) o;
            return new EqualsBuilder()
                    .append(ta, other.ta)
                    .append(courseDate, other.courseDate)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(ta)
                .append(courseDate)
                .toHashCode();
    }

    public int compareTo(TaConsecutiveAssignmentStart other) {
        return new CompareToBuilder()
                .append(ta, other.ta)
                .append(courseDate, other.courseDate)
                .toComparison();
    }

    @Override
    public String toString() {
        return ta + " " + courseDate + " - ...";
    }

    public Contract getContract() {
        return ta.getContract();
    }

    public int getCourseDateDayIndex() {
        return courseDate.getDayIndex();
    }

}
