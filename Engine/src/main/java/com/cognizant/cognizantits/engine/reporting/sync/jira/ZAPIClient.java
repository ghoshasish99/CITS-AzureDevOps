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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 *
 */
@SuppressWarnings("unchecked")
public class ZAPIClient {

    private static final Logger LOG = Logger.getLogger(ZAPIClient.class.getName());

    private static final String ZAPI = "rest/zapi/latest/",
            PROJLIST = "rest/zapi/latest/util/project-list",
            VERSIONLIST = "rest/zapi/latest/util/versionBoard-list?projectId=",
            CYCLELIST = "rest/zapi/latest/cycle?versionId=",
            EXELIST = "rest/zapi/latest/execution?cycleId=",
            EXECUTIONS = "rest/zapi/latest/execution",
            API = "rest/zapi/latest/moduleInfo",
            UPDATERESULT = "rest/zapi/latest/execution/{{eid}}/execute";

    class array {

        static final String PROJ = "options", VERSIONS = "versionOptions",
                EXECUTIONS = "executions";
    }

    public class status {

        public static final int PASS = 1, FAIL = 2, WIP = 3, BLOCKED = 4,
                UNEXECUTED = -1;

    }

    public static boolean isEnabled(JIRAHttpClient client) {

        try {
            URL projListUrl = new URL(client.url.toString() + API);
            JSONObject res = client.Get(projListUrl);
            return res.get("status").equals("ENABLED");

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return false;
        }
    }

    public static int getProjID(String projKey, JIRAHttpClient client) {
        int pid = -1;
        try {
            URL projListUrl = new URL(client.url.toString() + PROJLIST);
            DLogger.Log("Req Project List ", projListUrl.toString());
            JSONObject projList = client.Get(projListUrl);
            DLogger.Log("Looking for [", projKey, "] in", projList);
            for (Object proj : (Iterable<? extends Object>) projList
                    .get(array.PROJ)) {
                if (((Map<?, ?>) proj).get("label").toString()
                        .equalsIgnoreCase(projKey)) {
                    pid = Integer.valueOf(((Map<?, ?>) proj).get("value")
                            .toString());
                    break;
                }
            }
            if (pid == -1) {
                DLogger.LogE("Project [", projKey, "] not found");
            }

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return pid;
    }

    public static int getVersionID(String versionName, int projID,
            JIRAHttpClient client) {
        int vid = -1;
        try {
            URL projListUrl = new URL(client.url.toString() + VERSIONLIST
                    + String.valueOf(projID));
            DLogger.Log("Req Version List with Project ID [", projID, "] ",
                    projListUrl.toString());
            JSONObject versionInfo = client.Get(projListUrl);
            DLogger.Log("Looking for [", versionName, "] in", versionInfo);
            for (Object versionType : versionInfo.keySet()) {
                for (Object version : (Iterable<? extends Object>) versionInfo
                        .get(versionType)) {
                    // for diff. type of VERSIONS groups., array.VERSIONS changed 
                    // to iterate all types
                    if (((Map<?, ?>) version).get("label").toString()
                            .equalsIgnoreCase(versionName)) {
                        vid = Integer.valueOf(((Map<?, ?>) version)
                                .get("value").toString());
                        break;
                    }
                }
            }
            if (vid == -1) {
                DLogger.LogE("Release/Version [", versionName, "] not found");
            }

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return vid;
    }

    public static int getVersionID(String versionName, String projKey,
            JIRAHttpClient client) {
        return getVersionID(versionName, getProjID(projKey, client), client);
    }

    static int getCycleId(String cycleName, int versionID, JIRAHttpClient client) {

        int cid = -1;
        try {
            URL reqUrl = new URL(client.url.toString() + CYCLELIST
                    + String.valueOf(versionID));
            DLogger.Log("Req Cycle List with Version ID [", versionID, "] ",
                    reqUrl.toString());
            JSONObject cycleList = client.Get(reqUrl);
            DLogger.Log("Looking for [", cycleName, "] in", cycleList);
            for (Object key : cycleList.keySet()) {
                if (cycleList.get(key) instanceof Map) {
                    Map<?, ?> cycle = (Map<?, ?>) cycleList.get(key);
                    if (cycle.get("name").toString()
                            .equalsIgnoreCase(cycleName)) {
                        cid = Integer.valueOf(key.toString());
                        break;
                    }
                }
            }

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        if (cid == -1) {
            DLogger.LogE("TestSet/Cycle [", cycleName, "] not found");
        }
        return cid;
    }

    public static int getCycleId(String cycleName, String versionName,
            String projKey, JIRAHttpClient client) {
        return getCycleId(cycleName,
                getVersionID(versionName, projKey, client), client);
    }

    private static int getExecutionID(String testcaseName, int cycleId,
            JIRAHttpClient client) {
        int eid = -1;
        try {
            URL reqUrl = new URL(client.url.toString() + EXELIST
                    + String.valueOf(cycleId));
            DLogger.Log("Req Cycle List with Version ID [", cycleId, "] ",
                    reqUrl.toString());
            JSONObject executionList = client.Get(reqUrl);
            DLogger.Log("Looking for [", testcaseName, "] in", executionList);
            for (Object proj : (Iterable<? extends Object>) executionList
                    .get(array.EXECUTIONS)) {
                if (((Map<?, ?>) proj).get("summary").toString()
                        .equalsIgnoreCase(testcaseName)) {
                    eid = Integer.valueOf(((Map<?, ?>) proj).get("id")
                            .toString());
                    break;
                }
            }

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        if (eid == -1) {
            DLogger.LogE("TestCase/Test [", testcaseName, "] not found");
        }
        return eid;
    }

    public static int getExecutionID(String testcaseName, String cycleName,
            String versionName, String projKey, JIRAHttpClient client) {
        int id = getProjID(projKey, client);
        if (id > 0) {
            id = getVersionID(versionName, id, client);
            if (id > 0) {
                id = getCycleId(cycleName, id, client);
                if (id > 0) {
                    id = getExecutionID(testcaseName, id, client);
                }
            }
        }
        return id;
    }

    public static Map<?, ?> getExeMap(int cycleId, JIRAHttpClient client) {
        Map<Object, Integer> m = new HashMap<>();
        try {
            URL projListUrl = new URL(client.url.toString() + EXELIST
                    + String.valueOf(cycleId));
            JSONObject projList = client.Get(projListUrl);
            for (Object proj : (Iterable<? extends Object>) projList
                    .get(array.EXECUTIONS)) {
                m.put(((Map<?, ?>) proj).get("summary").toString(), Integer
                        .valueOf(((Map<?, ?>) proj).get("id").toString()));
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return m;
    }

    public static Map<Object, Integer> getExeMap(String jstr,
            JIRAHttpClient client) {
        Map<Object, Integer> m = new HashMap<>();
        try {
            URL projListUrl = new URL(client.url.toString() + EXECUTIONS);
            JSONObject res = client.Get(projListUrl, jstr);
            for (Object proj : (Iterable<? extends Object>) res
                    .get(array.EXECUTIONS)) {
                Object tc = ((Map<?, ?>) proj).get("summary");
                Object id = ((Map<?, ?>) proj).get("id");
                Object issuid = ((Map<?, ?>) proj).get("issueId");
                m.put(tc, Integer.valueOf(id.toString()));
                m.put(tc + ":" + id, Integer.valueOf(issuid.toString()));
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return m;
    }

    public static void updateResult(int status, int eid, JIRAHttpClient client)
            throws Exception {
        JSONObject jobj = new JSONObject();
        jobj.put("status", status);
        String rest = UPDATERESULT.replace("{{eid}}", String.valueOf(eid));
        URL targetUrl = new URL(client.url.toString() + rest);
        DLogger.Log("Updating Status with EID ", targetUrl.toString());
        Object response = client.put(targetUrl, jobj.toString());
        if (response == null) {
            DLogger.Log("Unknown Response : Check TestCase name");
        }
        DLogger.Log("Updating Status with EID,Response ", response);
    }

    public static String addAttachment(int eid, File toattach,
            JIRAHttpClient client) {
        String sr = "";
        try {
            URL projListUrl = new URL(client.url.toString() + ZAPI
                    + "attachment?entityId=" + eid + "&entityType=EXECUTION");
            DLogger.Log("Uploading Attachments with EID ",
                    projListUrl.toString());
            JSONObject res = client.post(projListUrl, toattach);
            DLogger.Log("Uploading Attachments with EID,Response ", res);
            sr = res.toString();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return sr;
    }

}
