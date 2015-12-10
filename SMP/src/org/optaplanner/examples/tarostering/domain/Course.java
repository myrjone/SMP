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

package org.optaplanner.examples.tarostering.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.common.domain.AbstractPersistable;

import java.util.UUID;

@XStreamAlias("Course")
public class Course extends AbstractPersistable {

    private CourseDay courseDay;
    private CourseType courseType;
    private int index;

    public Course() {}

    public Course (CourseType courseType, CourseDay courseDay) {
        this.courseType = courseType;
        this.courseDay = courseDay;
    }

    private int requiredTaSize;

    public CourseDay getCourseDay() {
        return courseDay;
    }

    public void setCourseDay(CourseDay courseDay) {
        this.courseDay = courseDay;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getRequiredTaSize() {
        return requiredTaSize;
    }

    public void setRequiredTaSize(int requiredTaSize) {
        this.requiredTaSize = requiredTaSize;
    }

    public String getLabel() {
        return courseType.getLabel() + " of " + courseDay.getLabel() ;
    }

    @Override
    public String toString() {
        return courseDay + "_" + courseType;
    }

}
