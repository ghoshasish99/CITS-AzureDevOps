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
package com.cognizant.cognizantits.engine.reporting.sync.qc.rest;

import com.cognizant.cognizantits.datalib.util.data.FileScanner;
import com.cognizant.cognizantits.engine.constants.FilePath;
import com.cognizant.cognizantits.engine.core.Control;
import com.cognizant.cognizantits.engine.core.RunManager;
import com.cognizant.cognizantits.engine.reporting.sync.Sync;
import com.cognizant.cognizantits.engine.reporting.sync.qc.rest.util.ServerException;
import com.cognizant.cognizantits.engine.reporting.util.TestInfo;
import com.cognizant.cognizantits.engine.util.data.KeyMap;
import java.io.File;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

/**
 * Upload test results to Quality Center using QC REST API
 * <br><br>
 * <b>Supports</b>:
 * <br>create new test case run with status
 * <br>upload report with screen-shots
 *
 * 
 *
 */
public class QCRestSync implements Sync {

    QCRestClient client;
    private static final Logger LOG = Logger.getLogger(QCRestSync.class.getName());

    private static final String TEMP_PATH
            = "/reporting/qc/rest/entity/";
    private static final String TEMP_NEW_RUN
            = TEMP_PATH.concat("run.post.xml");
    private final Properties vMap;

    /**
     * @throws java.lang.Exception
     * 
     * @param ops
     */
    public QCRestSync(Properties ops) throws Exception {
        LOG.info("Initializing TM integration with QC Rest API");
        client = new QCRestClient(ops.getProperty("QCUrl"),
                ops.getProperty("QCUserName"),
                ops.getProperty("QCPassword"),
                ops.getProperty("QCDomain"),
                ops.getProperty("QCProject"),ops);
        vMap = new Properties(ops);
        client.login();
        init();
    }

    @Override
    public String getModule() {
        return "QC_REST";
    }

    @Override
    public boolean isConnected() {
        return client.isLoggedIn();
    }

    private void init() throws Exception {

        vMap.put("user.name", client.usr);
        if (client.isLoggedIn()) {
            try {
                String tsPath = vMap.getProperty("qcTestsetLocation");
                String tsName = vMap.getProperty("qcTestsetName");
                vMap.putAll(RunManager.getGlobalSettings());
                vMap.putAll(Control.getCurrentProject().getProjectSettings().getUserDefinedSettings());
                tsPath = KeyMap.resolveContextVars(tsPath, vMap);
                tsName = KeyMap.resolveContextVars(tsName, vMap);

                String testset_folderId, testsetId;
                testset_folderId = findTestSetFolderIdFromPath(tsPath);
                testsetId = client.getTestSetId(testset_folderId, tsName);
                vMap.put("testset.id", testsetId);
                vMap.put("os.name", System.getProperty("os.name"));
                vMap.put("host.name", InetAddress.getLocalHost().getHostName());
            } catch (Exception ex) {
                LOG.severe(ex.getMessage());
            }
        }
    }

    private String findTestSetFolderIdFromPath(String tsPath) throws Exception {

        String[] folders = tsPath.split("\\\\|/", 0);
        String testset_folderId = "0";
        for (String folder : folders) {
            if ("Root".equals(folder) || folder.isEmpty()) {
                continue;
            }
            testset_folderId = client.getTestSetFolderId(folder, testset_folderId);
        }
        return testset_folderId;
    }

    @Override
    public boolean updateResults(TestInfo tc, String status, List<File> files) {
        try {
            LOG.log(Level.INFO, "Conneting qc to update results");
            String testFolderId, testId, instanceId, runId;
            testFolderId = client.getTestFolderId(tc.testScenario);
            testId = client.getTestId(testFolderId, tc.testCase);
            instanceId = client.getTestInstanceId(
                    vMap.getProperty("testset.id"), testId);
            if (!StringUtils.isNumeric(instanceId)) {
                System.out.print(
                        String.format("Instance not found for test //%s/%s(id:%s) ",
                                tc.testScenario, tc.testCase, testId));
                return false;
            }
            vMap.put("run.name", String.format("%s@%s_%s", tc.testCase, tc.date, tc.time));
            vMap.put("instance.id", instanceId);
            vMap.put("testcase.id", testId);
            vMap.put("run.status", "Not Completed");
            vMap.put("run.time", tc.getExeTime());
            vMap.put("run.desc", tc.testDescription);
            vMap.put("run.iteration", tc.iteration);
            vMap.put("run.platform", tc.platform);
            String  runTemplate = FileScanner.readStream(
                    QCRestSync.class.getResourceAsStream(TEMP_NEW_RUN));
            System.out.print(String.format("Status : %s | ", status));
            runId = client.createNewRun(payload(runTemplate, vMap));
            vMap.put("run.status", status);
            client.updateRun(payload(runTemplate, vMap), runId);
            if (StringUtils.isNumeric(runId)) {
                System.out.print(String.format(" New RunId : %s | ", runId));
                if (files != null && !files.isEmpty()) {
                    System.out.println(String.format(" Attachments : %s ", files.size()));
                    for (File file : files) {
                        try {
                            client.uploadAttachment(runId, file);
                        } catch (ServerException ex) {
                            String name = file.getName() + ".err.log.xml";
                            LOG.log(Level.SEVERE, "error uploading {0}, see log {1}",
                                    new Object[]{file.getName(), name});
                            ex.log(new File(FilePath.getCurrentResultsPath(), "logs"), name);
                        }
                    }
                }
                return true;
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return false;
    }

    @Override
    public String createIssue(JSONObject issue, List<File> attach) {
        return "Not Supported yet!!!!";
    }

    @Override
    public void disConnect() {
        try {
            client.logout();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private String payload(String tmpl, Map map) {
        return KeyMap.resolveContextVars(tmpl, map);
    }
}
