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

package org.optaplanner.examples.tarostering.persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.persistence.AbstractXmlSolutionImporter;
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

public class TaRosteringImporter extends AbstractXmlSolutionImporter {

    public static void main(String[] args) {
        new TaRosteringImporter().convertAll();
    }

    public TaRosteringImporter() {
        super(new TaRosteringDao());
    }

    @Override
    public XmlInputBuilder createXmlInputBuilder() {
        return new TaRosteringInputBuilder();
    }

    public static class TaRosteringInputBuilder extends XmlInputBuilder {

        protected Map<String, CourseDay> courseDayMap;
        protected Map<String, CourseType> courseTypeMap;
        protected Map<CourseType, Integer> courseTypeToRequiredTaSizeMap;
        protected Map<CourseType, Set<DayOfWeek>> courseTypeToDayOfWeekCoverMap;
        protected Map<String, Contract> contractMap;
        protected Map<String, Ta> taMap;
        protected Map<String, String> courseTypeToCoordinatorMap; // Map coordinator id to courseTYpeId

        @Override
        public Solution readSolution() throws IOException, JDOMException {
            // Note: javax.xml is terrible. JDom is much much easier.

            Element schedulingPeriodElement = document.getRootElement();
            assertElementName(schedulingPeriodElement, "SchedulingPeriod");
            TaRoster taRoster = new TaRoster();
            taRoster.setId(0L);
            taRoster.setCode(schedulingPeriodElement.getAttribute("ID").getValue());

            generateCourseDayList(taRoster);
            readCourseTypeList(taRoster, schedulingPeriodElement.getChild("CourseTypes"));
            readCourseTypeCoverRequirements(taRoster, schedulingPeriodElement.getChild("CoverRequirements"));
            generateCourseList(taRoster);
            readContractList(taRoster, schedulingPeriodElement.getChild("Contracts"));

            return taRoster;
        }

        private void generateCourseDayList(TaRoster taRoster) {
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
            taRoster.setCourseDayList(courseDayList);
        }

        private void readCourseTypeList(TaRoster taRoster, Element courseTypesElement) throws JDOMException {
            List<Element> courseTypeElementList = courseTypesElement.getChildren();
            List<CourseType> courseTypeList = new ArrayList<>(courseTypeElementList.size());
            courseTypeMap = new HashMap<>(courseTypeElementList.size());
            long id = 0L;
            int index = 0;
            for (Element element : courseTypeElementList) {
                assertElementName(element, "Course");
                CourseType courseType = new CourseType();
                courseType.setId(id);
                courseType.setCrn(element.getAttribute("ID").getValue());
                String startTimeString = element.getChild("StartTime").getText();
                courseType.setStartTimeString(startTimeString);
                String endTimeString = element.getChild("EndTime").getText();
                courseType.setEndTimeString(endTimeString);
                String dept = element.getChild("Dept").getText();
                courseType.setDepartment(dept);
                String crs = element.getChild("Crs").getText();
                courseType.setCourseNumber(crs);
                String sec = element.getChild("Sec").getText();
                courseType.setSectionNumber(sec);
                String bldg = element.getChild("Bldg").getText();
                courseType.setBuilding(bldg);
                String rm = element.getChild("Rm").getText();
                courseType.setRoomNumber(rm);

                courseTypeList.add(courseType);
                if (courseTypeMap.containsKey(courseType.getCrn())) {
                    throw new IllegalArgumentException("There are 2 courseTypes with the same code ("
                            + courseType.getCrn() + ").");
                }
                courseTypeMap.put(courseType.getCrn(), courseType);
                id++;
                index++;
            }
            taRoster.setCourseTypeList(courseTypeList);
        }


        private void generateCourseList(TaRoster taRoster) throws JDOMException {
            List<Course> courseList = new ArrayList<>();
            long id = 0L;
            int index = 0;
            for (CourseType courseType : taRoster.getCourseTypeList()) {
                Set<DayOfWeek> dayOfWeekSet = courseTypeToDayOfWeekCoverMap.get(courseType);
                if (dayOfWeekSet != null) {
                    int preferredSize = courseTypeToRequiredTaSizeMap.get(courseType);
                    for (DayOfWeek d : dayOfWeekSet) {
                        Course course = new Course();
                        course.setId(id);
                        CourseDay courseDay = courseDayMap.get(d.getCode());
                        course.setCourseDay(courseDay);
                        courseDay.getCourseList().add(course);
                        course.setRequiredTaSize(preferredSize);
                        course.setCourseType(courseType);
                        courseList.add(course);
                        id++;
                        index++;
                    }
                }
            }

            taRoster.setCourseList(courseList);
        }

        private void readContractList(TaRoster taRoster, Element contractsElement) throws JDOMException {
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
            taRoster.setContractList(contractList);
            taRoster.setContractLineList(contractLineList);
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

        private void readTaList(TaRoster taRoster, Element tasElement) throws JDOMException {
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
            taRoster.setTaList(taList);
        }

        private void readCourseTypeCoverRequirements(TaRoster taRoster, Element coverRequirementsElement) {
            courseTypeToRequiredTaSizeMap = new HashMap<>();
            courseTypeToDayOfWeekCoverMap = new HashMap<>();
            List<Element> coverRequirementElementList = coverRequirementsElement.getChildren();
            for (Element element : coverRequirementElementList) {
                assertElementName(element, "CourseCover");
                CourseType courseType = courseTypeMap.get(element.getChild("Course").getText());
                if (courseType == null) {
                    throw new IllegalArgumentException("Not a valid coursetype"
                            + " in coursecover");
                }
                int preferredSize = Integer.parseInt(element.getChild("Preferred").getText());
                Element days = element.getChild("Days");
                Set<DayOfWeek> dayOfWeekSet = new HashSet<>();
                if (days.getChildren().isEmpty()){
                    throw new IllegalArgumentException("No day coverage listed"
                            + " in coverrequirements");
                }
                for (Element e : days.getChildren()) {
                    assertElementName(e, "Day");
                    DayOfWeek dayOfWeek = DayOfWeek.valueOfCode(e.getText());
                    if (dayOfWeek == null) {
                        throw new IllegalArgumentException("Not a valid"
                                + " dayofweek in coverrequirements");
                    }
                    dayOfWeekSet.add(dayOfWeek);
                }
                if (courseTypeToRequiredTaSizeMap.containsKey(courseType)) {
                    throw new IllegalArgumentException("Coursetype is already"
                            + " defined in coverrequirements");
                }
                courseTypeToRequiredTaSizeMap.put(courseType, preferredSize);
                courseTypeToDayOfWeekCoverMap.put(courseType, dayOfWeekSet);
            }
        }

        private void readCourseOffRequestList(TaRoster taRoster, Element courseOffRequestsElement) throws JDOMException {
            List<CourseOffRequest> courseOffRequestList;
            if (courseOffRequestsElement == null) {
                courseOffRequestList = Collections.emptyList();
            } else {
                List<Element> courseOffElementList = courseOffRequestsElement.getChildren();
                courseOffRequestList = new ArrayList<>(courseOffElementList.size());
                long id = 0L;
                for (Element element : courseOffElementList) {
                    assertElementName(element, "CourseOff");

                    Element taElement = element.getChild("TaID");
                    Ta ta = taMap.get(taElement.getText());

                    if (ta == null) {
                        throw new IllegalArgumentException("The course (" + taElement.getText()
                                + ") of courseOffRequest () does not exist.");
                    }

                    Element courseTypeElement = element.getChild("CourseTypeID");
                    List<Course> courseList = taRoster.getCourseList();
                    for (Course crs : courseList) {
                        if (crs.getCourseType().getCrn().equals(courseTypeElement.getText())){
                            CourseOffRequest courseOffRequest = new CourseOffRequest();
                            courseOffRequest.setId(id);
                            courseOffRequest.setTa(ta);
                            courseOffRequest.setCourse(crs);
                            courseOffRequestList.add(courseOffRequest);
                            ta.getCourseOffRequestMap().put(crs, courseOffRequest);
                            id++;
                        }
                    }
                }
            }
            taRoster.setCourseOffRequestList(courseOffRequestList);
        }

        private void readCourseOnRequestList(TaRoster taRoster, Element courseOnRequestsElement) throws JDOMException {
            List<CourseOnRequest> courseOnRequestList;
            if (courseOnRequestsElement == null) {
                courseOnRequestList = Collections.emptyList();
            } else {
                List<Element> courseOnElementList = courseOnRequestsElement.getChildren();
                courseOnRequestList = new ArrayList<>(courseOnElementList.size());
                long id = 0L;
                for (Element element : courseOnElementList) {
                    assertElementName(element, "CourseOn");

                    Element taElement = element.getChild("TaID");
                    Ta ta = taMap.get(taElement.getText());
                    if (ta == null) {
                        throw new IllegalArgumentException("The course (" + taElement.getText()
                                + ") of courseOnRequest (s) does not exist.");
                    }

                    Element courseTypeElement = element.getChild("CourseTypeID");
                    List<Course> courseList = taRoster.getCourseList();
                    for (Course crs : courseList) {
                        if (crs.getCourseType().getCrn().equals(courseTypeElement.getText())){
                            CourseOnRequest courseOnRequest = new CourseOnRequest();
                            courseOnRequest.setId(id);
                            courseOnRequest.setTa(ta);
                            courseOnRequest.setCourse(crs);
                            courseOnRequest.setWeight(element.getAttribute("weight").getIntValue());
                            courseOnRequestList.add(courseOnRequest);
                            ta.getCourseOnRequestMap().put(crs, courseOnRequest);
                            id++;
                        }
                    }
                }
            }
            taRoster.setCourseOnRequestList(courseOnRequestList);
        }

        private void createCourseAssignmentList(TaRoster taRoster) {
            List<Course> courseList = taRoster.getCourseList();
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
            courseAssignmentList = sortCourseAssignmentByDay(courseAssignmentList);
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

    }

}
