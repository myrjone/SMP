package org.optaplanner.examples.nurserostering.domain.solver;

import java.util.Comparator;
import org.optaplanner.examples.nurserostering.domain.CourseAssignment;
import org.optaplanner.examples.nurserostering.domain.DayOfWeek;

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
