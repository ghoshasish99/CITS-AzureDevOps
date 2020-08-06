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
package com.cognizant.cognizantits.engine.core;

import com.cognizant.cognizantits.datalib.component.ExecutionStep;
import com.cognizant.cognizantits.datalib.component.TestSet;
import com.cognizant.cognizantits.engine.constants.FilePath;
import com.cognizant.cognizantits.engine.drivers.WebDriverFactory.Browser;
import com.cognizant.cognizantits.engine.settings.GlobalSettings;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.Platform;

public class RunManager {

    private static GlobalSettings globalSettings;

    private static Queue<RunContext> runQ;

    public static GlobalSettings getGlobalSettings() {
        if (globalSettings == null) {
            globalSettings = new GlobalSettings(FilePath.getConfigurationPath());
        }
        return globalSettings;
    }

    public static String getRunName() {
        if (globalSettings.isTestRun()) {
            return "TestCase - " + globalSettings.getScenario() + " - " + globalSettings.getTestCase();
        }
        return "TestSet - " + globalSettings.getRelease() + " - " + globalSettings.getTestSet();
    }

    public static void init() {
        upadteProperties();
    }

    private static void upadteProperties() {
        File appSettings = new File(FilePath.getAppSettings());
        if (appSettings.exists()) {
            try {
                Properties appSett = new Properties();
                appSett.load(new FileReader(appSettings));
                System.getProperties().putAll(appSett);
            } catch (IOException ex) {
                Logger.getLogger(RunManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void loadRunManager() throws Exception {
        if (globalSettings.isTestRun()) {
            runQ = getTestCaseRunManager();
        } else {
            runQ = getTestSetRunManager();
        }
    }

    public static Queue<RunContext> queue() throws Exception {
        return runQ;
    }

    static Queue<RunContext> getTestCaseRunManager() {
        Queue<RunContext> execQ = new LinkedList<>();
        RunContext exe = new RunContext();
        exe.Scenario = globalSettings.getScenario();
        exe.TestCase = globalSettings.getTestCase();
        exe.Description = "Test Run";
        exe.BrowserName = globalSettings.getBrowser();
        exe.Browser = Browser.fromString(exe.BrowserName);
        exe.Platform = Platform.ANY;
        exe.BrowserVersion = "default";
        exe.Iteration = "Single";
        exe.print();
        execQ.add(exe);
        return execQ;
    }

    static Queue<RunContext> getTestSetRunManager() throws Exception {
        Queue<RunContext> execQ = new LinkedList<>();
        TestSet testSet = Control.exe.getTestSet();
        try {
            testSet.loadTableModel();
            if (!testSet.getExecutableSteps().isEmpty()) {
                for (ExecutionStep step : testSet.getExecutableSteps()) {
                    addRunContext(step, execQ);
                }
            } else {
                throw new Exception("No testcases are selected for exection in - " + testSet.getName());
            }
        } catch (Exception ex) {
            throw new Exception(String.format(
                    "Not able to load TestSet [%s/%s]\n[%S]",
                    testSet.getRelease().getName(), testSet.getName(), ex.getMessage()));
        }
        System.out.println("----------------------------------------------------------");
        System.out.println(String.format(
                "[%s] TestCase%s selected for execution from [//%s/%s]",
                execQ.size(), execQ.size() > 1 ? "s" : "", testSet.getRelease().getName(),
                testSet.getName()));
        System.out.println("----------------------------------------------------------");
        return execQ;
    }

    private static void addRunContext(ExecutionStep step, Queue<RunContext> execQ) {
        RunContext exe = new RunContext();
        exe.Scenario = step.getTestScenarioName();
        exe.TestCase = step.getTestCaseName();
        exe.Description = exe.TestCase;
        exe.PlatformValue = step.getPlatform();
        exe.Platform = getPlatform(exe.PlatformValue);
        exe.BrowserName = step.getBrowser();
        exe.Browser = Browser.fromString(exe.BrowserName);
        exe.BrowserVersionValue = step.getBrowserVersion();
        exe.BrowserVersion = getBrowserVersion(exe.BrowserVersionValue);
        exe.Iteration = step.getIteration();
        exe.print();
        execQ.add(exe);
    }

    public static int getThreadCount(String threadCount) {
        switch (threadCount.toLowerCase()) {
            case "single":
                return 1;
            case "2 threads":
                return 2;
            case "3 threads":
                return 3;
            case "4 threads":
                return 4;
            case "5 threads":
                return 5;
            default:
                return getThread(threadCount);
        }
    }

    static int getThread(String threadCount) {
        try {
            int x = Integer.parseInt(threadCount);
            return Math.max(1, x);
        } catch (NumberFormatException ex) {
            System.out.println("Error Converting value[" + threadCount + "] Resetting thread to 1");
            return 1;
        }
    }

    static long getExecutionTimeout(String exeTimeout) {
        try {
            int l = Integer.parseInt(exeTimeout);
            return Math.abs(l);
        } catch (NumberFormatException ex) {
            return 300L;
        }
    }

    static String getBrowserVersion(String bVersion) {
        if (bVersion == null || bVersion.isEmpty()) {
            return "default";
        }
        return bVersion;
    }

    static Platform getPlatform(String platform) {
        if (platform != null && !platform.trim().isEmpty()) {
            return Platform.fromString(platform.toUpperCase());
        }
        return Platform.ANY;
    }

    public static void clear() {
        runQ.clear();
        runQ = null;
    }
}
