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

package org.optaplanner.examples.nurserostering.domain.pattern;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.nurserostering.domain.CourseType;

@XStreamAlias("CourseType3DaysPattern")
public class CourseType3DaysPattern extends Pattern {

    private CourseType dayIndex0CourseType;
    private CourseType dayIndex1CourseType;
    private CourseType dayIndex2CourseType;

    public CourseType getDayIndex0CourseType() {
        return dayIndex0CourseType;
    }

    public void setDayIndex0CourseType(CourseType dayIndex0CourseType) {
        this.dayIndex0CourseType = dayIndex0CourseType;
    }

    public CourseType getDayIndex1CourseType() {
        return dayIndex1CourseType;
    }

    public void setDayIndex1CourseType(CourseType dayIndex1CourseType) {
        this.dayIndex1CourseType = dayIndex1CourseType;
    }

    public CourseType getDayIndex2CourseType() {
        return dayIndex2CourseType;
    }

    public void setDayIndex2CourseType(CourseType dayIndex2CourseType) {
        this.dayIndex2CourseType = dayIndex2CourseType;
    }

    @Override
    public String toString() {
        return "Work pattern: " + dayIndex0CourseType + ", " + dayIndex1CourseType + ", " + dayIndex2CourseType;
    }

}
