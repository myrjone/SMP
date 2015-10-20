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
import java.util.List;
import org.optaplanner.examples.common.domain.AbstractPersistable;

@XStreamAlias("CourseDay")
public class CourseDay extends AbstractPersistable {

    private int dayIndex;
    private String dayString;
    private DayOfWeek dayOfWeek;
    private List<Course> courseList;

    public CourseDay() {}

    public CourseDay(long id, int dayIndex, String dayString, DayOfWeek dayOfWeek, List<Course> courseList) {
        this.id = id;
        this.dayIndex = dayIndex;
        this.dayString = dayString;
        this.dayOfWeek = dayOfWeek;
        this.courseList = courseList;
    }

    public int getDayIndex() {
        return dayIndex;
    }

    public void setDayIndex(int dayIndex) {
        this.dayIndex = dayIndex;
    }

    public String getDayString() {
        return dayString;
    }

    public void setDayString(String dayString) {
        this.dayString = dayString;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public String getLabel() {
        return dayOfWeek.getCode();
    }

    @Override
    public String toString() {
        return dayString + "(" + dayOfWeek + ")";
    }

    public int getWeekendSundayIndex() {
        switch (dayOfWeek) {
            case MONDAY:
                return dayIndex - 1;
            case TUESDAY:
                return dayIndex - 2;
            case WEDNESDAY:
                return dayIndex - 3;
            case THURSDAY:
                return dayIndex + 3;
            case FRIDAY:
                return dayIndex + 2;
            case SATURDAY:
                return dayIndex + 1;
            case SUNDAY:
                return dayIndex;
            default:
                throw new IllegalArgumentException("The dayOfWeek (" + dayOfWeek + ") is not valid.");
        }
    }

}
