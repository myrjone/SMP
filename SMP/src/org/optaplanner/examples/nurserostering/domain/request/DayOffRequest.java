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

package org.optaplanner.examples.nurserostering.domain.request;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.nurserostering.domain.Ta;
import org.optaplanner.examples.nurserostering.domain.CourseDate;

@XStreamAlias("DayOffRequest")
public class DayOffRequest extends AbstractPersistable {

    private Ta ta;
    private CourseDate courseDate;
    private int weight;

    public Ta getTa() {
        return ta;
    }

    public void setTa(Ta ta) {
        this.ta = ta;
    }

    public CourseDate getCourseDate() {
        return courseDate;
    }

    public void setCourseDate(CourseDate courseDate) {
        this.courseDate = courseDate;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return courseDate + "_OFF_" + ta;
    }

}
