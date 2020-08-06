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
package com.cognizant.cognizantits.engine.settings;

import com.cognizant.cognizantits.datalib.component.TestCase;
import com.cognizant.cognizantits.datalib.component.TestSet;
import com.cognizant.cognizantits.datalib.settings.AbstractPropSettings;

/**
 *
 * 
 */
public class GlobalSettings extends AbstractPropSettings {

    private static final long serialVersionUID = -431006539190776440L;

    public GlobalSettings(String location) {
        super(location, "Global Settings");
    }

    public void setFor(TestCase testCase, String browser) {
        setTestRun(true);
        setProjectPath(testCase.getProject().getLocation());
        setProjectName(testCase.getProject().getName());
        setScenario(testCase.getScenario().getName());
        setTestCase(testCase.getName());
        setBrowser(browser);
        save();
    }

    public void setFor(TestSet testSet) {
        setTestRun(false);
        setProjectPath(testSet.getProject().getLocation());
        setProjectName(testSet.getProject().getName());
        setRelease(testSet.getRelease().getName());
        setTestSet(testSet.getName());
        save();
    }

    public String getProjectPath() {
        return getProperty("ProjectPath");
    }

    public void setProjectPath(String value) {
        setProperty("ProjectPath", value);
    }

    public String getProjectName() {
        return getProperty("ProjectName");
    }

    public void setProjectName(String value) {
        setProperty("ProjectName", value);
    }

    public Boolean isTestRun() {
        return Boolean.valueOf(getProperty("TestRun", "true"));
    }

    public void setTestRun(Boolean value) {
        setProperty("TestRun", String.valueOf(value));
    }

    public String getScenario() {
        return getProperty("Scenario");
    }

    public void setScenario(String value) {
        setProperty("Scenario", value);
    }

    public String getTestCase() {
        return getProperty("TestCase");
    }

    public void setTestCase(String value) {
        setProperty("TestCase", value);
    }

    public String getBrowser() {
        return getProperty("Browser");
    }

    public void setBrowser(String value) {
        setProperty("Browser", value);
    }

    public String getRelease() {
        return getProperty("Release");
    }

    public void setRelease(String value) {
        setProperty("Release", value);
    }

    public String getTestSet() {
        return getProperty("TestSet");
    }

    public void setTestSet(String value) {
        setProperty("TestSet", value);
    }

}
