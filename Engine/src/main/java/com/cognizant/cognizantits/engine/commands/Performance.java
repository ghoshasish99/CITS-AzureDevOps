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
package com.cognizant.cognizantits.engine.commands;

import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.core.Control;
import com.cognizant.cognizantits.engine.reporting.TestCaseReport;
import com.cognizant.cognizantits.engine.reporting.performance.PerformanceTimings;
import com.cognizant.cognizantits.engine.reporting.performance.ResourceTimings;
import com.cognizant.cognizantits.engine.reporting.performance.har.Entry;
import com.cognizant.cognizantits.engine.reporting.performance.har.Har;
import com.cognizant.cognizantits.engine.reporting.performance.har.Har.Log;
import com.cognizant.cognizantits.engine.reporting.performance.har.Page;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.openqa.selenium.JavascriptExecutor;

/**
 *
 * 
 */
public class Performance extends Command {

    public Performance(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.BROWSER, desc = "To delete all the cookies", input = InputType.NO)
    public void ClearCache() {
        try {
            Driver.manage().deleteAllCookies();
            Report.updateTestLog(Action, "Cookies Cleared", Status.DONE);
        } catch (Exception e) {
            Report.updateTestLog(Action, "Failed to clear cookies", Status.DONE);
        }
    }

    /**
     * capture browser page navigation and resource timings and store it in the
     * report object
     */
    @Action(object = ObjectType.BROWSER, desc = "Capture the PageTimings for the Page [<Data>]", input = InputType.YES)
    public void capturePageTimings() {
        if (Control.exe.getExecSettings().getRunSettings().isPerformanceLogEnabled()) {
            try {
                storePageTimings();
                Report.updateTestLog(Action, "Timings Updated in Har", Status.DONE);
            } catch (Exception ex) {
                Report.updateTestLog(Action, "Unable to update PageTimings : " + ex.getMessage(),
                        Status.FAIL);
                Logger.getLogger(Performance.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    private void storePageTimings() {
        JavascriptExecutor js = (JavascriptExecutor) Driver;
        String pt = js.executeScript(PerformanceTimings.script()).toString();
        String rt = "[]";
        try {
            rt = js.executeScript(ResourceTimings.script()).toString();
        } catch (Exception ex) {
            Logger.getLogger(Performance.class.getName()).log(Level.SEVERE,
                    "Error on Resource Timings : {0}", ex.getMessage());
        }
        createHar(pt, rt);
    }

    @SuppressWarnings("rawtypes")
    private void createHar(String pt, String rt) {
        Har<String, Log> har = new Har<>();
        Page p = new Page(pt, har.pages());
        har.addPage(p);
        for (Object res : (JSONArray) JSONValue.parse(rt)) {
            JSONObject jse = (JSONObject) res;
            if (jse.size() > 14) {
                Entry e = new Entry(jse.toJSONString(), p);
                har.addEntry(e);
            }
        }
        har.addRaw(pt, rt);
        Control.ReportManager.addHar(har, (TestCaseReport) Report,
                escapeName(Data));
    }

    private String getPageName() {
        return escapeName(Data.isEmpty() ? Driver.getTitle() : Data);
    }

    private String escapeName(String data) {
        return Objects.toString(data, "")
                .replaceAll("[^a-zA-Z0-9-]", "_").replaceAll("__+", "_");
    }

}
