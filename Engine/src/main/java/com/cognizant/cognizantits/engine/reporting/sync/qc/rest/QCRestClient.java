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

import com.cognizant.cognizantits.engine.reporting.sync.qc.rest.util.Q;
import com.cognizant.cognizantits.engine.reporting.sync.qc.rest.util.ServerException;
import com.cognizant.cognizantits.engine.reporting.sync.qc.rest.util.XML;
import com.cognizant.cognizantits.engine.reporting.sync.qc.rest.util.XML.BY;
import com.cognizant.cognizantits.engine.support.DLogger;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.eclipse.jetty.http.HttpStatus;
import org.json.simple.JSONObject;

/**
 * QC REST Client Implementation
 * <br><br>
 * <b>Handles</b>:
 * <br>QC session management
 * <br>login/logout
 * <br>API endpoints
 * <br>API wrappers
 *
 * 
 *
 */
public class QCRestClient {

    private static final Logger LOG = Logger.getLogger(QCRestClient.class.getName());

    private final QCRestHttpClient httpClient;
    public final String usr, domain, project;
    public String serverUrl;

    public QCRestClient(String url, String userId, String pass, String domain, String project,Map config) {
        setServerUrl(url);
        this.usr = userId;
        this.domain = domain;
        this.project = project;
        httpClient = new QCRestHttpClient(getUrl(serverUrl), userId, pass,config);
    }

    private void setServerUrl(String url) {
        if (!url.endsWith("/")) {
            url = url.concat("/");
        }
        serverUrl = url;
    }

    private URL getUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //<editor-fold defaultstate="collapsed" desc="session">
    private static final String IS_LOGGED_IN = "rest/is-authenticated";
    private static final String SESSION = "rest/site-session";
    private static final String LOGIN = "authentication-point/authenticate";
    private static final String LOGOUT = "authentication-point/logout";

    public void login() throws Exception {
        JSONObject sessionRes, loginRes = httpClient.Get(this.getUrl(buildUrl(LOGIN)));
        if (loginRes != null) {            
            httpClient.COOKIES = (Header[]) loginRes.get("COOKIE");
            httpClient.COOKIES = (Header[]) loginRes.get("COOKIE");
            sessionRes = httpClient.post(this.getUrl(buildUrl(SESSION)), "");
            if (sessionRes.get("status").equals(HttpStatus.CREATED_201)) {
                DLogger.Log("session created");
                httpClient.COOKIES = (Header[]) sessionRes.get("COOKIE");
            } else {
                DLogger.Log("session not available status : ", sessionRes.get("status"));
            }
            httpClient.LOGIN_KEY = true;
        } else {
            httpClient.LOGIN_KEY = null;
        }
    }

    public void logout() throws Exception {
        httpClient.Get(this.getUrl(buildUrl(LOGOUT)));
    }

    public boolean isLoggedIn() {
        try {
            JSONObject res = httpClient.Get(this.getUrl(buildUrl(IS_LOGGED_IN)));
            return res != null && Objects.deepEquals(200, res.get("status"))
                    && Objects.toString(res.get("res"), "").contains(usr);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return false;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="API wrappers">
    public String getTestFolderId(String name) throws Exception {
        JSONObject res = httpClient.Get(getUrl(testFoldersUrl()),
                Q.QUERY, String.format(Q.NAME, name));
        return XML.getID(getRes(res), BY.ROOT);
    }

    public String getTestId(String folderId, String name) throws Exception {
        JSONObject res = httpClient.Get(getUrl(testsUrl()),
                Q.QUERY, String.format(Q.NAME_PARENTID, name, folderId));
        return XML.getID(getRes(res), BY.ROOT);
    }

    public String getTestSetFolderId(String name, String parentId) throws Exception {
        JSONObject res = httpClient.Get(getUrl(testSetFoldersUrl()),
                Q.QUERY, String.format(Q.NAME_PARENTID, name, parentId));
        return XML.getID(getRes(res), BY.ROOT);
    }

    public String getTestSetId(String folderId, String name) throws Exception {
        JSONObject res = httpClient.Get(getUrl(testSetsUrl()),
                Q.QUERY, String.format(Q.NAME_PARENTID, name, folderId));
        return XML.getID(getRes(res), BY.ROOT);
    }

    public String getTestInstanceId(String testSetId, String testId) throws Exception {
        JSONObject res = httpClient.Get(getUrl(testInstanceUrl()),
                Q.QUERY, String.format(Q.TESTSET_OR_CYCLE_ID, testSetId));
        return XML.getID(getRes(res), BY.TEST_ID, testId);
    }

    public String updateInstance(String xmlPayload, String instanceId) throws Exception {
        JSONObject res = httpClient.put(getUrl(instanceUrl(instanceId)), xmlPayload);
        return XML.getID(getRes(res), BY.ROOT);
    }

    public String createNewRun(String xmlPayload) throws Exception {
        JSONObject res = httpClient.post(getUrl(runsUrl()), xmlPayload);
        return XML.getID(getRes(res), BY.ROOT);
    }
    public String updateRun(String xmlPayload,String runId) throws Exception {
        JSONObject res = httpClient.put(getUrl(runsUrl(runId)), xmlPayload);
        return XML.getID(getRes(res), BY.ROOT);
    }

    private static String getRes(JSONObject res) {
        return (String) res.get("res");
    }
    public String uploadAttachment(String runId, File attach) throws Exception {
        JSONObject res = httpClient.post(getUrl(runsAttachUrl(runId)), attach);
        String id = XML.getID(getRes(res), BY.ROOT);
        if (id == null || id.isEmpty() || !StringUtils.isNumeric(id)) {
            throw new ServerException(getRes(res));
        }
        return id;
    }

    public String getResponse(String endpoint) throws Exception {
        return getRes(httpClient.Get(getUrl(endpoint)));
    }

    public String getRunFiledsResponse() throws Exception {
        return getRes(httpClient.Get(getUrl(runReqFields().split("\\?")[0])));
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="endpoint-builder">
    private String buildUrl(String rest) {
        return serverUrl.concat(rest);
    }

    private String testsUrl() {
        return buildUrlByType("tests");
    }

    private String runsUrl() {
        return buildUrlByType("runs");
    }
    private String runsUrl(String id) {
        return String.format("%s/%s", runsUrl(), id);
    }

    private String instanceUrl(String instanceId) {
        return String.format("%s/%s", testInstanceUrl(), instanceId);
    }

    private String runsAttachUrl(String id) {
        return String.format("%s/%s/attachments", runsUrl(), id);
    }

    private String testSetsUrl() {
        return buildUrlByType("test-sets");
    }

    private String testInstanceUrl() {
        return buildUrlByType("test-instances");
    }

    private String testSetFoldersUrl() {
        return buildUrlByType("test-set-folders");
    }

    private String testFoldersUrl() {
        return buildUrlByType("test-folders");
    }

    private String runReqFields() {
        return buildUrlByType("customization/entities/run/fields?required=true");
    }

    @SuppressWarnings("unused")
    private String attachReqFields() {
        return buildUrlByType("customization/entities/attachment/fields?required=true");
    }

    @SuppressWarnings("unused")
    private String runAttachReqFields() {
        return buildUrlByType("customization/entities/run/attachment/fields?required=true");
    }

    private String buildUrlByType(String entityType) {
        return buildUrl("rest/domains/"
                + domain
                + "/projects/"
                + project
                + "/"
                + entityType);
    }
//</editor-fold>

}
