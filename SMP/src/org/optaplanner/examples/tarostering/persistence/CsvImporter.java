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
