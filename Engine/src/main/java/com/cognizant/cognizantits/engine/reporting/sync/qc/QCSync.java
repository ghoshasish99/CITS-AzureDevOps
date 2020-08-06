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
package com.cognizant.cognizantits.engine.reporting.sync.qc;

import com.cognizant.cognizantits.engine.core.Control;
import com.cognizant.cognizantits.engine.reporting.sync.Sync;
import com.cognizant.cognizantits.engine.reporting.util.TestInfo;
import com.cognizant.cognizantits.engine.util.data.KeyMap;
import com.cognizant.cognizantits.qcconnection.qcupdation.ITDConnection4;
import com.cognizant.cognizantits.qcconnection.qualitycenter.QualityCenter;
import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

public class QCSync implements Sync {

    private QualityCenter qc;
    private ITDConnection4 qcCon;

    public QCSync(String Url, String UserID, String Password, String Domain,
            String Project) {
        LOG.info("Initializing TM integration with Quality Center");
        qc = new QualityCenter();
        try {
            qcCon = qc.QClogin(Url, UserID, Password, Domain, Project);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    private static final Logger LOG = Logger.getLogger(QCSync.class.getName());

    /**
     * 
     * @param ops
     */
    public QCSync(Properties ops) {
        this(ops.getProperty("QCUrl"),
                ops.getProperty("QCUserName"),
                ops.getProperty("QCPassword"),
                ops.getProperty("QCDomain"),
                ops.getProperty("QCProject"));
    }

    @Override
    public boolean isConnected() {
        try {
            return qcCon != null && qcCon.connected();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public synchronized boolean updateResults(TestInfo tc, String status,
            List<File> attach) {
        try {
            String tsPath = Control.exe.getExecSettings().getTestMgmgtSettings().getProperty("qcTestsetLocation");
            String tsName = Control.exe.getExecSettings().getTestMgmgtSettings().getProperty("qcTestsetName");
            Properties vMap = tc.getMap();
            vMap.putAll(Control.exe.getProject().getProjectSettings().getUserDefinedSettings());
            tsPath = KeyMap.resolveContextVars(tsPath, vMap);
            tsName = KeyMap.resolveContextVars(tsName, vMap);
            if (qc.QCUpdateTCStatus(qcCon, tsPath, tsName, tc.testScenario,
                    tc.testCase, "[1]", tc.testCase + "@" + tc.date + "_"
                    + tc.time, status, attach)) {
                System.out.println("Result Updated  in QC-ALM !!!");
                return true;
            } else {
                System.out.println("Error while Updating Result in QC-ALM !!!");
            }
        } catch (Exception ex) {
            System.out.println("Error while Updating Result in QC-ALM !!!");
            LOG.log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public void disConnect() {
        qcCon.logout();
        qcCon.disconnect();
        qcCon.releaseConnection();
        qcCon.dispose();
    }

    @Override
    public String getModule() {
        return "QC";
    }

    @Override
    public String createIssue(JSONObject issue, List<File> attach) {
        return null;
    }

}
