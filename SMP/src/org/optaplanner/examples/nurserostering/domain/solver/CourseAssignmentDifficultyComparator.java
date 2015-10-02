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

package org.optaplanner.examples.nurserostering.domain.solver;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.optaplanner.examples.nurserostering.domain.Course;
import org.optaplanner.examples.nurserostering.domain.CourseAssignment;

public class CourseAssignmentDifficultyComparator implements Comparator<CourseAssignment>, Serializable {

    @Override
    public int compare(CourseAssignment a, CourseAssignment b) {
        Course aCourse = a.getCourse();
        Course bCourse = b.getCourse();
        return new CompareToBuilder()
                    .append(bCourse.getCourseDate(), aCourse.getCourseDate()) // Descending
                    .append(bCourse.getCourseType(), aCourse.getCourseType()) // Descending
                    // For construction heuristics, scheduling the courses in sequence is better
                    .append(aCourse.getRequiredTaSize(), bCourse.getRequiredTaSize())
                    .toComparison();
    }

}
