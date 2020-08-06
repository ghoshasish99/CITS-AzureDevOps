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
package com.cognizant.cognizantits.engine.reporting.performance.metrics;

import com.cognizant.cognizantits.engine.constants.FilePath;
import com.cognizant.cognizantits.engine.reporting.performance.PerformanceReport;
import com.cognizant.cognizantits.engine.reporting.performance.har.Har;
import com.cognizant.cognizantits.engine.reporting.performance.metrics.pagespeed.PageSpeed;
import java.io.File;
import org.json.simple.JSONObject;

/**
 *
 * 
 */
public class MetricsProvider {

    private static final File H_PS_EXE = new File(FilePath.getConfigurationPath(), "har_to_pagespeed.exe");

    public static final String MODULE = "perf.metrics.module";

    public static final void init() {
        switch (getProvider()) {
            default:
                break;
        }
    }

    public enum Module {
        DEF;

        public static Module toModule(String m) {
            try {
                return valueOf(m.trim());
            } catch (Exception ex) {
                return DEF;
            }
        }

        public static String getConfig(Module m) {
            switch (m) {
                default:
                    return FilePath.getConfigurationPath();
            }

        }
    }

    public static PageMetrics getMetrics(Har<?, ?> har, int i, String harName,
            PerformanceReport.Report r) {
        PageMetrics metrics;
        switch (getProvider()) {
            default:
                metrics = new PageSpeed(((JSONObject) har.log().pages.get(0)).
                        get("title").toString() + " " + harName,
                        r.savedHars.get(harName).get(i), H_PS_EXE);
                break;
        }
        return metrics;
    }

    private static Module getProvider() {
        return Module.toModule(
                System.getProperty(MODULE, Module.DEF.name())
                        .toUpperCase()
        );
    }

    public static boolean isModule(Module m) {
        return getProvider().equals(m);
    }

}
