/*
 * Copyright 2014 - 2017 Cognizant Technology Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognizant.cognizantits.engine.reporting.intf;

import com.cognizant.cognizantits.engine.core.RunContext;
import com.cognizant.cognizantits.engine.drivers.SeleniumDriver;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.Step;
import java.io.File;
import java.util.List;

public interface Report {

    public void createReport(RunContext runContext, String runTime);

    public void updateTestLog(String stepName, String stepDescription, Status state, String link, List<String> links);

    public Status finalizeReport();

    public void startComponent(String component,String desc);

    public void startIteration(int iteration);

    public void endComponent(String string);

    public void endIteration(int iteration);

    public SeleniumDriver getDriver();

    public String getScreenShotName();
    
    public String getNewScreenShotName();

    public File getReportLoc();

    public Step getStep();

    public int getStepCount();

}
