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

package org.optaplanner.examples.nurserostering.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.hardsoft.HardSoftScoreDefinition;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;
import org.optaplanner.examples.nurserostering.domain.contract.ContractLine;
import org.optaplanner.examples.nurserostering.domain.contract.PatternContractLine;
import org.optaplanner.examples.nurserostering.domain.pattern.Pattern;
import org.optaplanner.examples.nurserostering.domain.request.DayOffRequest;
import org.optaplanner.examples.nurserostering.domain.request.DayOnRequest;
import org.optaplanner.examples.nurserostering.domain.request.CourseOffRequest;
import org.optaplanner.examples.nurserostering.domain.request.CourseOnRequest;
import org.optaplanner.persistence.xstream.impl.score.XStreamScoreConverter;

@PlanningSolution
@XStreamAlias("NurseRoster")
public class NurseRoster extends AbstractPersistable implements Solution<HardSoftScore> {

    private String code;

    private NurseRosterParametrization nurseRosterParametrization;
    private List<Skill> skillList;
    private List<CourseType> courseTypeList;
    private List<CourseTypeSkillRequirement> courseTypeSkillRequirementList;
    private List<Pattern> patternList;
    private List<Contract> contractList;
    private List<ContractLine> contractLineList;
    private List<PatternContractLine> patternContractLineList;
    private List<Ta> taList;
    private List<SkillProficiency> skillProficiencyList;
    private List<CourseDate> courseDateList;
    private List<Course> courseList;
    private List<DayOffRequest> dayOffRequestList;
    private List<DayOnRequest> dayOnRequestList;
    private List<CourseOffRequest> courseOffRequestList;
    private List<CourseOnRequest> courseOnRequestList;

    private List<CourseAssignment> courseAssignmentList;

    @XStreamConverter(value = XStreamScoreConverter.class, types = {HardSoftScoreDefinition.class})
    private HardSoftScore score;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public NurseRosterParametrization getNurseRosterParametrization() {
        return nurseRosterParametrization;
    }

    public void setNurseRosterParametrization(NurseRosterParametrization nurseRosterParametrization) {
        this.nurseRosterParametrization = nurseRosterParametrization;
    }

    public List<Skill> getSkillList() {
        return skillList;
    }

    public void setSkillList(List<Skill> skillList) {
        this.skillList = skillList;
    }

    public List<CourseType> getCourseTypeList() {
        return courseTypeList;
    }

    public void setCourseTypeList(List<CourseType> courseTypeList) {
        this.courseTypeList = courseTypeList;
    }

    public List<CourseTypeSkillRequirement> getCourseTypeSkillRequirementList() {
        return courseTypeSkillRequirementList;
    }

    public void setCourseTypeSkillRequirementList(List<CourseTypeSkillRequirement> courseTypeSkillRequirementList) {
        this.courseTypeSkillRequirementList = courseTypeSkillRequirementList;
    }

    public List<Pattern> getPatternList() {
        return patternList;
    }

    public void setPatternList(List<Pattern> patternList) {
        this.patternList = patternList;
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

    public List<PatternContractLine> getPatternContractLineList() {
        return patternContractLineList;
    }

    public void setPatternContractLineList(List<PatternContractLine> patternContractLineList) {
        this.patternContractLineList = patternContractLineList;
    }

    @ValueRangeProvider(id = "taRange")
    public List<Ta> getTaList() {
        return taList;
    }

    public void setTaList(List<Ta> taList) {
        this.taList = taList;
    }

    public List<SkillProficiency> getSkillProficiencyList() {
        return skillProficiencyList;
    }

    public void setSkillProficiencyList(List<SkillProficiency> skillProficiencyList) {
        this.skillProficiencyList = skillProficiencyList;
    }

    public List<CourseDate> getCourseDateList() {
        return courseDateList;
    }

    public void setCourseDateList(List<CourseDate> courseDateList) {
        this.courseDateList = courseDateList;
    }

    public List<Course> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<Course> courseList) {
        this.courseList = courseList;
    }

    public List<DayOffRequest> getDayOffRequestList() {
        return dayOffRequestList;
    }

    public void setDayOffRequestList(List<DayOffRequest> dayOffRequestList) {
        this.dayOffRequestList = dayOffRequestList;
    }

    public List<DayOnRequest> getDayOnRequestList() {
        return dayOnRequestList;
    }

    public void setDayOnRequestList(List<DayOnRequest> dayOnRequestList) {
        this.dayOnRequestList = dayOnRequestList;
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

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.add(nurseRosterParametrization);
        facts.addAll(skillList);
        facts.addAll(courseTypeList);
        facts.addAll(courseTypeSkillRequirementList);
        facts.addAll(patternList);
        facts.addAll(contractList);
        facts.addAll(contractLineList);
        facts.addAll(patternContractLineList);
        facts.addAll(taList);
        facts.addAll(skillProficiencyList);
        facts.addAll(courseDateList);
        facts.addAll(courseList);
        facts.addAll(dayOffRequestList);
        facts.addAll(dayOnRequestList);
        facts.addAll(courseOffRequestList);
        facts.addAll(courseOnRequestList);
        // Do not add the planning entity's (courseAssignmentList) because that will be done automatically
        return facts;
    }

}
