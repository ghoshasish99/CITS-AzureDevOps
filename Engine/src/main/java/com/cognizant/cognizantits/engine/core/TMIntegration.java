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

import com.cognizant.cognizantits.datalib.settings.TestMgmtSettings;
import com.cognizant.cognizantits.engine.reporting.SummaryReport;
import com.cognizant.cognizantits.engine.reporting.sync.Sync;
import com.cognizant.cognizantits.engine.reporting.sync.Unknown;
import com.cognizant.cognizantits.engine.reporting.sync.jira.JIRASync;
import com.cognizant.cognizantits.engine.reporting.sync.qc.QCSync;
import com.cognizant.cognizantits.engine.reporting.sync.qc.rest.QCRestSync;
import com.cognizant.cognizantits.engine.reporting.sync.tfs.VStsSync;
import com.cognizant.cognizantits.engine.reporting.sync.zephyr.ZephyrSync;
import java.util.Properties;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 
 */
public class TMIntegration {

    private static final Logger LOG = LoggerFactory.getLogger(TMIntegration.class.getName());

    public static void init(SummaryReport ReportManager) {
        LOG.debug("Trying to Initialize TestManagement Integration");
        if (!RunManager.getGlobalSettings().isTestRun()) {
            ReportManager.sync = getInstance(Control.exe.getExecSettings().getTestMgmgtSettings());
        } else {
            LOG.warn("TM integration disabled for testcases running via design mode");
        }
    }

    public static Sync getInstance(TestMgmtSettings testMgmgtSettings) {
        try {
            switch (testMgmgtSettings.getUpdateResultsToTM()) {
                case "None":
                    LOG.info("TM integration disabled for the testset");
                    return null;
                case "JIRA - Zephyr":
                    return new JIRASync(decryptValues(testMgmgtSettings));
                case "Quality Center":
                    return new QCSync(decryptValues(testMgmgtSettings));
                case "QC_REST":
                    return new QCRestSync(decryptValues(testMgmgtSettings));
                case "VS Team Services":
                    return new VStsSync(decryptValues(testMgmgtSettings));
                case "Zephyr":
                    return new ZephyrSync(decryptValues(testMgmgtSettings));
                default:
                    LOG.warn("Initializing TM integration with Unknown - " + testMgmgtSettings.getUpdateResultsToTM());
                    return new Unknown();

            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    public static boolean isEnabled() {
        return !RunManager.getGlobalSettings().isTestRun()
                && !Control.exe.getExecSettings().getTestMgmgtSettings().getUpdateResultsToTM()
                .equals("None");
    }

    public static String decrypt(String v) {
        if (isEnc(v)) {
            v = v.replaceFirst("TMENC:", "");
            return doDecrypt(v);
        } else {
            return v;
        }
    }

    public static String encrypt(String v) {
        if (!isEnc(v)) {
            return "TMENC:" + doEncrypt(v);
        }
        return v;
    }

    private static String doDecrypt(String v) {
        // do implement ur owm crypto
        return new String(Base64.decodeBase64(v));
    }

    private static String doEncrypt(String v) {
        // do implement ur owm crypto
        return new String(Base64.encodeBase64(v.getBytes()));
    }

    public static boolean isEnc(String v) {
        return v != null && v.startsWith("TMENC:");
    }

    private static Properties decryptValues(Properties ops) {
        for (String key : ops.stringPropertyNames()) {
            ops.put(key, decrypt(ops.getProperty(key, "")));
        }
        return ops;
    }

}
