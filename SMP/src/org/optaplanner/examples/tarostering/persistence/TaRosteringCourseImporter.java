package org.optaplanner.examples.tarostering.persistence;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.tarostering.domain.*;
import org.optaplanner.examples.tarostering.domain.DayOfWeek;
import org.optaplanner.examples.tarostering.domain.contract.Contract;
import org.optaplanner.examples.tarostering.domain.contract.ContractLine;
import org.optaplanner.examples.tarostering.domain.contract.ContractLineType;
import org.optaplanner.examples.tarostering.domain.contract.MinMaxContractLine;

import java.io.IOException;
import java.util.*;

/**
 * Created by ahooper on 10/13/2015.
 */
public class TaRosteringCourseImporter extends AbstractTxtSolutionImporter {
    protected TaRoster taRoster;

    public TaRosteringCourseImporter() {
        super(new TaRosteringDao());
    }


    public static class CourseImporterTxtInputBuilder extends TxtInputBuilder {
        protected TaRoster taRoster;
        protected Map<String, CourseDay> courseDayMap;
        protected Map<String, CourseType> courseTypeMap;
        protected Map<String, Contract> contractMap;

        public CourseImporterTxtInputBuilder(TaRoster taRoster) {
            this.taRoster = taRoster;
            this.courseDayMap = new HashMap<>();
            this.courseTypeMap = new HashMap<>();
            this.contractMap = new HashMap<>();
        }

        @Override
        public Solution readSolution() throws IOException {
            TaRoster taRoster = new TaRoster();
            taRoster.setId(0L);

            generateCourseDayList(taRoster);
            readCourseList(taRoster);
            generateContract(taRoster);

            return taRoster;
        }

        private void generateCourseDayList(TaRoster taRoster) {
            int courseDaySize = DayOfWeek.values().length;
            List<CourseDay> courseDayList = new ArrayList<>(courseDaySize);
            courseDayMap = new HashMap<>(courseDaySize);
            long id = 0L;
            int dayIndex = 0;
            for (DayOfWeek day : DayOfWeek.values()) {
                String dayString = day.getCode();
                CourseDay courseDay = new CourseDay(id, dayIndex, dayString, day, new ArrayList<Course>());
                courseDayList.add(courseDay);
                courseDayMap.put(day.getAbbrev(), courseDay);
                id++;
                dayIndex++;
            }
            taRoster.setCourseDayList(courseDayList);
        }

        private void readCourseList(TaRoster taRoster) throws IOException {
            List<Course> courseList = new ArrayList<>();
            List<CourseType> courseTypeList = new ArrayList<>();
            String expectedLegend = "CRN,DEPT,CRS,SEC,DAY,START,END,BLDG,RM,COORD";
            String crn, department, courseNumber, sectionNumber, days, startTime, endTime, building, room, coordinatorName;
            int courseIndex;
            int curLine = 1;

            try {
                String line = bufferedReader.readLine();
                if (!line.equals(expectedLegend)) throw new IOException("Unexpected file format.");

                while ((line = bufferedReader.readLine()) != null) {
                    String[] values = line.split(",");
                    curLine++;

                    if (values.length <= 1) continue;
                    courseIndex = courseList.size();
                    crn = values[0];
                    department = values[1];
                    courseNumber = values[2];
                    sectionNumber = values[3];
                    days = values[4];
                    startTime = parseTimeString(values[5], curLine);
                    endTime = parseTimeString(values[6], curLine);
                    building = values[7];
                    room = values[8];
                    coordinatorName = values[9];

                    CourseType ct = new CourseType(courseIndex, crn, startTime, endTime, department, courseNumber,
                            sectionNumber, building, room, coordinatorName);
                    courseTypeList.add(ct);
                    courseTypeMap.put(crn, ct);

                    for (char d : days.toCharArray()) {
                        DayOfWeek dayOfWeek = DayOfWeek.valueOfAbbrev(String.valueOf(d));
                        if (dayOfWeek == null) throw new IllegalArgumentException("Invalid day on line " + curLine);

                        CourseDay cd = courseDayMap.get(String.valueOf(d));
                        Course course = new Course();
                        course.setCourseType(ct);
                        course.setCourseDay(cd);
                        cd.getCourseList().add(course);
                        course.setRequiredTaSize(1);
                        courseList.add(course);
                    }
                }

                taRoster.setCourseList(courseList);
                taRoster.setCourseTypeList(courseTypeList);
            }
            catch (Exception ex) {
                throw new IOException("Unexpected file format on line " + curLine);
            }
        }

        private void generateContract(TaRoster taRoster) {
            List<Contract> contractList = new ArrayList<>();
            List<ContractLine> contractLineList = new ArrayList<>();

            Contract contract = new Contract();
            contract.setId(0L);
            contract.setCode("0");
            contract.setDescription("Default contract");

            MinMaxContractLine cl = new MinMaxContractLine();
            cl.setId(0L);
            cl.setContract(contract);
            cl.setContractLineType(ContractLineType.TOTAL_ASSIGNMENTS);
            cl.setMinimumEnabled(true);
            cl.setMinimumValue(1);
            cl.setMinimumWeight(1);
            cl.setMaximumEnabled(true);
            cl.setMaximumValue(1);
            cl.setMaximumWeight(2);

            contractList.add(contract);
            contractLineList.add(cl);

            taRoster.setContractList(contractList);
            taRoster.setContractLineList(contractLineList);
        }

        private String parseTimeString(String time, int lineNum) throws IOException {
            String parsedTime;
            char[] vals = time.toCharArray();

            if (time.length() == 3) {
                parsedTime = "0" + String.valueOf(vals[0]) + ":" +
                        String.valueOf(vals[1]) + String.valueOf(vals[2]) + ":00";
            } else if (time.length() == 4) {
                parsedTime = String.valueOf(vals[0]) + String.valueOf(vals[1]) + ":" +
                        String.valueOf(vals[2]) + String.valueOf(vals[3]) + ":00";
            } else throw new IOException(String.format("Invalid time format on line {0}.", lineNum));

            return parsedTime;
        }

    }

    @Override
    public TxtInputBuilder createTxtInputBuilder() {
        return new CourseImporterTxtInputBuilder(taRoster);
    }

}
