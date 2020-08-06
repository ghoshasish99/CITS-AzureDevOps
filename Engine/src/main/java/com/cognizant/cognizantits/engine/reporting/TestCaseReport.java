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
package com.cognizant.cognizantits.engine.reporting;

import com.cognizant.cognizantits.engine.constants.FilePath;
import com.cognizant.cognizantits.engine.core.RunContext;
import com.cognizant.cognizantits.engine.drivers.SeleniumDriver;
import com.cognizant.cognizantits.engine.reporting.impl.handlers.PrimaryHandler;
import com.cognizant.cognizantits.engine.reporting.impl.handlers.TestCaseHandler;
import com.cognizant.cognizantits.engine.reporting.impl.html.HtmlTestCaseHandler;
import com.cognizant.cognizantits.engine.reporting.intf.Report;
import com.cognizant.cognizantits.engine.reporting.util.DateTimeUtils;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.Step;
import com.cognizant.cognizantits.engine.support.methodInf.MethodInfoManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class TestCaseReport implements Report {

    public static volatile int tcCount;

    public String Scenario;
    public String TestCase;
    public String screenShotFileName;

    public boolean runComplete = false;

    int iterCounter = 0;
    int stepNo = 0;

    public final DateTimeUtils startTime;
    SeleniumDriver seleniumdriver;

    Step curr;
    Status currentStatus;

    public PrimaryHandler primaryHandler;
    private final List<TestCaseHandler> handlers;

    public TestCaseReport() {
        ++tcCount;
        startTime = new DateTimeUtils();
        handlers = new ArrayList<>();
        register(new HtmlTestCaseHandler(this), true);
    }

    /**
     * sets the selenium driver
     *
     * @param driver
     */
    public void setDriver(SeleniumDriver driver) {
        seleniumdriver = driver;
        for (TestCaseHandler handler : handlers) {
            handler.setDriver(driver);
        }
    }

    /**
     * updates the current step details and resolves DESCRIPTION if not
     * available
     *
     * @param curr
     *
     */
    public void updateStepDetails(Step curr) {
        this.curr = curr;
        if (this.curr.Description == null || this.curr.Description.trim().isEmpty()) {
            this.curr.Description = MethodInfoManager.getResolvedDescriptionFor(curr.toTestStep());
        }
    }

    /**
     * initializes the test case report details
     *
     * @param runContext
     * @param runTime
     */
    @Override
    public synchronized void createReport(RunContext runContext, String runTime) {
        this.Scenario = runContext.Scenario;
        this.TestCase = runContext.TestCase;
        for (TestCaseHandler handler : handlers) {
            handler.createReport(runContext, runTime);
        }
    }
    //<editor-fold defaultstate="collapsed" desc="wrapper functions">

    public void updateTestLog(String stepName, String stepDescription, Status state) {
        updateTestLog(stepName, stepDescription, state, null, null);
    }

    public void updateTestLog(String stepName, String stepDescription, Status state, String optionalLink) {
        updateTestLog(stepName, stepDescription, state, optionalLink, null);
    }

    /**
     * updates the step results to the test case json DATA
     *
     * @param stepName
     * @param stepDescription
     * @param state
     * @param optional
     */
    public void updateTestLog(String stepName, String stepDescription, Status state, List<String> optional) {
        updateTestLog(stepName, stepDescription, state, null, optional);
    }
//</editor-fold>

    @Override
    public void updateTestLog(String stepName, String stepDescription, Status state,
            String optionalLink, List<String> optional) {
        currentStatus = state;
        stepNo++;
        setScreenShotName();
        System.out.println(String.format("[%s]   | %s", state, stepDescription));
        System.out.println(String.format("\n%99s\n", "=").replace(" ", "="));
        for (TestCaseHandler handler : handlers) {
            handler.updateTestLog(stepName, stepDescription, state, optionalLink, optional);
        }
    }

    /**
     * finalize the test case execution and create standalone test case report
     * file for upload purpose
     *
     * @return
     */
    @Override
    public Status finalizeReport() {
        runComplete = true;
        for (TestCaseHandler handler : handlers) {
            handler.finalizeReport();
        }
        return (currentStatus = primaryHandler.getCurrentStatus());
    }

    private void setScreenShotName() {
        screenShotFileName = getNewScreenShotName();
    }

    //<editor-fold defaultstate="collapsed" desc="flow-control">
    /**
     * creates new iteration object
     *
     * @param iteration
     */
    @Override
    public void startIteration(int iteration) {
        stepNo = 0;
        iterCounter++;
        for (TestCaseHandler handler : handlers) {
            handler.startIteration(iteration);
        }
    }

    /**
     * creates new reusable object
     *
     * @param component
     * @param desc
     */
    @Override
    public void startComponent(String component, String desc) {
        for (TestCaseHandler handler : handlers) {
            handler.startComponent(component, desc);
        }
    }

    @Override
    public void endComponent(String component) {
        for (TestCaseHandler handler : handlers) {
            handler.endComponent(component);
        }
    }

    @Override
    public void endIteration(int iteration) {
        for (TestCaseHandler handler : handlers) {
            handler.endIteration(iteration);
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="external-API">
    /**
     *
     * @return the screen shot NAME for the current step
     */
    @Override
    public SeleniumDriver getDriver() {
        return seleniumdriver;
    }

    public int getIter() {
        return iterCounter;
    }

    public Object getData() {
        return primaryHandler.getData();
    }

    public File getFile() {
        return primaryHandler.getFile();
    }

    public static synchronized int getTestCaseNumber() {
        return tcCount;
    }

    @Override
    public String getScreenShotName() {
        return screenShotFileName;
    }

    @Override
    public String getNewScreenShotName() {
        return File.separator
                + "img"
                + File.separator
                + Scenario
                + "_"
                + TestCase
                + "_Step-"
                + stepNo + "_"
                + DateTimeUtils.TimeNowForFolder()
                + ".png";
    }

    @Override
    public File getReportLoc() {
        return new File(FilePath.getCurrentResultsPath());
    }

    @Override
    public Step getStep() {
        return curr;
    }

    public String getTestCaseName() {
        return TestCase;
    }

    public String getScenarioName() {
        return Scenario;
    }

    public Status getCurrentStatus() {
        return currentStatus;
    }

    public Boolean isStepPassed() {
        if (currentStatus != null) {
            return currentStatus.equals(Status.PASS) || currentStatus.equals(Status.DONE)
                    || currentStatus.equals(Status.SCREENSHOT);
        }
        return false;
    }

    @Override
    public int getStepCount() {
        return stepNo;
    }

//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="handler-registration">
    public void register(TestCaseHandler testCaseHandler) {
        if (!handlers.contains(testCaseHandler)) {
            handlers.add(testCaseHandler);
        }
    }

    public void register(TestCaseHandler testCaseHandler, boolean primaryHandler) {
        register(testCaseHandler);
        if (primaryHandler) {
            this.primaryHandler = (PrimaryHandler) testCaseHandler;
        }
    }
//</editor-fold>
}
