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

package org.optaplanner.examples.nurserostering.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;
import org.optaplanner.examples.nurserostering.domain.solver.CourseAssignmentDifficultyComparator;
import org.optaplanner.examples.nurserostering.domain.solver.TaStrengthComparator;

@PlanningEntity(difficultyComparatorClass = CourseAssignmentDifficultyComparator.class)
@XStreamAlias("CourseAssignment")
public class CourseAssignment extends AbstractPersistable {

    private Course course;
    private int indexInCourse;

    // Planning variables: changes during planning, between score calculations.
    private Ta ta;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getIndexInCourse() {
        return indexInCourse;
    }

    public void setIndexInCourse(int indexInCourse) {
        this.indexInCourse = indexInCourse;
    }

    @PlanningVariable(valueRangeProviderRefs = {"taRange"},
            strengthComparatorClass = TaStrengthComparator.class)
    public Ta getTa() {
        return ta;
    }

    public void setTa(Ta ta) {
        this.ta = ta;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public CourseDay getCourseDay() {
        return course.getCourseDay();
    }

    public CourseType getCourseType() {
        return course.getCourseType();
    }

    public int getCourseDayDayIndex() {
        return course.getCourseDay().getDayIndex();
    }

    public DayOfWeek getCourseDayDayOfWeek() {
        return course.getCourseDay().getDayOfWeek();
    }

    public Contract getContract() {
        if (ta == null) {
            return null;
        }
        return ta.getContract();
    }

    @Override
    public String toString() {
        return course + "@" + ta;
    }

}
