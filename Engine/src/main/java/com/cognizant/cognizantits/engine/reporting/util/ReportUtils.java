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
package com.cognizant.cognizantits.engine.reporting.util;

import com.cognizant.cognizantits.datalib.component.ExecutionStep;
import com.cognizant.cognizantits.datalib.component.TestSet;
import com.cognizant.cognizantits.engine.constants.FilePath;
import com.cognizant.cognizantits.engine.core.Control;
import com.cognizant.cognizantits.engine.core.RunContext;
import com.cognizant.cognizantits.engine.core.RunManager;
import com.cognizant.cognizantits.engine.drivers.SeleniumDriver;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * 
 */
@SuppressWarnings({"unchecked"})
public class ReportUtils {

    public static Properties appSett = new Properties();

    public static void loadDefaultTheme(JSONObject obj) {
        try {
            appSett.putAll(System.getProperties());
            Object Theme = appSett.get(RDS.TestSet.THEME);
            if (Theme == null || Theme.toString().isEmpty()) {
                Theme = getFirstTheme();
            }
            obj.put(RDS.TestSet.THEME, Theme.toString());
            if (appSett.get(RDS.TestSet.THEMES) != null) {
                String[] themes = appSett.get(RDS.TestSet.THEMES).toString().split(",");
                JSONArray jsthemes = new JSONArray();
                jsthemes.addAll(Arrays.asList(themes));
                obj.put(RDS.TestSet.THEMES, jsthemes);
            }
        } catch (Exception ex) {
            Logger.getLogger(ReportUtils.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    private static Object getFirstTheme() {
        File themeFolder = new File(FilePath.getReportThemePath());
        for (File file : themeFolder.listFiles()) {
            if (file.getName().endsWith(".css")) {
                return file.getName().replace(".css", "");
            }
        }
        return "default";
    }

    public static String resolveDesc(String val) {
        return resolveTags(Objects.toString(val, " Uncaught Exception "));
    }

    public static String resolveTags(String val) {
        if (val.contains("\n")) {
            val = val.replaceAll("\n", "<br>") + "#CTAG";
        }
        return val;
    }

    public static Boolean takeScreenshot(SeleniumDriver seleniumdriver, String imgSrc) {
        try {
            File scrFile = seleniumdriver.createScreenShot();
            if (scrFile != null) {
                File imgFile = new File(FilePath.getCurrentResultsPath() + imgSrc);
                FileUtils.copyFile(scrFile, imgFile, true);
                scrFile.delete();
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(ReportUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static void updateStatus(String status, RunContext runContext) {
        if (!RunManager.getGlobalSettings().isTestRun()) {
            updateTestCaseSheet(runContext, status);
        }
    }

    public static void updateTestCaseSheet(RunContext runContext, String status) {
        try {
            TestSet testSet = Control.exe.getTestSet();
            testSet.loadTableModel();
            for (ExecutionStep step : testSet.getTestSteps()) {
                if (Boolean.valueOf(step.getExecute())
                        && runContext.Scenario.equalsIgnoreCase(step.getTestScenarioName())
                        && runContext.TestCase.equalsIgnoreCase(step.getTestCaseName())
                        && runContext.Iteration.equalsIgnoreCase(step.getIteration())
                        && runContext.BrowserName.equalsIgnoreCase(step.getBrowser())
                        && runContext.BrowserVersionValue.equalsIgnoreCase(step.getBrowserVersion())
                        && runContext.PlatformValue.equalsIgnoreCase(step.getPlatform())) {
                    step.setStatus(status);
                }
            }
            testSet.fireTableDataChanged();
            testSet.save();
        } catch (Exception ex) {
            Logger.getLogger(ReportUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
