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

package org.optaplanner.examples.nurserostering.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractXmlSolutionImporter;
import org.optaplanner.examples.nurserostering.domain.DayOfWeek;
import org.optaplanner.examples.nurserostering.domain.Ta;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.NurseRosterParametrization;
import org.optaplanner.examples.nurserostering.domain.Course;
import org.optaplanner.examples.nurserostering.domain.CourseAssignment;
import org.optaplanner.examples.nurserostering.domain.CourseDate;
import org.optaplanner.examples.nurserostering.domain.CourseType;
import org.optaplanner.examples.nurserostering.domain.WeekendDefinition;
import org.optaplanner.examples.nurserostering.domain.contract.BooleanContractLine;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;
import org.optaplanner.examples.nurserostering.domain.contract.ContractLine;
import org.optaplanner.examples.nurserostering.domain.contract.ContractLineType;
import org.optaplanner.examples.nurserostering.domain.contract.MinMaxContractLine;
import org.optaplanner.examples.nurserostering.domain.contract.PatternContractLine;
import org.optaplanner.examples.nurserostering.domain.pattern.FreeBefore2DaysWithAWorkDayPattern;
import org.optaplanner.examples.nurserostering.domain.pattern.Pattern;
import org.optaplanner.examples.nurserostering.domain.pattern.CourseType2DaysPattern;
import org.optaplanner.examples.nurserostering.domain.pattern.CourseType3DaysPattern;
import org.optaplanner.examples.nurserostering.domain.pattern.WorkBeforeFreeSequencePattern;
import org.optaplanner.examples.nurserostering.domain.request.DayOffRequest;
import org.optaplanner.examples.nurserostering.domain.request.DayOnRequest;
import org.optaplanner.examples.nurserostering.domain.request.CourseOffRequest;
import org.optaplanner.examples.nurserostering.domain.request.CourseOnRequest;

public class NurseRosteringImporter extends AbstractXmlSolutionImporter {

    public static void main(String[] args) {
        new NurseRosteringImporter().convertAll();
    }

    public NurseRosteringImporter() {
        super(new NurseRosteringDao());
    }

    public XmlInputBuilder createXmlInputBuilder() {
        return new NurseRosteringInputBuilder();
    }

    public static class NurseRosteringInputBuilder extends XmlInputBuilder {

        protected Map<String, CourseDate> courseDateMap;
        protected Map<String, CourseType> courseTypeMap;
        protected Map<List<String>, Course> dateAndCourseTypeToCourseMap;
        protected Map<List<Object>, List<Course>> dayOfWeekAndCourseTypeToCourseListMap;
        protected Map<String, Pattern> patternMap;
        protected Map<String, Contract> contractMap;
        protected Map<String, Ta> taMap;

        public Solution readSolution() throws IOException, JDOMException {
            // Note: javax.xml is terrible. JDom is much much easier.

            Element schedulingPeriodElement = document.getRootElement();
            assertElementName(schedulingPeriodElement, "SchedulingPeriod");
            NurseRoster nurseRoster = new NurseRoster();
            nurseRoster.setId(0L);
            nurseRoster.setCode(schedulingPeriodElement.getAttribute("ID").getValue());

            generateCourseDateList(nurseRoster,
                    schedulingPeriodElement.getChild("StartDate"),
                    schedulingPeriodElement.getChild("EndDate"));
            generateNurseRosterInfo(nurseRoster);
            readCourseTypeList(nurseRoster, schedulingPeriodElement.getChild("CourseTypes"));
            generateCourseList(nurseRoster);
            readPatternList(nurseRoster, schedulingPeriodElement.getChild("Patterns"));
            readContractList(nurseRoster, schedulingPeriodElement.getChild("Contracts"));
            readTaList(nurseRoster, schedulingPeriodElement.getChild("Tas"));
            readRequiredTaSizes(nurseRoster, schedulingPeriodElement.getChild("CoverRequirements"));
            readDayOffRequestList(nurseRoster, schedulingPeriodElement.getChild("DayOffRequests"));
            readDayOnRequestList(nurseRoster, schedulingPeriodElement.getChild("DayOnRequests"));
            readCourseOffRequestList(nurseRoster, schedulingPeriodElement.getChild("CourseOffRequests"));
            readCourseOnRequestList(nurseRoster, schedulingPeriodElement.getChild("CourseOnRequests"));
            createCourseAssignmentList(nurseRoster);

            BigInteger possibleSolutionSize = BigInteger.valueOf(nurseRoster.getTaList().size()).pow(
                    nurseRoster.getCourseAssignmentList().size());
            logger.info("NurseRoster {} has {} courseTypes, {} patterns, {} contracts, {} tas," +
                    " {} courseDates, {} courseAssignments and {} requests with a search space of {}.",
                    getInputId(),
                    nurseRoster.getCourseTypeList().size(),
                    nurseRoster.getPatternList().size(),
                    nurseRoster.getContractList().size(),
                    nurseRoster.getTaList().size(),
                    nurseRoster.getCourseDateList().size(),
                    nurseRoster.getCourseAssignmentList().size(),
                    nurseRoster.getDayOffRequestList().size() + nurseRoster.getDayOnRequestList().size()
                            + nurseRoster.getCourseOffRequestList().size() + nurseRoster.getCourseOnRequestList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return nurseRoster;
        }

        private void generateCourseDateList(NurseRoster nurseRoster,
                Element startDateElement, Element endDateElement) throws JDOMException {
            // Mimic JSR-310 LocalDate
            TimeZone LOCAL_TIMEZONE = TimeZone.getTimeZone("GMT");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(LOCAL_TIMEZONE);
            calendar.clear();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setCalendar(calendar);
            Date startDate;
            try {
                startDate = dateFormat.parse(startDateElement.getText());
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid startDate (" + startDateElement.getText() + ").", e);
            }
            calendar.setTime(startDate);
            int startDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            int startYear = calendar.get(Calendar.YEAR);
            Date endDate;
            try {
                endDate = dateFormat.parse(endDateElement.getText());
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid endDate (" + endDateElement.getText() + ").", e);
            }
            calendar.setTime(endDate);
            int endDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            int endYear = calendar.get(Calendar.YEAR);
            int maxDayIndex = endDayOfYear - startDayOfYear;
            if (startYear > endYear) {
                throw new IllegalStateException("The startYear (" + startYear
                        + " must be before endYear (" + endYear + ").");
            }
            if (startYear < endYear) {
                int tmpYear = startYear;
                calendar.setTime(startDate);
                while (tmpYear < endYear) {
                    maxDayIndex += calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
                    calendar.add(Calendar.YEAR, 1);
                    tmpYear++;
                }
            }
            int courseDateSize = maxDayIndex + 1;
            List<CourseDate> courseDateList = new ArrayList<CourseDate>(courseDateSize);
            courseDateMap = new HashMap<String, CourseDate>(courseDateSize);
            long id = 0L;
            int dayIndex = 0;
            calendar.setTime(startDate);
            for (int i = 0; i < courseDateSize; i++) {
                CourseDate courseDate = new CourseDate();
                courseDate.setId(id);
                courseDate.setDayIndex(dayIndex);
                String dateString = dateFormat.format(calendar.getTime());
                courseDate.setDateString(dateString);
                courseDate.setDayOfWeek(DayOfWeek.valueOfCalendar(calendar.get(Calendar.DAY_OF_WEEK)));
                courseDate.setCourseList(new ArrayList<Course>());
                courseDateList.add(courseDate);
                courseDateMap.put(dateString, courseDate);
                id++;
                dayIndex++;
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            nurseRoster.setCourseDateList(courseDateList);
        }

        private void generateNurseRosterInfo(NurseRoster nurseRoster) {
            List<CourseDate> courseDateList = nurseRoster.getCourseDateList();
            NurseRosterParametrization nurseRosterParametrization = new NurseRosterParametrization();
            nurseRosterParametrization.setFirstCourseDate(courseDateList.get(0));
            nurseRosterParametrization.setLastCourseDate(courseDateList.get(courseDateList.size() - 1));
            nurseRosterParametrization.setPlanningWindowStart(courseDateList.get(0));
            nurseRoster.setNurseRosterParametrization(nurseRosterParametrization);
        }

        private void readCourseTypeList(NurseRoster nurseRoster, Element courseTypesElement) throws JDOMException {
            List<Element> courseTypeElementList = (List<Element>) courseTypesElement.getChildren();
            List<CourseType> courseTypeList = new ArrayList<CourseType>(courseTypeElementList.size());
            courseTypeMap = new HashMap<String, CourseType>(courseTypeElementList.size());
            long id = 0L;
            int index = 0;
            for (Element element : courseTypeElementList) {
                assertElementName(element, "Course");
                CourseType courseType = new CourseType();
                courseType.setId(id);
                courseType.setCode(element.getAttribute("ID").getValue());
                courseType.setIndex(index);
                String startTimeString = element.getChild("StartTime").getText();
                courseType.setStartTimeString(startTimeString);
                String endTimeString = element.getChild("EndTime").getText();
                courseType.setEndTimeString(endTimeString);
                courseType.setNight(startTimeString.compareTo(endTimeString) > 0);
                courseType.setDescription(element.getChild("Description").getText());

                courseTypeList.add(courseType);
                if (courseTypeMap.containsKey(courseType.getCode())) {
                    throw new IllegalArgumentException("There are 2 courseTypes with the same code ("
                            + courseType.getCode() + ").");
                }
                courseTypeMap.put(courseType.getCode(), courseType);
                id++;
                index++;
            }
            nurseRoster.setCourseTypeList(courseTypeList);
        }

        private void generateCourseList(NurseRoster nurseRoster) throws JDOMException {
            List<CourseType> courseTypeList = nurseRoster.getCourseTypeList();
            int courseListSize = courseDateMap.size() * courseTypeList.size();
            List<Course> courseList = new ArrayList<Course>(courseListSize);
            dateAndCourseTypeToCourseMap = new HashMap<List<String>, Course>(courseListSize);
            dayOfWeekAndCourseTypeToCourseListMap = new HashMap<List<Object>, List<Course>>(7 * courseTypeList.size());
            long id = 0L;
            int index = 0;
            for (CourseDate courseDate : nurseRoster.getCourseDateList()) {
                for (CourseType courseType : courseTypeList) {
                    Course course = new Course();
                    course.setId(id);
                    course.setCourseDate(courseDate);
                    courseDate.getCourseList().add(course);
                    course.setCourseType(courseType);
                    course.setIndex(index);
                    course.setRequiredTaSize(0); // Filled in later
                    courseList.add(course);
                    dateAndCourseTypeToCourseMap.put(Arrays.asList(courseDate.getDateString(), courseType.getCode()), course);
                    addCourseToDayOfWeekAndCourseTypeToCourseListMap(courseDate, courseType, course);
                    id++;
                    index++;
                }
            }
            nurseRoster.setCourseList(courseList);
        }

        private void addCourseToDayOfWeekAndCourseTypeToCourseListMap(CourseDate courseDate, CourseType courseType,
                Course course) {
            List<Object> key = Arrays.<Object>asList(courseDate.getDayOfWeek(), courseType);
            List<Course> dayOfWeekAndCourseTypeToCourseList = dayOfWeekAndCourseTypeToCourseListMap.get(key);
            if (dayOfWeekAndCourseTypeToCourseList == null) {
                dayOfWeekAndCourseTypeToCourseList = new ArrayList<Course>((courseDateMap.size() + 6) / 7);
                dayOfWeekAndCourseTypeToCourseListMap.put(key, dayOfWeekAndCourseTypeToCourseList);
            }
            dayOfWeekAndCourseTypeToCourseList.add(course);
        }

        private void readPatternList(NurseRoster nurseRoster, Element patternsElement) throws JDOMException {
            List<Pattern> patternList;
            if (patternsElement == null) {
                patternList = Collections.emptyList();
            } else {
                List<Element> patternElementList = (List<Element>) patternsElement.getChildren();
                patternList = new ArrayList<Pattern>(patternElementList.size());
                patternMap = new HashMap<String, Pattern>(patternElementList.size());
                long id = 0L;
                long patternEntryId = 0L;
                for (Element element : patternElementList) {
                    assertElementName(element, "Pattern");
                    String code = element.getAttribute("ID").getValue();
                    int weight = element.getAttribute("weight").getIntValue();

                    List<Element> patternEntryElementList = (List<Element>) element.getChild("PatternEntries")
                            .getChildren();
                    if (patternEntryElementList.size() < 2) {
                        throw new IllegalArgumentException("The size of PatternEntries ("
                                + patternEntryElementList.size() + ") of pattern (" + code + ") should be at least 2.");
                    }
                    Pattern pattern;
                    if (patternEntryElementList.get(0).getChild("CourseType").getText().equals("None")) {
                        pattern = new FreeBefore2DaysWithAWorkDayPattern();
                        if (patternEntryElementList.size() != 3) {
                            throw new IllegalStateException("boe");
                        }
                    } else if (patternEntryElementList.get(1).getChild("CourseType").getText().equals("None")) {
                        pattern = new WorkBeforeFreeSequencePattern();
                        // TODO support this too (not needed for competition)
                        throw new UnsupportedOperationException("The pattern (" + code + ") is not supported."
                                + " None of the test data exhibits such a pattern.");
                    } else {
                        switch (patternEntryElementList.size()) {
                            case 2:
                                pattern = new CourseType2DaysPattern();
                                break;
                            case 3:
                                pattern = new CourseType3DaysPattern();
                                break;
                            default:
                                throw new IllegalArgumentException("A size of PatternEntries ("
                                        + patternEntryElementList.size() + ") of pattern (" + code
                                        + ") above 3 is not supported.");
                        }
                    }
                    pattern.setId(id);
                    pattern.setCode(code);
                    pattern.setWeight(weight);
                    int patternEntryIndex = 0;
                    DayOfWeek firstDayOfweek = null;
                    for (Element patternEntryElement : patternEntryElementList) {
                        assertElementName(patternEntryElement, "PatternEntry");
                        Element courseTypeElement = patternEntryElement.getChild("CourseType");
                        boolean courseTypeIsNone;
                        CourseType courseType;
                        if (courseTypeElement.getText().equals("Any")) {
                            courseTypeIsNone = false;
                            courseType = null;
                        } else if (courseTypeElement.getText().equals("None")) {
                            courseTypeIsNone = true;
                            courseType = null;
                        } else {
                            courseTypeIsNone = false;
                            courseType = courseTypeMap.get(courseTypeElement.getText());
                            if (courseType == null) {
                                throw new IllegalArgumentException("The courseType (" + courseTypeElement.getText()
                                        + ") of pattern (" + pattern.getCode() + ") does not exist.");
                            }
                        }
                        Element dayElement = patternEntryElement.getChild("Day");
                        DayOfWeek dayOfWeek;
                        if (dayElement.getText().equals("Any")) {
                            dayOfWeek = null;
                        } else {
                            dayOfWeek = DayOfWeek.valueOfCode(dayElement.getText());
                            if (dayOfWeek == null) {
                                throw new IllegalArgumentException("The dayOfWeek (" + dayElement.getText()
                                        + ") of pattern (" + pattern.getCode() + ") does not exist.");
                            }
                        }
                        if (patternEntryIndex == 0) {
                            firstDayOfweek = dayOfWeek;
                        } else {
                            if (firstDayOfweek != null) {
                                if (firstDayOfweek.getDistanceToNext(dayOfWeek) != patternEntryIndex) {
                                    throw new IllegalArgumentException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of pattern (" + pattern.getCode()
                                            + ") the dayOfWeek (" + dayOfWeek
                                            + ") is not valid with previous entries.");
                                }
                            } else {
                                if (dayOfWeek != null) {
                                    throw new IllegalArgumentException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of pattern (" + pattern.getCode()
                                            + ") the dayOfWeek should be (Any), in line with previous entries.");
                                }
                            }
                        }
                        if (pattern instanceof FreeBefore2DaysWithAWorkDayPattern) {
                            FreeBefore2DaysWithAWorkDayPattern castedPattern = (FreeBefore2DaysWithAWorkDayPattern) pattern;
                            if (patternEntryIndex == 0) {
                                if (dayOfWeek == null) {
                                    // TODO Support an any dayOfWeek too (not needed for competition)
                                    throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                            + ") the dayOfWeek should not be (Any)."
                                            + "\n None of the test data exhibits such a pattern.");
                                }
                                castedPattern.setFreeDayOfWeek(dayOfWeek);
                            }
                            if (patternEntryIndex == 1) {
                                if (courseType != null) {
                                    // TODO Support a specific courseType too (not needed for competition)
                                    throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                            + ") the courseType should be (Any)."
                                            + "\n None of the test data exhibits such a pattern.");
                                }
                                // castedPattern.setWorkCourseType(courseType);
                                // castedPattern.setWorkDayLength(patternEntryElementList.size() - 1);
                            }
                            // if (patternEntryIndex > 1 && courseType != castedPattern.getWorkCourseType()) {
                            //     throw new IllegalArgumentException("On patternEntryIndex (" + patternEntryIndex
                            //             + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                            //             + ") the courseType (" + courseType + ") should be ("
                            //             + castedPattern.getWorkCourseType() + ").");
                            // }
                            if (patternEntryIndex != 0 && courseTypeIsNone) {
                                throw new IllegalArgumentException("On patternEntryIndex (" + patternEntryIndex
                                        + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                        + ") the courseType can not be (None).");
                            }
                        } else if (pattern instanceof WorkBeforeFreeSequencePattern) {
                            WorkBeforeFreeSequencePattern castedPattern = (WorkBeforeFreeSequencePattern) pattern;
                            if (patternEntryIndex == 0) {
                                castedPattern.setWorkDayOfWeek(dayOfWeek);
                                castedPattern.setWorkCourseType(courseType);
                                castedPattern.setFreeDayLength(patternEntryElementList.size() - 1);
                            }
                            if (patternEntryIndex != 0 && !courseTypeIsNone) {
                                throw new IllegalArgumentException("On patternEntryIndex (" + patternEntryIndex
                                        + ") of WorkBeforeFreeSequence pattern (" + pattern.getCode()
                                        + ") the courseType should be (None).");
                            }
                        } else if (pattern instanceof CourseType2DaysPattern) {
                            CourseType2DaysPattern castedPattern = (CourseType2DaysPattern) pattern;
                            if (patternEntryIndex == 0) {
                                if (dayOfWeek != null) {
                                    // TODO Support a specific dayOfWeek too (not needed for competition)
                                    throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                            + ") the dayOfWeek should be (Any)."
                                            + "\n None of the test data exhibits such a pattern.");
                                }
                                // castedPattern.setStartDayOfWeek(dayOfWeek);
                            }
                            if (courseType == null) {
                                // TODO Support any courseType too (not needed for competition)
                                throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                        + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                        + ") the courseType should not be (Any)."
                                        + "\n None of the test data exhibits such a pattern.");
                            }
                            switch (patternEntryIndex) {
                                case 0:
                                    castedPattern.setDayIndex0CourseType(courseType);
                                    break;
                                case 1:
                                    castedPattern.setDayIndex1CourseType(courseType);
                                    break;
                                default:
                                    throw new IllegalArgumentException("The patternEntryIndex ("
                                            + patternEntryIndex + ") is not supported.");
                            }
                        } else if (pattern instanceof CourseType3DaysPattern) {
                            CourseType3DaysPattern castedPattern = (CourseType3DaysPattern) pattern;
                            if (patternEntryIndex == 0) {
                                if (dayOfWeek != null) {
                                    // TODO Support a specific dayOfWeek too (not needed for competition)
                                    throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                            + ") the dayOfWeek should be (Any)."
                                            + "\n None of the test data exhibits such a pattern.");
                                }
                                // castedPattern.setStartDayOfWeek(dayOfWeek);
                            }
                            if (courseType == null) {
                                // TODO Support any courseType too
                                throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                        + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                        + ") the courseType should not be (Any)."
                                        + "\n None of the test data exhibits such a pattern.");
                            }
                            switch (patternEntryIndex) {
                                case 0:
                                    castedPattern.setDayIndex0CourseType(courseType);
                                    break;
                                case 1:
                                    castedPattern.setDayIndex1CourseType(courseType);
                                    break;
                                case 2:
                                    castedPattern.setDayIndex2CourseType(courseType);
                                    break;
                                default:
                                    throw new IllegalArgumentException("The patternEntryIndex ("
                                            + patternEntryIndex + ") is not supported.");
                            }
                        } else {
                            throw new IllegalStateException("Unsupported patternClass (" + pattern.getClass() + ").");
                        }
                        patternEntryIndex++;
                    }
                    patternList.add(pattern);
                    if (patternMap.containsKey(pattern.getCode())) {
                        throw new IllegalArgumentException("There are 2 patterns with the same code ("
                                + pattern.getCode() + ").");
                    }
                    patternMap.put(pattern.getCode(), pattern);
                    id++;
                }
            }
            nurseRoster.setPatternList(patternList);
        }

        private void readContractList(NurseRoster nurseRoster, Element contractsElement) throws JDOMException {
            int contractLineTypeListSize = ContractLineType.values().length;
            List<Element> contractElementList = (List<Element>) contractsElement.getChildren();
            List<Contract> contractList = new ArrayList<Contract>(contractElementList.size());
            contractMap = new HashMap<String, Contract>(contractElementList.size());
            long id = 0L;
            List<ContractLine> contractLineList = new ArrayList<ContractLine>(
                    contractElementList.size() * contractLineTypeListSize);
            long contractLineId = 0L;
            List<PatternContractLine> patternContractLineList = new ArrayList<PatternContractLine>(
                    contractElementList.size() * 3);
            long patternContractLineId = 0L;
            for (Element element : contractElementList) {
                assertElementName(element, "Contract");
                Contract contract = new Contract();
                contract.setId(id);
                contract.setCode(element.getAttribute("ID").getValue());
                contract.setDescription(element.getChild("Description").getText());

                List<ContractLine> contractLineListOfContract = new ArrayList<ContractLine>(contractLineTypeListSize);
                contractLineId = readBooleanContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("SingleAssignmentPerDay"),
                        ContractLineType.SINGLE_ASSIGNMENT_PER_DAY);
                contractLineId = readMinMaxContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("MinNumAssignments"),
                        element.getChild("MaxNumAssignments"),
                        ContractLineType.TOTAL_ASSIGNMENTS);
                contractLineId = readMinMaxContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("MinConsecutiveWorkingDays"),
                        element.getChild("MaxConsecutiveWorkingDays"),
                        ContractLineType.CONSECUTIVE_WORKING_DAYS);
                contractLineId = readMinMaxContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("MinConsecutiveFreeDays"),
                        element.getChild("MaxConsecutiveFreeDays"),
                        ContractLineType.CONSECUTIVE_FREE_DAYS);
                contractLineId = readMinMaxContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("MinConsecutiveWorkingWeekends"),
                        element.getChild("MaxConsecutiveWorkingWeekends"),
                        ContractLineType.CONSECUTIVE_WORKING_WEEKENDS);
                contractLineId = readMinMaxContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, null,
                        element.getChild("MaxWorkingWeekendsInFourWeeks"),
                        ContractLineType.TOTAL_WORKING_WEEKENDS_IN_FOUR_WEEKS);
                WeekendDefinition weekendDefinition = WeekendDefinition.valueOfCode(
                        element.getChild("WeekendDefinition").getText());
                contract.setWeekendDefinition(weekendDefinition);
                contractLineId = readBooleanContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("CompleteWeekends"),
                        ContractLineType.COMPLETE_WEEKENDS);
                contractLineId = readBooleanContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("IdenticalCourseTypesDuringWeekend"),
                        ContractLineType.IDENTICAL_COURSE_TYPES_DURING_WEEKEND);
                contractLineId = readBooleanContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("NoNightCourseBeforeFreeWeekend"),
                        ContractLineType.NO_NIGHT_COURSE_BEFORE_FREE_WEEKEND);
                contract.setContractLineList(contractLineListOfContract);

                List<Element> unwantedPatternElementList = (List<Element>) element.getChild("UnwantedPatterns")
                        .getChildren();
                for (Element patternElement : unwantedPatternElementList) {
                    assertElementName(patternElement, "Pattern");
                    Pattern pattern = patternMap.get(patternElement.getText());
                    if (pattern == null) {
                        throw new IllegalArgumentException("The pattern (" + patternElement.getText()
                                + ") of contract (" + contract.getCode() + ") does not exist.");
                    }
                    PatternContractLine patternContractLine = new PatternContractLine();
                    patternContractLine.setId(patternContractLineId);
                    patternContractLine.setContract(contract);
                    patternContractLine.setPattern(pattern);
                    patternContractLineList.add(patternContractLine);
                    patternContractLineId++;
                }

                contractList.add(contract);
                if (contractMap.containsKey(contract.getCode())) {
                    throw new IllegalArgumentException("There are 2 contracts with the same code ("
                            + contract.getCode() + ").");
                }
                contractMap.put(contract.getCode(), contract);
                id++;
            }
            nurseRoster.setContractList(contractList);
            nurseRoster.setContractLineList(contractLineList);
            nurseRoster.setPatternContractLineList(patternContractLineList);
        }

        private long readBooleanContractLine(Contract contract, List<ContractLine> contractLineList,
                List<ContractLine> contractLineListOfContract, long contractLineId, Element element,
                ContractLineType contractLineType) throws DataConversionException {
            boolean enabled = Boolean.valueOf(element.getText());
            int weight;
            if (enabled) {
                weight = element.getAttribute("weight").getIntValue();
                if (weight < 0) {
                    throw new IllegalArgumentException("The weight (" + weight
                            + ") of contract (" + contract.getCode() + ") and contractLineType (" + contractLineType
                            + ") should be 0 or at least 1.");
                } else if (weight == 0) {
                    // If the weight is zero, the constraint should not be considered.
                    enabled = false;
                    logger.warn("In contract ({}), the contractLineType ({}) is enabled with weight 0.",
                            contract.getCode(), contractLineType);
                }
            } else {
                weight = 0;
            }
            if (enabled) {
                BooleanContractLine contractLine = new BooleanContractLine();
                contractLine.setId(contractLineId);
                contractLine.setContract(contract);
                contractLine.setContractLineType(contractLineType);
                contractLine.setEnabled(enabled);
                contractLine.setWeight(weight);
                contractLineList.add(contractLine);
                contractLineListOfContract.add(contractLine);
                contractLineId++;
            }
            return contractLineId;
        }

        private long readMinMaxContractLine(Contract contract, List<ContractLine> contractLineList,
                List<ContractLine> contractLineListOfContract, long contractLineId,
                Element minElement, Element maxElement,
                ContractLineType contractLineType) throws DataConversionException {
            boolean minimumEnabled = minElement == null ? false : minElement.getAttribute("on").getBooleanValue();
            int minimumWeight;
            if (minimumEnabled) {
                minimumWeight = minElement.getAttribute("weight").getIntValue();
                if (minimumWeight < 0) {
                    throw new IllegalArgumentException("The minimumWeight (" + minimumWeight
                            + ") of contract (" + contract.getCode() + ") and contractLineType (" + contractLineType
                            + ") should be 0 or at least 1.");
                } else if (minimumWeight == 0) {
                    // If the weight is zero, the constraint should not be considered.
                    minimumEnabled = false;
                    logger.warn("In contract ({}), the contractLineType ({}) minimum is enabled with weight 0.",
                            contract.getCode(), contractLineType);
                }
            } else {
                minimumWeight = 0;
            }
            boolean maximumEnabled = maxElement == null ? false : maxElement.getAttribute("on").getBooleanValue();
            int maximumWeight;
            if (maximumEnabled) {
                maximumWeight = maxElement.getAttribute("weight").getIntValue();
                if (maximumWeight < 0) {
                    throw new IllegalArgumentException("The maximumWeight (" + maximumWeight
                            + ") of contract (" + contract.getCode() + ") and contractLineType (" + contractLineType
                            + ") should be 0 or at least 1.");
                } else if (maximumWeight == 0) {
                    // If the weight is zero, the constraint should not be considered.
                    maximumEnabled = false;
                    logger.warn("In contract ({}), the contractLineType ({}) maximum is enabled with weight 0.",
                            contract.getCode(), contractLineType);
                }
            } else {
                maximumWeight = 0;
            }
            if (minimumEnabled || maximumEnabled) {
                MinMaxContractLine contractLine = new MinMaxContractLine();
                contractLine.setId(contractLineId);
                contractLine.setContract(contract);
                contractLine.setContractLineType(contractLineType);
                contractLine.setMinimumEnabled(minimumEnabled);
                if (minimumEnabled) {
                    int minimumValue = Integer.parseInt(minElement.getText());
                    if (minimumValue < 1) {
                        throw new IllegalArgumentException("The minimumValue (" + minimumValue
                                + ") of contract (" + contract.getCode() + ") and contractLineType ("
                                + contractLineType + ") should be at least 1.");
                    }
                    contractLine.setMinimumValue(minimumValue);
                    contractLine.setMinimumWeight(minimumWeight);
                }
                contractLine.setMaximumEnabled(maximumEnabled);
                if (maximumEnabled) {
                    int maximumValue = Integer.parseInt(maxElement.getText());
                    if (maximumValue < 0) {
                        throw new IllegalArgumentException("The maximumValue (" + maximumValue
                                + ") of contract (" + contract.getCode() + ") and contractLineType ("
                                + contractLineType + ") should be at least 0.");
                    }
                    contractLine.setMaximumValue(maximumValue);
                    contractLine.setMaximumWeight(maximumWeight);
                }
                contractLineList.add(contractLine);
                contractLineListOfContract.add(contractLine);
                contractLineId++;
            }
            return contractLineId;
        }

        private void readTaList(NurseRoster nurseRoster, Element tasElement) throws JDOMException {
            List<Element> taElementList = (List<Element>) tasElement.getChildren();
            List<Ta> taList = new ArrayList<Ta>(taElementList.size());
            taMap = new HashMap<String, Ta>(taElementList.size());
            long id = 0L;
            for (Element element : taElementList) {
                assertElementName(element, "Ta");
                Ta ta = new Ta();
                ta.setId(id);
                ta.setCode(element.getAttribute("ID").getValue());
                ta.setName(element.getChild("Name").getText());
                Element contractElement = element.getChild("ContractID");
                Contract contract = contractMap.get(contractElement.getText());
                if (contract == null) {
                    throw new IllegalArgumentException("The contract (" + contractElement.getText()
                            + ") of ta (" + ta.getCode() + ") does not exist.");
                }
                ta.setContract(contract);
                int estimatedRequestSize = (courseDateMap.size() / taElementList.size()) + 1;
                ta.setDayOffRequestMap(new HashMap<CourseDate, DayOffRequest>(estimatedRequestSize));
                ta.setDayOnRequestMap(new HashMap<CourseDate, DayOnRequest>(estimatedRequestSize));
                ta.setCourseOffRequestMap(new HashMap<Course, CourseOffRequest>(estimatedRequestSize));
                ta.setCourseOnRequestMap(new HashMap<Course, CourseOnRequest>(estimatedRequestSize));

                taList.add(ta);
                if (taMap.containsKey(ta.getCode())) {
                    throw new IllegalArgumentException("There are 2 tas with the same code ("
                            + ta.getCode() + ").");
                }
                taMap.put(ta.getCode(), ta);
                id++;
            }
            nurseRoster.setTaList(taList);
        }

        private void readRequiredTaSizes(NurseRoster nurseRoster, Element coverRequirementsElement) {
            List<Element> coverRequirementElementList = (List<Element>) coverRequirementsElement.getChildren();
            for (Element element : coverRequirementElementList) {
                if (element.getName().equals("DayOfWeekCover")) {
                    Element dayOfWeekElement = element.getChild("Day");
                    DayOfWeek dayOfWeek = DayOfWeek.valueOfCode(dayOfWeekElement.getText());
                    if (dayOfWeek == null) {
                        throw new IllegalArgumentException("The dayOfWeek (" + dayOfWeekElement.getText()
                                + ") of an entity DayOfWeekCover does not exist.");
                    }

                    List<Element> coverElementList = (List<Element>) element.getChildren("Cover");
                    for (Element coverElement : coverElementList) {
                        Element courseTypeElement = coverElement.getChild("Course");
                        CourseType courseType = courseTypeMap.get(courseTypeElement.getText());
                        if (courseType == null) {
                            if (courseTypeElement.getText().equals("Any")) {
                                throw new IllegalStateException("The courseType Any is not supported on DayOfWeekCover.");
                            } else if (courseTypeElement.getText().equals("None")) {
                                throw new IllegalStateException("The courseType None is not supported on DayOfWeekCover.");
                            } else {
                                throw new IllegalArgumentException("The courseType (" + courseTypeElement.getText()
                                        + ") of an entity DayOfWeekCover does not exist.");
                            }
                        }
                        List<Object> key = Arrays.<Object>asList(dayOfWeek, courseType);
                        List<Course> courseList = dayOfWeekAndCourseTypeToCourseListMap.get(key);
                        if (courseList == null) {
                            throw new IllegalArgumentException("The dayOfWeek (" + dayOfWeekElement.getText()
                                    + ") with the courseType (" + courseTypeElement.getText()
                                    + ") of an entity DayOfWeekCover does not have any courses.");
                        }
                        int requiredTaSize = Integer.parseInt(coverElement.getChild("Preferred").getText());
                        for (Course course : courseList) {
                            course.setRequiredTaSize(course.getRequiredTaSize() + requiredTaSize);
                        }
                    }
                } else if (element.getName().equals("DateSpecificCover")) {
                    Element dateElement = element.getChild("Date");
                    List<Element> coverElementList = (List<Element>) element.getChildren("Cover");
                    for (Element coverElement : coverElementList) {
                        Element courseTypeElement = coverElement.getChild("Course");
                        Course course = dateAndCourseTypeToCourseMap.get(Arrays.asList(dateElement.getText(), courseTypeElement.getText()));
                        if (course == null) {
                            throw new IllegalArgumentException("The date (" + dateElement.getText()
                                    + ") with the courseType (" + courseTypeElement.getText()
                                    + ") of an entity DateSpecificCover does not have a course.");
                        }
                        int requiredTaSize = Integer.parseInt(coverElement.getChild("Preferred").getText());
                        course.setRequiredTaSize(course.getRequiredTaSize() + requiredTaSize);
                    }
                } else {
                    throw new IllegalArgumentException("Unknown cover entity (" + element.getName() + ").");
                }
            }
        }

        private void readDayOffRequestList(NurseRoster nurseRoster, Element dayOffRequestsElement) throws JDOMException {
            List<DayOffRequest> dayOffRequestList;
            if (dayOffRequestsElement == null) {
                dayOffRequestList = Collections.emptyList();
            } else {
                List<Element> dayOffElementList = (List<Element>) dayOffRequestsElement.getChildren();
                dayOffRequestList = new ArrayList<DayOffRequest>(dayOffElementList.size());
                long id = 0L;
                for (Element element : dayOffElementList) {
                    assertElementName(element, "DayOff");
                    DayOffRequest dayOffRequest = new DayOffRequest();
                    dayOffRequest.setId(id);

                    Element taElement = element.getChild("TaID");
                    Ta ta = taMap.get(taElement.getText());
                    if (ta == null) {
                        throw new IllegalArgumentException("The courseDate (" + taElement.getText()
                                + ") of dayOffRequest (" + dayOffRequest + ") does not exist.");
                    }
                    dayOffRequest.setTa(ta);

                    Element dateElement = element.getChild("Date");
                    CourseDate courseDate = courseDateMap.get(dateElement.getText());
                    if (courseDate == null) {
                        throw new IllegalArgumentException("The date (" + dateElement.getText()
                                + ") of dayOffRequest (" + dayOffRequest + ") does not exist.");
                    }
                    dayOffRequest.setCourseDate(courseDate);

                    dayOffRequest.setWeight(element.getAttribute("weight").getIntValue());

                    dayOffRequestList.add(dayOffRequest);
                    ta.getDayOffRequestMap().put(courseDate, dayOffRequest);
                    id++;
                }
            }
            nurseRoster.setDayOffRequestList(dayOffRequestList);
        }

        private void readDayOnRequestList(NurseRoster nurseRoster, Element dayOnRequestsElement) throws JDOMException {
            List<DayOnRequest> dayOnRequestList;
            if (dayOnRequestsElement == null) {
                dayOnRequestList = Collections.emptyList();
            } else {
                List<Element> dayOnElementList = (List<Element>) dayOnRequestsElement.getChildren();
                dayOnRequestList = new ArrayList<DayOnRequest>(dayOnElementList.size());
                long id = 0L;
                for (Element element : dayOnElementList) {
                    assertElementName(element, "DayOn");
                    DayOnRequest dayOnRequest = new DayOnRequest();
                    dayOnRequest.setId(id);

                    Element taElement = element.getChild("TaID");
                    Ta ta = taMap.get(taElement.getText());
                    if (ta == null) {
                        throw new IllegalArgumentException("The courseDate (" + taElement.getText()
                                + ") of dayOnRequest (" + dayOnRequest + ") does not exist.");
                    }
                    dayOnRequest.setTa(ta);

                    Element dateElement = element.getChild("Date");
                    CourseDate courseDate = courseDateMap.get(dateElement.getText());
                    if (courseDate == null) {
                        throw new IllegalArgumentException("The date (" + dateElement.getText()
                                + ") of dayOnRequest (" + dayOnRequest + ") does not exist.");
                    }
                    dayOnRequest.setCourseDate(courseDate);

                    dayOnRequest.setWeight(element.getAttribute("weight").getIntValue());

                    dayOnRequestList.add(dayOnRequest);
                    ta.getDayOnRequestMap().put(courseDate, dayOnRequest);
                    id++;
                }
            }
            nurseRoster.setDayOnRequestList(dayOnRequestList);
        }

        private void readCourseOffRequestList(NurseRoster nurseRoster, Element courseOffRequestsElement) throws JDOMException {
            List<CourseOffRequest> courseOffRequestList;
            if (courseOffRequestsElement == null) {
                courseOffRequestList = Collections.emptyList();
            } else {
                List<Element> courseOffElementList = (List<Element>) courseOffRequestsElement.getChildren();
                courseOffRequestList = new ArrayList<CourseOffRequest>(courseOffElementList.size());
                long id = 0L;
                for (Element element : courseOffElementList) {
                    assertElementName(element, "CourseOff");
                    CourseOffRequest courseOffRequest = new CourseOffRequest();
                    courseOffRequest.setId(id);

                    Element taElement = element.getChild("TaID");
                    Ta ta = taMap.get(taElement.getText());
                    if (ta == null) {
                        throw new IllegalArgumentException("The course (" + taElement.getText()
                                + ") of courseOffRequest (" + courseOffRequest + ") does not exist.");
                    }
                    courseOffRequest.setTa(ta);

                    Element dateElement = element.getChild("Date");
                    Element courseTypeElement = element.getChild("CourseTypeID");
                    Course course = dateAndCourseTypeToCourseMap.get(Arrays.asList(dateElement.getText(), courseTypeElement.getText()));
                    if (course == null) {
                        throw new IllegalArgumentException("The date (" + dateElement.getText()
                                + ") or the courseType (" + courseTypeElement.getText()
                                + ") of courseOffRequest (" + courseOffRequest + ") does not exist.");
                    }
                    courseOffRequest.setCourse(course);

                    courseOffRequest.setWeight(element.getAttribute("weight").getIntValue());

                    courseOffRequestList.add(courseOffRequest);
                    ta.getCourseOffRequestMap().put(course, courseOffRequest);
                    id++;
                }
            }
            nurseRoster.setCourseOffRequestList(courseOffRequestList);
        }

        private void readCourseOnRequestList(NurseRoster nurseRoster, Element courseOnRequestsElement) throws JDOMException {
            List<CourseOnRequest> courseOnRequestList;
            if (courseOnRequestsElement == null) {
                courseOnRequestList = Collections.emptyList();
            } else {
                List<Element> courseOnElementList = (List<Element>) courseOnRequestsElement.getChildren();
                courseOnRequestList = new ArrayList<CourseOnRequest>(courseOnElementList.size());
                long id = 0L;
                for (Element element : courseOnElementList) {
                    assertElementName(element, "CourseOn");
                    CourseOnRequest courseOnRequest = new CourseOnRequest();
                    courseOnRequest.setId(id);

                    Element taElement = element.getChild("TaID");
                    Ta ta = taMap.get(taElement.getText());
                    if (ta == null) {
                        throw new IllegalArgumentException("The course (" + taElement.getText()
                                + ") of courseOnRequest (" + courseOnRequest + ") does not exist.");
                    }
                    courseOnRequest.setTa(ta);

                    Element dateElement = element.getChild("Date");
                    Element courseTypeElement = element.getChild("CourseTypeID");
                    Course course = dateAndCourseTypeToCourseMap.get(Arrays.asList(dateElement.getText(), courseTypeElement.getText()));
                    if (course == null) {
                        throw new IllegalArgumentException("The date (" + dateElement.getText()
                                + ") or the courseType (" + courseTypeElement.getText()
                                + ") of courseOnRequest (" + courseOnRequest + ") does not exist.");
                    }
                    courseOnRequest.setCourse(course);

                    courseOnRequest.setWeight(element.getAttribute("weight").getIntValue());

                    courseOnRequestList.add(courseOnRequest);
                    ta.getCourseOnRequestMap().put(course, courseOnRequest);
                    id++;
                }
            }
            nurseRoster.setCourseOnRequestList(courseOnRequestList);
        }

        private void createCourseAssignmentList(NurseRoster nurseRoster) {
            List<Course> courseList = nurseRoster.getCourseList();
            List<CourseAssignment> courseAssignmentList = new ArrayList<CourseAssignment>(courseList.size());
            long id = 0L;
            for (Course course : courseList) {
                for (int i = 0; i < course.getRequiredTaSize(); i++) {
                    CourseAssignment courseAssignment = new CourseAssignment();
                    courseAssignment.setId(id);
                    id++;
                    courseAssignment.setCourse(course);
                    courseAssignment.setIndexInCourse(i);
                    // Notice that we leave the PlanningVariable properties on null
                    courseAssignmentList.add(courseAssignment);
                }
            }
            nurseRoster.setCourseAssignmentList(courseAssignmentList);
        }

    }

}
