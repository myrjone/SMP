package org.optaplanner.examples.tarostering.persistence;

import org.optaplanner.examples.tarostering.domain.Course;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahooper on 10/6/2015.
 */
public class CsvImporter {

    private static final String COMMA_DELIMITER = ",";

    public void importCourseList(String fileName) {
        BufferedReader fileReader = null;

        try {
            /*
             * Assuming this order:
             * CRN, DEPT, CRS, SEC, DAY, START, END, BLDG, RM
             */
            fileReader = new BufferedReader(new FileReader("SMP/data/input/" + fileName));
            String line = "";
            List<Course> courseList = new ArrayList<>();

            while ((line = fileReader.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                if (values.length > 0) {
                    //TODO: Populate course object
                    courseList.add(new Course());
                }
            }

        }
        catch (Exception ex) {

        }
    }

}
