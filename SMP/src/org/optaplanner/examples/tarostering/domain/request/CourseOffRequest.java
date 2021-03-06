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

package org.optaplanner.examples.tarostering.domain.request;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.tarostering.domain.Course;
import org.optaplanner.examples.tarostering.domain.Ta;

@XStreamAlias("CourseOffRequest")
public class CourseOffRequest extends AbstractPersistable {

    private Ta ta;
    private Course course;

    public Ta getTa() {
        return ta;
    }

    public void setTa(Ta ta) {
        this.ta = ta;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return course + "_OFF_" + ta;
    }

}
