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
package com.cognizant.cognizantits.engine.reporting.impl.handlers;

import com.cognizant.cognizantits.engine.core.RunContext;
import com.cognizant.cognizantits.engine.drivers.SeleniumDriver;
import com.cognizant.cognizantits.engine.reporting.TestCaseReport;
import com.cognizant.cognizantits.engine.reporting.intf.Report;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.Step;
import java.io.File;
import java.util.List;

/**
 *
 * 
 */
public class TestCaseHandler implements Report {

    public TestCaseReport report;

    public TestCaseHandler(TestCaseReport report) {
        this.report = report;
    }

    @Override
    public void startComponent(String component,String desc) {

    }

    @Override
    public void startIteration(int iteration) {

    }

    @Override
    public void endComponent(String string) {

    }

    @Override
    public void endIteration(int iteration) {

    }

    @Override
    public SeleniumDriver getDriver() {
        return report.getDriver();
    }

    @Override
    public String getScreenShotName() {
        return report.getScreenShotName();
    }

    @Override
    public String getNewScreenShotName() {
        return report.getNewScreenShotName();
    }

    @Override
    public File getReportLoc() {
        return report.getReportLoc();
    }

    @Override
    public void createReport(RunContext runContext, String runTime) {
    }

    @Override
    public void updateTestLog(String stepName, String stepDescription, Status state, String link, List<String> links) {

    }

    @Override
    public Step getStep() {
        return report.getStep();
    }

    @Override
    public int getStepCount() {
        return report.getStepCount();
    }

    @Override
    public Status finalizeReport() {
        return null;
    }

    public void setDriver(SeleniumDriver driver) {

    }

}
