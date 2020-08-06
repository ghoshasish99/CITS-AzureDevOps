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
package com.cognizant.cognizantits.engine.reporting.sync.tfs;

import com.cognizant.cognizantits.engine.reporting.sync.Sync;
import com.cognizant.cognizantits.engine.reporting.util.TestInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 *
 */
public class VStsSync implements Sync {

    private VStsClient conn;
    private final ArrayList<VStsTestData> listOTest = new ArrayList<>();
    private String project = "";
    private int testPlanId;
    private static final Logger LOG = Logger.getLogger(VStsSync.class.getName());

    public VStsSync(String server, String PAT, String project, int testPlanId, Map config) {
        conn = new VStsClient(server, PAT, config);
        this.project = project;
        this.testPlanId = testPlanId;
    }

    /**
     *
     * @param options
     */
    public VStsSync(Properties options) {
        this(options.getProperty("VSTSUrl"), options.getProperty("VSTSAccessToken"),
                options.getProperty("VSTSProject"), Integer.valueOf(options.getProperty("VSTSTestPlanId")),
                options);
    }

    @Override
    public boolean isConnected() {
        try {
            return conn.isConnected() && conn.containsProject(project);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public void disConnect() {
        conn.createNewTestRun(listOTest);
        conn = null;
    }

    @Override
    public String getModule() {
        return "VS Team Services";
    }

    @Override
    public boolean updateResults(TestInfo tc, String status, List<File> attach) {
        VStsTestData test = new VStsTestData(project, testPlanId, tc.testScenario, tc.testCase, status, attach);
        listOTest.add(test);
        return true;
    }

    @Override
    public String createIssue(JSONObject issue, List<File> attach) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
