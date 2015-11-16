/*
 * Copyright 2015 JBoss Inc
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


package org.optaplanner.examples.tarostering.persistence;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.tarostering.domain.Course;
import org.optaplanner.examples.tarostering.domain.CourseAssignment;
import org.optaplanner.examples.tarostering.domain.CourseType;
import org.optaplanner.examples.tarostering.domain.DayOfWeek;
import org.optaplanner.examples.tarostering.domain.Ta;
import org.optaplanner.examples.tarostering.domain.TaRoster;
import org.optaplanner.examples.tarostering.domain.contract.MinMaxContractLine;
import org.optaplanner.examples.tarostering.domain.request.CourseOffRequest;
import org.optaplanner.examples.tarostering.domain.request.CourseOnRequest;
import org.optaplanner.examples.tarostering.domain.solver.CourseAssignmentDayOfWeekComparator;

public class TaRosteringTaImporter extends AbstractTxtSolutionImporter {
    private static final int REQUIRED_COL_SIZE = 5;
    protected TaRoster taRoster;
    protected final Map<String, Ta> taMap;
    protected final Map<String, Long> timeStringToTimeValueMap;
    protected final Map<Integer, String> columnToTimeStringMap;
    protected final Map<CourseType, String> courseTypeToTimeStringMap;
    private static long id = 0L;

    // Maps a ta to a map of days to times available
    protected Map<Ta, Map<DayOfWeek,List<String>>> taToAvailabilityMap;

    public TaRosteringTaImporter(TaRoster taRoster) {
        super(new TaRosteringDao());
        this.taRoster = taRoster;
        this.taMap = new HashMap<>();
        this.taToAvailabilityMap = new HashMap<>();
        this.timeStringToTimeValueMap = new HashMap<>();
        this.columnToTimeStringMap = new HashMap<>();
        this.courseTypeToTimeStringMap = new HashMap<>();
    }

    protected class TaImporterTxtInputBuilder extends TxtInputBuilder {

        private int numOfColumns;


        public TaImporterTxtInputBuilder() {
            this.numOfColumns = REQUIRED_COL_SIZE - 1;
        }

        @Override
        public Solution readSolution() throws IOException {
            String str;
            String [] tokens;
            int line = 1;
            str = bufferedReader.readLine();
            str = str.replaceAll("\\s", "");
            if (str == null) {
                throw new IllegalArgumentException("File " + super.inputFile.getName() + " is empty");
            }
            tokens = str.split(",");
            validateHeader(tokens, line);
            generateCourseLookupMap();
            generateFileTimeLookupMap(tokens, line);
            line++;
            while((str=bufferedReader.readLine())!=null && str.length()!=0) {
                str = str.replaceAll("\\s", "");
                tokens = str.split(",");
                createTA(tokens, line, id);
                line++;
            }

            generateCourseOffRequests();
            generateCourseAssignment();
            taRoster.setCourseOnRequestList(Collections.<CourseOnRequest>emptyList());

            // Dynamically set the max course assignments
            int totalAssignments = taRoster.getCourseAssignmentList().size();
            int totalTas = taRoster.getTaList().size();
            int maxAssignments = (totalAssignments + totalTas - 1) / totalTas;
            MinMaxContractLine mmcl = (MinMaxContractLine) taRoster.getContractLineList().get(0);
            mmcl.setMaximumValue(maxAssignments > 1 ? maxAssignments : 1);
            taRoster.getContractLineList().set(0, mmcl);
            id++;
            return taRoster;
        }

        private void generateCourseAssignment() {
            List<Course> courseList = taRoster.getCourseList();
            List<CourseAssignment> courseAssignmentList = new ArrayList<>(courseList.size());
            long id = 0L;
            for (Course course : courseList) {
                for (int i = 0; i < course.getRequiredTaSize(); i++) {
                    CourseAssignment courseAssignment = new CourseAssignment();
                    courseAssignment.setCourse(course);
                    courseAssignment.setIndexInCourse(i);
                    // Notice that we leave the PlanningVariable properties on null
                    courseAssignmentList.add(courseAssignment);
                }
            }
            courseAssignmentList = sortCourseAssignmentByDay(courseAssignmentList);
            for (CourseAssignment courseAssignmentList1 : courseAssignmentList) {
                courseAssignmentList1.setId(id++);
            }
            taRoster.setCourseAssignmentList(courseAssignmentList);
        }

        private List<CourseAssignment> sortCourseAssignmentByDay(List<CourseAssignment> caList) {
            if (caList.isEmpty()) {
                return Collections.emptyList();
            }
            else {
                List<CourseAssignment> temp = caList;
                Comparator<CourseAssignment> comp = new CourseAssignmentDayOfWeekComparator();
                Collections.sort(temp, comp);
                return temp;
            }
        }

        private void generateFileTimeLookupMap(String[] tokens, int line) {
            for (int i = REQUIRED_COL_SIZE - 1; i < tokens.length; i++) {
                try {
                    String timeString = tokens[i];
                    if (!Pattern.matches("^\\d{2}:\\d{2}:\\d{2}-\\d{2}:\\d{2}:\\d{2}$", timeString)) {
                        throw new IllegalArgumentException("Error on line " + line + " in "
                                + "file " + super.inputFile.getName() + " - "
                                + " time entries must be xxxx-xxxx, where x is an "
                                + "integer between 0 and 9");
                    }
                    String[] splitTimeString = timeString.split("-");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    Date parsedStartTime = dateFormat.parse(splitTimeString[0]);
                    Timestamp startTimeStamp = new java.sql.Timestamp(parsedStartTime.getTime());
                    Date parsedEndTime = dateFormat.parse(splitTimeString[1]);
                    Timestamp endTimeStamp = new java.sql.Timestamp(parsedEndTime.getTime());
                    timeStringToTimeValueMap.put(splitTimeString[0], startTimeStamp.getTime());
                    timeStringToTimeValueMap.put(splitTimeString[1], endTimeStamp.getTime());
                    columnToTimeStringMap.put(i,tokens[i]);
                    numOfColumns++;
                } catch (ParseException ex) {
                    throw new RuntimeException("Error parsing file " + super.inputFile.getName());
                }

            }
        }

         private void generateCourseLookupMap() {
                try {
                    List<CourseType> courseTypeList = taRoster.getCourseTypeList();
                    for (CourseType courseType : courseTypeList) {
                        String startString = courseType.getStartTimeString();
                        String endString = courseType.getEndTimeString();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                        Date parsedStartTime = dateFormat.parse(courseType.getStartTimeString());
                        Timestamp startTimeStamp = new java.sql.Timestamp(parsedStartTime.getTime());
                        Date parsedEndTime = dateFormat.parse(courseType.getEndTimeString());
                        Timestamp endTimeStamp = new java.sql.Timestamp(parsedEndTime.getTime());
                        courseTypeToTimeStringMap.put(courseType, startString + "-" + endString);
                        timeStringToTimeValueMap.put(startString, startTimeStamp.getTime());
                        timeStringToTimeValueMap.put(endString, endTimeStamp.getTime());
                    }
                } catch (ParseException ex) {
                    throw new RuntimeException("Error generating coursetimes for file " + super.inputFile.getName());

            }
        }

        private void validateHeader(String[] tokens, int line) {
            if (tokens.length <  REQUIRED_COL_SIZE) {
                throw new IllegalArgumentException("Error on line " + line + " in "
                            + "file " + super.inputFile.getName() + " - "
                            + " number of columns must be " + numOfColumns);
            }
            int colCount = 0;
            if (!("first".equals(tokens[colCount]))) {
                throw new IllegalArgumentException("Error on line " + line + " in "
                            + "file " + super.inputFile.getName() + " - "
                            + " column " + colCount + " must be 'first'");
            }
            colCount++;
            if (!"last".equals(tokens[colCount])) {
                throw new IllegalArgumentException("Error on line " + line + " in "
                            + "file " + super.inputFile.getName() + " - "
                            + " column " + colCount + " must be 'last'");
            }
            colCount++;
            if (!"email".equals(tokens[colCount])) {
                throw new IllegalArgumentException("Error on line " + line + " in "
                            + "file " + super.inputFile.getName() + " - "
                            + " column " + colCount + " must be 'email'");
            }
            colCount++;
            if (!"day".equals(tokens[colCount])) {
                throw new IllegalArgumentException("Error on line " + line + " in "
                            + "file " + super.inputFile.getName() + " - "
                            + " column " + colCount + " must be 'day'");
            }
        }

        private void generateCourseOffRequests() {
            List<Course> courseList = taRoster.getCourseList();
            List<CourseOffRequest> courseOffRequestList = new ArrayList<>();
            long id = 0L;
            for (String email : taMap.keySet()) {
                Ta ta = taMap.get(email);
                Map<DayOfWeek,List<String>> availabilityMap = taToAvailabilityMap.get(ta);
                // Iterate over every day in the ta's availability
                for (DayOfWeek dayOfWeek : availabilityMap.keySet()) {
                    List<String> taTimeList = availabilityMap.get(dayOfWeek);

                    // for each timeslot (eg 0800-1000)
                    for (String timeString : taTimeList) {
                        String[] timeRange = timeString.split("-");
                        long taStart = timeStringToTimeValueMap.get(timeRange[0]);
                        long taEnd = timeStringToTimeValueMap.get(timeRange[1]);

                        for (Course course : courseList) {
                            DayOfWeek courseDayOfWeek = course.getCourseDay().getDayOfWeek();
                            if (dayOfWeek == courseDayOfWeek) {
                                CourseType courseType = course.getCourseType();
                                String courseTimeString = courseTypeToTimeStringMap.get(courseType);
                                String[] splitCourseTimeString = courseTimeString.split("-");
                                long courseStart = timeStringToTimeValueMap.get(splitCourseTimeString[0]);
                                long courseEnd = timeStringToTimeValueMap.get(splitCourseTimeString[1]);

                                // Actual logic
                                if (courseEnd <= taEnd && courseStart >= taStart) {
                                    CourseOffRequest courseOffRequest = new CourseOffRequest();
                                    courseOffRequest.setId(id++);
                                    courseOffRequest.setTa(ta);
                                    courseOffRequest.setCourse(course);
                                    ta.getCourseOffRequestMap().put(course, courseOffRequest);
                                    courseOffRequestList.add(courseOffRequest);
                                }
                            }
                        }
                    }
                }
            }
            taRoster.setCourseOffRequestList(courseOffRequestList);
        }

        private void createTA(String[] tokens, int line, long id) {
            if (tokens.length != numOfColumns) {
                throw new IllegalArgumentException("Error on line " + line + " in "
                        + "file " + super.inputFile.getName() + " - "
                        + " number of columns must be " + numOfColumns);
            }

            if (taRoster.getTaList() == null) {
                List<Ta> taList = new ArrayList<>();
                taRoster.setTaList(taList);
            }

            Ta ta;
            Map<DayOfWeek, List<String>> availabilityMap;
            if (taMap.containsKey(tokens[2])) {
                ta = taMap.get(tokens[2]);
                availabilityMap = taToAvailabilityMap.get(ta);
            }
            else {
                ta = new Ta();
                ta.setId(id);
                ta.setCode(String.valueOf(id));
                ta.setName(tokens[1] + ", " + tokens[0]);
                ta.setEmail(tokens[2]);
                ta.setContract(taRoster.getContractList().get(0));
                ta.setCourseOffRequestMap(new HashMap<Course, CourseOffRequest>());
                ta.setCourseOnRequestMap(Collections.<Course, CourseOnRequest>emptyMap());
                taMap.put(tokens[2], ta);
                taRoster.getTaList().add(ta);
                availabilityMap = new HashMap<>();
                taToAvailabilityMap.put(ta, availabilityMap);
            }
            DayOfWeek dayOfWeek = DayOfWeek.valueOfCode(tokens[3]);
            if (dayOfWeek == null) {
                throw new IllegalArgumentException("Error on line "
                        + line + " of file " + super.inputFile.getName() + " -"
                        + "not a valid day of week");
            }
            else if (availabilityMap.containsKey(dayOfWeek)) {
                throw new IllegalArgumentException("Error on line "
                        + line + " of file " + super.inputFile.getName() + " -"
                        + dayOfWeek.getCode() + " has already been set");
            }

            List<String> availabilityList = new ArrayList<>();
            for (int i = REQUIRED_COL_SIZE-1; i < numOfColumns; i++) {
                if (Integer.parseInt(tokens[i]) == 1) {
                    String startString = columnToTimeStringMap.get(i).split("-")[0];
                    String endString = columnToTimeStringMap.get(i).split("-")[1];
                    while (i < numOfColumns && Integer.parseInt(tokens[i]) == 1) {
                        endString = columnToTimeStringMap.get(i).split("-")[1];
                        i++;
                    }
                    availabilityList.add(startString + "-" + endString);
                }
            }
            availabilityMap.put(dayOfWeek, availabilityList);
        }

    }
    @Override
    public TxtInputBuilder createTxtInputBuilder() {
        return new TaImporterTxtInputBuilder();
    }

}
