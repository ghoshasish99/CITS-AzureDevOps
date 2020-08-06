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

import java.lang.reflect.Field;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestInfo {

    public String testScenario = "";
    public String testCase = "";
    public String testDescription = "";
    public String iteration = "";
    public String executionTime = "";
    public String browser = "";
    public String browserVersion = "";
    public String platform = "";
    public String date = "";
    public String time = "";

    public TestInfo(String ts, String tc, String tDesc, String iteration,
            String etime, String date, String time, String browser, String browserVersion, String platform) {
        this.testScenario = ts;
        this.testCase = tc;
        this.iteration = iteration;
        this.executionTime = etime;
        this.date = date;
        this.time = time;
        this.browser = browser;
        this.browserVersion = browserVersion;
        this.platform = platform;
        setDesc(tDesc);
    }

    public Properties getMap() {
        Properties map = new Properties();
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value;
            try {
                value = field.get(this);
                if (value != null) {
                    map.put(field.getName(), value.toString());
                }
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(TestInfo.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return map;
    }

    public int getExeTime() {
        try {
            String[] tokens = executionTime.split(":");
            int duration = Integer.parseInt(tokens[0]);
            duration *= 60;
            duration += Integer.parseInt(tokens[1]);
            int seconds = Integer.parseInt(tokens[2]);
            if (seconds > 30) {
                duration++;
            }
            return Math.max(1, duration);
        } catch (Exception ex) {
            return 0;
        }
    }

    private void setDesc(String tDesc) {
        StringBuilder desc = new StringBuilder();
        desc.append(
                String.format(
                        "[%s]\n Testcase //%s/%s for iteration %s executed on browser %s version %s.||\n Execution Time %s hrs",
                        tDesc,
                        this.testScenario,
                        this.testCase,
                        this.iteration,
                        this.browser,
                        this.browserVersion,
                        this.executionTime));
        this.testDescription = desc.toString();
    }
}
