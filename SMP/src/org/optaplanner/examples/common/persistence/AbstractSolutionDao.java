/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.examples.common.persistence;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSolutionDao implements SolutionDao {

    /**
     * The path to the data directory, preferably with unix slashes for portability.
     * For example: -D{@value #DATA_DIR_SYSTEM_PROPERTY}=sources/data/
     */
    public static final String DATA_DIR_SYSTEM_PROPERTY = "org.optaplanner.examples.dataDir";

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    protected String dirName;
    protected File dataDir;

    public AbstractSolutionDao(String dirName) {
        this.dirName = dirName;
        String dataDirPath = System.getProperty(DATA_DIR_SYSTEM_PROPERTY, "data/");
        dataDir = new File(dataDirPath, dirName);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    @Override
    public String getDirName() {
        return dirName;
    }

    @Override
    public File getDataDir() {
        return dataDir;
    }

}
