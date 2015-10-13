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

import org.optaplanner.examples.common.app.CommonBenchmarkApp;

public class TaRosteringBenchmarkApp extends CommonBenchmarkApp {

    public static void main(String[] args) {
        new TaRosteringBenchmarkApp().buildAndBenchmark(args);
    }

    public TaRosteringBenchmarkApp() {
        super(
                new ArgOption("sprint",
                        "org/optaplanner/examples/tarostering/benchmark/taRosteringSprintBenchmarkConfig.xml"),
                new ArgOption("medium",
                        "org/optaplanner/examples/tarostering/benchmark/taRosteringMediumBenchmarkConfig.xml"),
                new ArgOption("long",
                        "org/optaplanner/examples/tarostering/benchmark/taRosteringLongBenchmarkConfig.xml"),
                new ArgOption("stepLimit",
                        "org/optaplanner/examples/tarostering/benchmark/taRosteringStepLimitBenchmarkConfig.xml")
        );
    }

}
