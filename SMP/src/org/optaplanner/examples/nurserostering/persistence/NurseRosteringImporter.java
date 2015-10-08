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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractXmlSolutionImporter;
import org.optaplanner.examples.nurserostering.domain.Coordinator;
import org.optaplanner.examples.nurserostering.domain.Course;
import org.optaplanner.examples.nurserostering.domain.CourseAssignment;
import org.optaplanner.examples.nurserostering.domain.CourseDay;
import org.optaplanner.examples.nurserostering.domain.CourseType;
import org.optaplanner.examples.nurserostering.domain.DayOfWeek;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.Ta;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;
import org.optaplanner.examples.nurserostering.domain.contract.ContractLine;
import org.optaplanner.examples.nurserostering.domain.contract.ContractLineType;
import org.optaplanner.examples.nurserostering.domain.contract.MinMaxContractLine;
import org.optaplanner.examples.nurserostering.domain.request.CourseOffRequest;
import org.optaplanner.examples.nurserostering.domain.request.CourseOnRequest;

public class NurseRosteringImporter extends AbstractXmlSolutionImporter {

    public static void main(String[] args) {
        new NurseRosteringImporter().convertAll();
    }

    public NurseRosteringImporter() {
        super(new NurseRosteringDao());
    }

    @Override
    public XmlInputBuilder createXmlInputBuilder() {
        return new NurseRosteringInputBuilder();
    }

    public static class NurseRosteringInputBuilder extends XmlInputBuilder {

        protected Map<String, CourseDay> courseDayMap;
        protected Map<String, CourseType> courseTypeMap;
        protected Map<List<String>, Course> dayAndCourseTypeToCourseMap;
        protected Map<List<Object>, List<Course>> dayOfWeekAndCourseTypeToCourseListMap;
        protected Map<String, Contract> contractMap;
        protected Map<String, Ta> taMap;
        protected Map<String, Coordinator> coordinatorMap;
        protected Map<String, String> courseTypeToCoordinatorMap; // Map coordinator id to courseTYpeId

        @Override
        public Solution readSolution() throws IOException, JDOMException {
            // Note: javax.xml is terrible. JDom is much much easier.

            Element schedulingPeriodElement = document.getRootElement();
            assertElementName(schedulingPeriodElement, "SchedulingPeriod");
            NurseRoster nurseRoster = new NurseRoster();
            nurseRoster.setId(0L);
            nurseRoster.setCode(schedulingPeriodElement.getAttribute("ID").getValue());

            generateCourseDayList(nurseRoster);
            readCourseTypeList(nurseRoster, schedulingPeriodElement.getChild("CourseTypes"));
            generateCourseList(nurseRoster);
            readCoordinatorList(nurseRoster, schedulingPeriodElement.getChild("Coordinators"));
            readCoordinatorToCourseTypeMapping(nurseRoster, schedulingPeriodElement.getChild("CoordinatorToCourseType"));
            readContractList(nurseRoster, schedulingPeriodElement.getChild("Contracts"));
            readTaList(nurseRoster, schedulingPeriodElement.getChild("Tas"));
            readRequiredTaSizes(nurseRoster, schedulingPeriodElement.getChild("CoverRequirements"));
            readCourseOffRequestList(nurseRoster, schedulingPeriodElement.getChild("CourseOffRequests"));
            readCourseOnRequestList(nurseRoster, schedulingPeriodElement.getChild("CourseOnRequests"));
            createCourseAssignmentList(nurseRoster);

            BigInteger possibleSolutionSize = BigInteger.valueOf(nurseRoster.getTaList().size()).pow(
                    nurseRoster.getCourseAssignmentList().size());
            logger.info("NurseRoster {} has {} courseTypes, {} contracts, {} tas," +
                    " {} courseDays, {} courseAssignments and {} requests with a search space of {}.",
                    getInputId(),
                    nurseRoster.getCourseTypeList().size(),
                    nurseRoster.getContractList().size(),
                    nurseRoster.getTaList().size(),
                    nurseRoster.getCourseDayList().size(),
                    nurseRoster.getCourseAssignmentList().size(),
                    nurseRoster.getCourseOffRequestList().size() + nurseRoster.getCourseOnRequestList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return nurseRoster;
        }

        private void readCoordinatorToCourseTypeMapping(NurseRoster nurseRoster,
                Element coordinatorsElement) throws JDOMException{
            if (coordinatorsElement == null) {
                return;
            }
            List<Element> coordinatorMappingElementList = coordinatorsElement.getChildren();
            courseTypeToCoordinatorMap = new HashMap<>(coordinatorMappingElementList.size());
            for (Element element : coordinatorMappingElementList) {
                assertElementName(element, "Mapping");
                String coordinatorId = element.getChild("CoordinatorId").getText();
                String courseTypeId = element.getChild("CourseTypeId").getText();

                if (coordinatorMap.containsKey(coordinatorId)) {
                    if (courseTypeMap.containsKey(courseTypeId)) {
                        if (courseTypeToCoordinatorMap.containsKey(courseTypeId)) {
                            throw new IllegalArgumentException("Coordinator "
                                    +  courseTypeToCoordinatorMap.get(courseTypeId) + " is already assigned to courseType "
                                    + courseTypeId);
                        }
                        else {
                            Coordinator coordinator = coordinatorMap.get(coordinatorId);
                            CourseType courseType = courseTypeMap.get(courseTypeId);
                            ArrayList<CourseType> courseTypeList = (ArrayList<CourseType>) coordinator.getCourseTypes();
                            courseTypeList.add(courseType);
                            coordinator.setCourseTypes(courseTypeList);
                            coordinator.setCourseTypes(courseTypeList);
                            courseTypeToCoordinatorMap.put(courseTypeId, coordinatorId);
                        }
                    }
                    else {
                        throw new IllegalArgumentException("CourseType "
                                + courseTypeId + " does not exist");
                    }
                }
                else {
                    throw new IllegalArgumentException("Coordinator "
                                + coordinatorId + " does not exist");
                }
            }
        }

        private void readCoordinatorList(NurseRoster nurseRoster, Element coordinatorsElement)
            throws JDOMException{
            List<Coordinator> coordinatorList;
            if (coordinatorsElement == null) {
                coordinatorList = Collections.emptyList();
            }
            else {
                List<Element> coordinatorsElementList = coordinatorsElement.getChildren();
                coordinatorList = new ArrayList<>(coordinatorsElementList.size());
                coordinatorMap = new HashMap<>(coordinatorsElementList.size());
                long id = 0L;
                for (Element element : coordinatorsElementList) {
                    assertElementName(element, "Coordinator");
                    String code = element.getAttribute("ID").getValue();

                    if (coordinatorMap.containsKey(code)) {
                        throw new IllegalArgumentException("Coordinator "
                                    + code + " already exists");
                    }
                    else {
                        Coordinator coordinator = new Coordinator();
                        coordinator.setId(id);
                        coordinator.setCode(code);
                        coordinator.setName(element.getChild("Name").getText());
                        coordinator.setEmail(element.getChild("Email").getText());
                        coordinator.setCourseTypes(new ArrayList<CourseType>());
                        coordinatorList.add(coordinator);
                        coordinatorMap.put(code,coordinator);
                    }
                }
            }
            nurseRoster.setCoordinatorList(coordinatorList);
        }

        private void generateCourseDayList(NurseRoster nurseRoster) {
            int courseDaySize = DayOfWeek.values().length;
            List<CourseDay> courseDayList = new ArrayList<>(courseDaySize);
            courseDayMap = new HashMap<>(courseDaySize);
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
                courseDayMap.put(dayString, courseDay);
                id++;
                dayIndex++;
            }
            nurseRoster.setCourseDayList(courseDayList);
        }

        private void readCourseTypeList(NurseRoster nurseRoster, Element courseTypesElement) throws JDOMException {
            List<Element> courseTypeElementList = courseTypesElement.getChildren();
            List<CourseType> courseTypeList = new ArrayList<>(courseTypeElementList.size());
            courseTypeMap = new HashMap<>(courseTypeElementList.size());
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
                String dept = element.getChild("Dept").getText();
                courseType.setDept(dept);
                String crs = element.getChild("Crs").getText();
                courseType.setCrs(crs);
                String sec = element.getChild("Sec").getText();
                courseType.setSec(sec);
                String bldg = element.getChild("Bldg").getText();
                courseType.setBldg(bldg);
                String rm = element.getChild("Rm").getText();
                courseType.setRm(rm);

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
            int courseListSize = courseDayMap.size() * courseTypeList.size();
            List<Course> courseList = new ArrayList<>(courseListSize);
            dayAndCourseTypeToCourseMap = new HashMap<>(courseListSize);
            dayOfWeekAndCourseTypeToCourseListMap = new HashMap<>(7 * courseTypeList.size());
            long id = 0L;
            int index = 0;
            for (CourseDay courseDay : nurseRoster.getCourseDayList()) {
                for (CourseType courseType : courseTypeList) {
                    Course course = new Course();
                    course.setId(id);
                    course.setCourseDay(courseDay);
                    courseDay.getCourseList().add(course);
                    course.setCourseType(courseType);
                    course.setIndex(index);
                    course.setRequiredTaSize(0); // Filled in later
                    courseList.add(course);
                    dayAndCourseTypeToCourseMap.put(Arrays.asList(courseDay.getDayString(), courseType.getCode()), course);
                    addCourseToDayOfWeekAndCourseTypeToCourseListMap(courseDay, courseType, course);
                    id++;
                    index++;
                }
            }
            nurseRoster.setCourseList(courseList);
        }

        private void addCourseToDayOfWeekAndCourseTypeToCourseListMap(CourseDay courseDay, CourseType courseType,
                Course course) {
            List<Object> key = Arrays.<Object>asList(courseDay.getDayOfWeek(), courseType);
            List<Course> dayOfWeekAndCourseTypeToCourseList = dayOfWeekAndCourseTypeToCourseListMap.get(key);
            if (dayOfWeekAndCourseTypeToCourseList == null) {
                dayOfWeekAndCourseTypeToCourseList = new ArrayList<>((courseDayMap.size() + 6) / 7);
                dayOfWeekAndCourseTypeToCourseListMap.put(key, dayOfWeekAndCourseTypeToCourseList);
            }
            dayOfWeekAndCourseTypeToCourseList.add(course);
        }

        private void readContractList(NurseRoster nurseRoster, Element contractsElement) throws JDOMException {
            int contractLineTypeListSize = ContractLineType.values().length;
            List<Element> contractElementList = contractsElement.getChildren();
            List<Contract> contractList = new ArrayList<>(contractElementList.size());
            contractMap = new HashMap<>(contractElementList.size());
            long id = 0L;
            List<ContractLine> contractLineList = new ArrayList<>(
                    contractElementList.size() * contractLineTypeListSize);
            long contractLineId = 0L;
            for (Element element : contractElementList) {
                assertElementName(element, "Contract");
                Contract contract = new Contract();
                contract.setId(id);
                contract.setCode(element.getAttribute("ID").getValue());
                contract.setDescription(element.getChild("Description").getText());

                List<ContractLine> contractLineListOfContract = new ArrayList<>(contractLineTypeListSize);
                contractLineId = readMinMaxContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("MinNumAssignments"),
                        element.getChild("MaxNumAssignments"),
                        ContractLineType.TOTAL_ASSIGNMENTS);

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
            List<Element> taElementList = tasElement.getChildren();
            List<Ta> taList = new ArrayList<>(taElementList.size());
            taMap = new HashMap<>(taElementList.size());
            long id = 0L;
            for (Element element : taElementList) {
                assertElementName(element, "Ta");
                Ta ta = new Ta();
                ta.setId(id);
                ta.setCode(element.getAttribute("ID").getValue());
                ta.setName(element.getChild("Name").getText());
                ta.setEmail(element.getChild("Email").getText());
                Element contractElement = element.getChild("ContractID");
                Contract contract = contractMap.get(contractElement.getText());
                if (contract == null) {
                    throw new IllegalArgumentException("The contract (" + contractElement.getText()
                            + ") of ta (" + ta.getCode() + ") does not exist.");
                }
                ta.setContract(contract);
                int estimatedRequestSize = (courseDayMap.size() / taElementList.size()) + 1;
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
            List<Element> coverRequirementElementList = coverRequirementsElement.getChildren();
            for (Element element : coverRequirementElementList) {
                switch (element.getName()) {
                    case "DayOfWeekCover":
                        {
                            Element dayOfWeekElement = element.getChild("Day");
                            DayOfWeek dayOfWeek = DayOfWeek.valueOfCode(dayOfWeekElement.getText());
                            if (dayOfWeek == null) {
                                throw new IllegalArgumentException("The dayOfWeek (" + dayOfWeekElement.getText()
                                        + ") of an entity DayOfWeekCover does not exist.");
                            }       List<Element> coverElementList = element.getChildren("Cover");
                            for (Element coverElement : coverElementList) {
                                Element courseTypeElement = coverElement.getChild("Course");
                                CourseType courseType = courseTypeMap.get(courseTypeElement.getText());
                                if (courseType == null) {
                                    switch (courseTypeElement.getText()) {
                                        case "Any":
                                            throw new IllegalStateException("The courseType Any is not supported on DayOfWeekCover.");
                                        case "None":
                                            throw new IllegalStateException("The courseType None is not supported on DayOfWeekCover.");
                                        default:
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
                                }       int requiredTaSize = Integer.parseInt(coverElement.getChild("Preferred").getText());
                                for (Course course : courseList) {
                                    course.setRequiredTaSize(course.getRequiredTaSize() + requiredTaSize);
                        }   }
                            break;
                        }
                    case "DaySpecificCover":
                    {
                        Element dayElement = element.getChild("Day");
                        List<Element> coverElementList = element.getChildren("Cover");
                        for (Element coverElement : coverElementList) {
                            Element courseTypeElement = coverElement.getChild("Course");
                            Course course = dayAndCourseTypeToCourseMap.get(Arrays.asList(dayElement.getText(), courseTypeElement.getText()));
                            if (course == null) {
                                throw new IllegalArgumentException("The day (" + dayElement.getText()
                                        + ") with the courseType (" + courseTypeElement.getText()
                                        + ") of an entity DaySpecificCover does not have a course.");
                            }
                            int requiredTaSize = Integer.parseInt(coverElement.getChild("Preferred").getText());
                            course.setRequiredTaSize(course.getRequiredTaSize() + requiredTaSize);
                        }       break;
                    }
                    default:
                        throw new IllegalArgumentException("Unknown cover entity (" + element.getName() + ").");
                }
            }
        }

        private void readCourseOffRequestList(NurseRoster nurseRoster, Element courseOffRequestsElement) throws JDOMException {
            List<CourseOffRequest> courseOffRequestList;
            if (courseOffRequestsElement == null) {
                courseOffRequestList = Collections.emptyList();
            } else {
                List<Element> courseOffElementList = courseOffRequestsElement.getChildren();
                courseOffRequestList = new ArrayList<>(courseOffElementList.size());
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

                    String day = element.getChild("Day").getText();
                    Element courseTypeElement = element.getChild("CourseTypeID");
                    Course course = dayAndCourseTypeToCourseMap.get(Arrays.asList(day, courseTypeElement.getText()));
                    if (course == null) {
                        throw new IllegalArgumentException("The day (" + day
                                + ") or the courseType (" + courseTypeElement.getText()
                                + ") of courseOffRequest (" + courseOffRequest + ") does not exist.");
                    }
                    courseOffRequest.setCourse(course);

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
                List<Element> courseOnElementList = courseOnRequestsElement.getChildren();
                courseOnRequestList = new ArrayList<>(courseOnElementList.size());
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

                    String day = element.getChild("Day").getText();
                    Element courseTypeElement = element.getChild("CourseTypeID");
                    Course course = dayAndCourseTypeToCourseMap.get(Arrays.asList(day, courseTypeElement.getText()));
                    if (course == null) {
                        throw new IllegalArgumentException("The day (" + day
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
            List<CourseAssignment> courseAssignmentList = new ArrayList<>(courseList.size());
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
