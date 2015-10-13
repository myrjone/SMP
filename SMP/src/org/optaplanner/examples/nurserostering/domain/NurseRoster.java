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
import com.thoughtworks.xstream.annotations.XStreamConverter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.tarostering.domain.contract.Contract;
import org.optaplanner.examples.tarostering.domain.contract.ContractLine;
import org.optaplanner.examples.tarostering.domain.request.CourseOffRequest;
import org.optaplanner.examples.tarostering.domain.request.CourseOnRequest;
import org.optaplanner.persistence.xstream.impl.score.XStreamScoreConverter;

@PlanningSolution
@XStreamAlias("TaRoster")
public class TaRoster extends AbstractPersistable implements Solution<HardSoftScore> {

    private String code;

    private List<CourseType> courseTypeList;
    private List<Contract> contractList;
    private List<ContractLine> contractLineList;
    private List<Ta> taList;
    private List<CourseDay> courseDayList;
    private List<Course> courseList;
    private List<CourseOffRequest> courseOffRequestList;
    private List<CourseOnRequest> courseOnRequestList;
    private List<Coordinator> coordinatorList;

    private List<CourseAssignment> courseAssignmentList;

    @XStreamConverter(value = XStreamScoreConverter.class, types = {HardSoftScoreDefinition.class})
    private HardSoftScore score;

    public List<Coordinator> getCoordinatorList() {
        return coordinatorList;
    }

    public void setCoordinatorList(List<Coordinator> coordinatorList) {
        this.coordinatorList = coordinatorList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<CourseType> getCourseTypeList() {
        return courseTypeList;
    }

    public void setCourseTypeList(List<CourseType> courseTypeList) {
        this.courseTypeList = courseTypeList;
    }

    public List<Contract> getContractList() {
        return contractList;
    }

    public void setContractList(List<Contract> contractList) {
        this.contractList = contractList;
    }

    public List<ContractLine> getContractLineList() {
        return contractLineList;
    }

    public void setContractLineList(List<ContractLine> contractLineList) {
        this.contractLineList = contractLineList;
    }

    @ValueRangeProvider(id = "taRange")
    public List<Ta> getTaList() {
        return taList;
    }

    public void setTaList(List<Ta> taList) {
        this.taList = taList;
    }

    public List<CourseDay> getCourseDayList() {
        return courseDayList;
    }

    public void setCourseDayList(List<CourseDay> courseDayList) {
        this.courseDayList = courseDayList;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public List<CourseOffRequest> getCourseOffRequestList() {
        return courseOffRequestList;
    }

    public void setCourseOffRequestList(List<CourseOffRequest> courseOffRequestList) {
        this.courseOffRequestList = courseOffRequestList;
    }

    public List<CourseOnRequest> getCourseOnRequestList() {
        return courseOnRequestList;
    }

    public void setCourseOnRequestList(List<CourseOnRequest> courseOnRequestList) {
        this.courseOnRequestList = courseOnRequestList;
    }

    @PlanningEntityCollectionProperty
    public List<CourseAssignment> getCourseAssignmentList() {
        return courseAssignmentList;
    }

    public void setCourseAssignmentList(List<CourseAssignment> courseAssignmentList) {
        this.courseAssignmentList = courseAssignmentList;
    }

    @Override
    public HardSoftScore getScore() {
        return score;
    }

    @Override
    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @Override
    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<>();
        facts.addAll(courseTypeList);
        facts.addAll(contractList);
        facts.addAll(contractLineList);
        facts.addAll(taList);
        facts.addAll(courseDayList);
        facts.addAll(courseList);
        facts.addAll(courseOffRequestList);
        facts.addAll(courseOnRequestList);
        facts.addAll(coordinatorList);
        // Do not add the planning entity's (courseAssignmentList) because that will be done automatically
        return facts;
    }

}
