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
import java.util.Map;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.swingui.components.Labeled;
import org.optaplanner.examples.tarostering.domain.contract.Contract;
import org.optaplanner.examples.tarostering.domain.request.CourseOffRequest;
import org.optaplanner.examples.tarostering.domain.request.CourseOnRequest;

@XStreamAlias("Ta")
public class Ta extends AbstractPersistable implements Labeled {

    private String code;
    private String name;
    private Contract contract;
    private String email;

    private Map<Course, CourseOffRequest> courseOffRequestMap;
    private Map<Course, CourseOnRequest> courseOnRequestMap;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Map<Course, CourseOffRequest> getCourseOffRequestMap() {
        return courseOffRequestMap;
    }

    public void setCourseOffRequestMap(Map<Course, CourseOffRequest> courseOffRequestMap) {
        this.courseOffRequestMap = courseOffRequestMap;
    }

    public Map<Course, CourseOnRequest> getCourseOnRequestMap() {
        return courseOnRequestMap;
    }

    public void setCourseOnRequestMap(Map<Course, CourseOnRequest> courseOnRequestMap) {
        this.courseOnRequestMap = courseOnRequestMap;
    }

    @Override
    public String getLabel() {
        return name;
    }

}
