/*
 * Copyright 2015 JBoss Inc
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


package org.optaplanner.examples.tarostering.domain.solver;

import java.util.Comparator;
import org.optaplanner.examples.tarostering.domain.CourseAssignment;
import org.optaplanner.examples.tarostering.domain.DayOfWeek;

public class CourseAssignmentDayOfWeekComparator implements Comparator<CourseAssignment>{
    @Override
    public int compare(CourseAssignment a, CourseAssignment b) {
        DayOfWeek aDay = a.getCourseDayDayOfWeek();
        DayOfWeek bDay = b.getCourseDayDayOfWeek();
        if (aDay.ordinal() < bDay.ordinal()) {
            return -1;
        }
        else if (aDay.ordinal() > bDay.ordinal()) {
            return 1;
        }
        else return 0;
    }
}
