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
package com.cognizant.cognizantits.engine.reporting.sync.jira;

import com.cognizant.cognizantits.engine.support.DLogger;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

public class JIRAClient {

    private static final Logger LOG = Logger.getLogger(JIRAClient.class.getName());

    public static final String ISSUE = "rest/api/latest/issue",
            ISSUE_ATTACHMENTS = "rest/api/latest/issue/issuekey/attachments",
            PROJECT = "rest/api/latest/project";

    private final JIRAHttpClient client;

    public JIRAClient(String urlStr, String userName, String password, Map options)
            throws MalformedURLException {
        client = new JIRAHttpClient(toURL(urlStr), userName, password, options);
    }

    private static URL toURL(String url) throws MalformedURLException {
        if (!url.endsWith("/")) {
            url = url.concat("/");
        }
        return new URL(url);
    }

    /**
     * connect with ZAPIClient to execution get execution id and call the update
     * result
     *
     * @param status
     * @param tc
     * @param ts
     * @param rc
     * @param proj
     * @return
     * @throws Exception
     * @see #updateResult(int, int,
     * com.cognizant.reporting.sync.jira.JIRAHttpClient)
     */
    public int updateResult(int status, String tc, String ts, String rc,
            String proj) throws Exception {
        DLogger.Log("Req EID with", "Testcase : ", tc, "TestSet :", ts,
                "Release :", rc, "Project :", proj);
        int eid = ZAPIClient.getExecutionID(tc, ts, rc, proj, client);
        if (eid > 0) {
            updateResult(status, eid);
        }
        return eid;
    }

    /**
     *
     * connect with ZAPIClient to update execution(test case) result
     *
     * @param status execution status
     * @param eid execution ID
     * @throws Exception
     */
    public void updateResult(int status, int eid) throws Exception {
        ZAPIClient.updateResult(status, eid, client);
    }

    /**
     * connect with ZAPIClient to update execution(test case) attachment (report
     * )
     *
     * @param eid execution ID
     * @param attachment file to upload
     * @return
     */
    public String addAttachment(int eid, File attachment) {
        return ZAPIClient.addAttachment(eid, attachment, client);
    }

    /**
     * upload the bug from given details (using JIRA rest api)
     *
     * @param issue issue details to upload
     * @param attachments dependent attachments to upload(report)
     * @return
     */
    @SuppressWarnings("unchecked")
    public JSONObject createIssue(JSONObject issue, List<File> attachments) {
        JSONObject res = null;
        try {
            res = client.post(new URL(client.url + ISSUE), issue.toString());
            String restAttach = ISSUE_ATTACHMENTS.replace("issuekey",
                    (String) res.get("id"));
            if (attachments != null && !attachments.isEmpty()) {
                List<JSONObject> attchRes = new ArrayList<>();
                res.put("Attachments", attchRes);
                for (File f : attachments) {
                    attchRes.add(client.post(new URL(client.url + restAttach), f));
                }
            } else {
                LOG.log(Level.INFO, "no attachments to upload");
            }
            LOG.log(Level.INFO, "issue response {0}", res.toString());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return res;
    }

    /**
     * create the json data for JIRA api from map
     *
     * @param dataMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public static JSONObject getJsonified(LinkedHashMap<String, String> dataMap) {
        JSONObject fields = new JSONObject();

        JSONObject project = new JSONObject();
        project.put("key", dataMap.get("project"));
        dataMap.remove("project");

        JSONObject issueType = new JSONObject();
        issueType.put("name", dataMap.get("issueType"));
        dataMap.remove("issueType");

        fields.put("project", project);
        fields.put("issuetype", issueType);

        for (String key : dataMap.keySet()) {
            JSONObject obj;
            switch (key) {
                case "priority":
                    obj = new JSONObject();
                    obj.put("name", dataMap.get(key));
                    fields.put(key, obj);
                    break;
                case "assignee":
                    obj = new JSONObject();
                    obj.put("name", dataMap.get(key));
                    fields.put(key, obj);
                    break;
                default:
                    fields.put(key, dataMap.get(key));
                    break;
            }
        }
        JSONObject data = new JSONObject();
        data.put("fields", fields);
        DLogger.Log(data);
        return data;
    }

    /**
     * check for project existence , used for test connection feature
     *
     * @param project
     * @return
     */
    public boolean containsProject(String project) {
        try {
            String res = client.Get(new URL(client.url + PROJECT)).toString();
            return res.contains("\"key\":\"" + project + "\"")
                    || ZAPIClient.getProjID(project, client) != -1;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
    }

    public boolean isConnected() {
        try {
            DLogger.Log(client.Get(new URL(client.url + PROJECT)).toString());
            return true;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
    }

}
