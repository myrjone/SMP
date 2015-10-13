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

package org.optaplanner.examples.tarostering.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.tarostering.persistence.TaRosteringDao;
import org.optaplanner.examples.tarostering.persistence.TaRosteringExporter;
import org.optaplanner.examples.tarostering.persistence.TaRosteringImporter;
import org.optaplanner.examples.tarostering.swingui.TaRosteringPanel;

public class TaRosteringApp extends CommonApp {

    public static final String SOLVER_CONFIG
            = "taRosteringSolverConfig.xml";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new TaRosteringApp().init();
    }

    public TaRosteringApp() {
        super("Ta rostering",
                "Official competition name: INRC2010 - Ta rostering\n\n" +
                        "Assign courses to tas.",
                TaRosteringPanel.LOGO_PATH);
    }

    @Override
    protected Solver createSolver() {
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        return solverFactory.buildSolver();
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new TaRosteringPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new TaRosteringDao();
    }

    @Override
    protected AbstractSolutionImporter createSolutionImporter() {
        return new TaRosteringImporter();
    }

    @Override
    protected AbstractSolutionExporter createSolutionExporter() {
        return new TaRosteringExporter();
    }

}
