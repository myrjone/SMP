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

@XStreamAlias("CourseType")
public class CourseType extends AbstractPersistable {

    private String crn;
    private int index;
    private String startTimeString;
    private String endTimeString;
    private String department;
    private String courseNumber;
    private String sectionNumber;
    private String building;
    private String roomNumber;
    private String coordinatorName;

    public CourseType() {}

    /***
     *
     * @param index unique identifier
     * @param crn CRN number for the course
     * @param startTimeString
     * @param endTimeString
     * @param department
     * @param courseNumber
     * @param sectionNumber
     * @param building
     * @param roomNumber
     * @param coordinatorName
     */
    public CourseType(int index, String crn, String startTimeString, String endTimeString, String department, String courseNumber,
                      String sectionNumber, String building, String roomNumber, String coordinatorName) {
        this.index = index;
        this.crn = crn;
        this.startTimeString = startTimeString;
        this.endTimeString = endTimeString;
        this.department = department;
        this.courseNumber = courseNumber;
        this.sectionNumber = sectionNumber;
        this.building = building;
        this.roomNumber = roomNumber;
        this.coordinatorName = coordinatorName;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(String sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCrn() {
        return crn;
    }

    public void setCrn(String crn) {
        this.crn = crn;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getStartTimeString() {
        return startTimeString;
    }

    public String getStartTimeFormatted() {
        String[] split = startTimeString.split(":");
        return split[0] + split[1];
    }

    public void setStartTimeString(String startTimeString) {
        this.startTimeString = startTimeString;
    }

    public String getEndTimeString() {
        return endTimeString;
    }

    public String getEndTimeFormatted() {
        String[] split = endTimeString.split(":");
        return split[0] + split[1];
    }


    public void setEndTimeString(String endTimeString) {
        this.endTimeString = endTimeString;
    }

    public String getLabel() {
        return crn + " (" + department + " " + courseNumber + ")";
    }

    @Override
    public String toString() {
        return crn;
    }

}
