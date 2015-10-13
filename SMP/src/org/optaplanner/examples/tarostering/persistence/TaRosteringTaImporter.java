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
import org.optaplanner.examples.tarostering.domain.Coordinator;
import org.optaplanner.examples.tarostering.domain.Course;
import org.optaplanner.examples.tarostering.domain.CourseAssignment;
import org.optaplanner.examples.tarostering.domain.CourseDay;
import org.optaplanner.examples.tarostering.domain.CourseType;
import org.optaplanner.examples.tarostering.domain.DayOfWeek;
import org.optaplanner.examples.tarostering.domain.Ta;
import org.optaplanner.examples.tarostering.domain.TaRoster;
import org.optaplanner.examples.tarostering.domain.contract.Contract;
import org.optaplanner.examples.tarostering.domain.contract.ContractLine;
import org.optaplanner.examples.tarostering.domain.contract.ContractLineType;
import org.optaplanner.examples.tarostering.domain.contract.MinMaxContractLine;
import org.optaplanner.examples.tarostering.domain.request.CourseOffRequest;
import org.optaplanner.examples.tarostering.domain.request.CourseOnRequest;
import org.optaplanner.examples.tarostering.domain.solver.CourseAssignmentDayOfWeekComparator;

public class TaRosteringTaImporter extends AbstractTxtSolutionImporter {
    protected TaRoster taRoster;

    public static void main(String[] args) {
        TaRosteringTaImporter taRosteringTaImporter = new TaRosteringTaImporter();
        taRosteringTaImporter.convertAll();
    }

    //TODO: REMOVE ***** For testing purposes only
    private static void generateCourseDayList(TaRoster taRoster) {
            int courseDaySize = DayOfWeek.values().length;
            List<CourseDay> courseDayList = new ArrayList<>(courseDaySize);
            long id = 0L;
            int dayIndex = 0;
            for (DayOfWeek day : DayOfWeek.values()) {
                CourseDay courseDay = new CourseDay();
                courseDay.setId(id);
                courseDay.setDayIndex(dayIndex);
                String dayString = day.getCode();
                courseDay.setDayString(dayString);
                courseDay.setDayOfWeek(day);
                courseDay.setCourseList(new ArrayList<Course>());
                courseDayList.add(courseDay);
                id++;
                dayIndex++;
            }
            taRoster.setCourseDayList(courseDayList);
        }
    /// TODO: REMOVE
    public void initTA(TaRoster taRoster) {
        long id = 0L;
        int index = 0;
        generateCourseDayList(taRoster);
        taRoster.setId(0L);
        ArrayList<CourseType> courseTypeList = new ArrayList<>();
        CourseType courseType = new CourseType();
        courseType.setIndex(index++);
        courseType.setId(id++);
        courseType.setCode("00000");
        courseType.setStartTimeString("08:00:00");
        courseType.setEndTimeString("11:50:00");
        courseTypeList.add(courseType);
        CourseType courseType2 = new CourseType();
        courseType2.setId(id++);
        courseType2.setIndex(index++);
        courseType2.setCode("00001");
        courseType2.setStartTimeString("01:10:00");
        courseType2.setEndTimeString("15:30:00");
        courseTypeList.add(courseType2);
        CourseType courseType3 = new CourseType();
        courseType3.setId(id++);
        courseType3.setIndex(index++);
        courseType3.setCode("00002");
        courseType3.setStartTimeString("08:00:00");
        courseType3.setEndTimeString("09:50:00");
        courseTypeList.add(courseType3);
        CourseType courseType4 = new CourseType();
        courseType4.setId(id++);
        courseType4.setIndex(index++);
        courseType4.setCode("00003");
        courseType4.setStartTimeString("01:10:00");
        courseType4.setEndTimeString("15:30:00");
        courseTypeList.add(courseType4);
        taRoster.setCourseTypeList(courseTypeList);

        id = 0L;
        index = 0;
        ArrayList<Course> courseList = new ArrayList<>();
        Course course = new Course();
        course.setId(id++);
        course.setIndex(index++);
        course.setCourseDay(taRoster.getCourseDayList().get(2));
        course.setCourseType(courseType);
        course.setRequiredTaSize(1);
        courseList.add(course);
        Course course2 = new Course();
        course2.setId(id++);
        course2.setIndex(index++);
        course2.setCourseDay(taRoster.getCourseDayList().get(4));
        course2.setCourseType(courseType2);
        course2.setRequiredTaSize(1);
        courseList.add(course2);
        Course course3 = new Course();
        course3.setId(id++);
        course3.setIndex(index++);
        course3.setCourseDay(taRoster.getCourseDayList().get(2));
        course3.setCourseType(courseType3);
        course3.setRequiredTaSize(1);
        courseList.add(course3);
        Course course4 = new Course();
        course4.setId(id++);
        course4.setIndex(index++);
        course4.setCourseDay(taRoster.getCourseDayList().get(4));
        course4.setCourseType(courseType4);
        course4.setRequiredTaSize(2);
        courseList.add(course4);
        Course course5 = new Course();
        course5.setId(id++);
        course5.setIndex(index++);
        course5.setCourseDay(taRoster.getCourseDayList().get(3));
        course5.setCourseType(courseType3);
        course5.setRequiredTaSize(1);
        courseList.add(course5);
        taRoster.setCourseList(courseList);

        id = 0L;
        long contractLineId = 0L;
        index = 0;
        List<Contract> contractList = new ArrayList<>();
        Contract contract = new Contract();
        contract.setId(id);
        contract.setCode("0");
        contractList.add(contract);
        taRoster.setContractList(contractList);

        // Setup contract line
        List<ContractLine> contractLineList = new ArrayList<>();
        MinMaxContractLine contractLine = new MinMaxContractLine();
        contractLine.setId(contractLineId);
        contractLine.setContract(contract);
        contractLine.setContractLineType(ContractLineType.TOTAL_ASSIGNMENTS);
        contractLine.setMinimumEnabled(true);
        contractLine.setMinimumValue(1);
        contractLine.setMinimumWeight(1);
        contractLine.setMaximumEnabled(true);
        contractLine.setMaximumValue(2);
        contractLine.setMaximumWeight(1);
        contractLineList.add(contractLine);
        taRoster.setContractLineList(contractLineList);

        taRoster.setCoordinatorList(Collections.<Coordinator>emptyList());
        taRoster.setCourseOnRequestList(Collections.<CourseOnRequest>emptyList());
    }

    public TaRosteringTaImporter() {
        super(new TaRosteringDao());
    }

    public void setTaRoster(TaRoster taRoster) {
        this.taRoster = taRoster;
    }
    protected class TaImporterTxtInputBuilder extends TxtInputBuilder {
        private static final int REQUIRED_COL_SIZE = 5;
        private int numOfColumns;
        protected TaRoster taRoster;
        protected final Map<String, Ta> taMap;
        protected final Map<String, Long> timeStringToTimeValueMap;
        protected final Map<Integer, String> columnToTimeStringMap;
        protected final Map<CourseType, String> courseTypeToTimeStringMap;

        // Maps a ta to a map of days to times available
        protected Map<Ta, Map<DayOfWeek,List<String>>> taToAvailabilityMap;

        public TaImporterTxtInputBuilder(TaRoster taRoster) {
            this.taRoster = taRoster;
            this.taMap = new HashMap<>();
            this.taToAvailabilityMap = new HashMap<>();
            this.timeStringToTimeValueMap = new HashMap<>();
            this.columnToTimeStringMap = new HashMap<>();
            this.courseTypeToTimeStringMap = new HashMap<>();
            this.numOfColumns = REQUIRED_COL_SIZE - 1;
        }

        @Override
        public Solution readSolution() throws IOException {
            TaRoster taRoster = new TaRoster();
            this.taRoster = taRoster;
            initTA(taRoster);
            String str;
            String [] tokens;
            int line = 1;
            int code = 0;
            long id = 0L;
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
                createTA(tokens, line, code, id);
                line++;
                id++;
                code++;
            }
            generateTaList();
            generateCourseOffRequests();
            generateCourseAssignment();
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

        private void generateTaList() {
            List<Ta> taList = new ArrayList<>();
            for (String email : this.taMap.keySet()) {
                taList.add(taMap.get(email));
            }
            taRoster.setTaList(taList);
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
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
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
                        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
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

        private void createTA(String[] tokens, int line, int code, long id) {
            if (tokens.length != numOfColumns) {
                throw new IllegalArgumentException("Error on line " + line + " in "
                        + "file " + super.inputFile.getName() + " - "
                        + " number of columns must be " + numOfColumns);
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
                ta.setCode(String.valueOf(code));
                ta.setName(tokens[0] + ", " + tokens[1]);
                ta.setEmail(tokens[2]);
                ta.setContract(taRoster.getContractList().get(0));
                ta.setCourseOffRequestMap(new HashMap<Course, CourseOffRequest>());
                ta.setCourseOnRequestMap(Collections.<Course, CourseOnRequest>emptyMap());
                taMap.put(tokens[2], ta);
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
            for (int i = REQUIRED_COL_SIZE; i < numOfColumns; i++) {
                if (Integer.parseInt(tokens[i]) == 1) {
                    String startString = columnToTimeStringMap.get(i).split("-")[0];
                    String endString = columnToTimeStringMap.get(i).split("-")[1];
                    while (i < 10 && Integer.parseInt(tokens[i]) == 1) {
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
        return new TaImporterTxtInputBuilder(taRoster);
    }

}
