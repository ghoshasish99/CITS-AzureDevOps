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
package com.cognizant.cognizantits.engine.reporting.sync.zephyr;

import com.cognizant.cognizantits.engine.core.RunManager;
import com.cognizant.cognizantits.engine.reporting.sync.Sync;
import static com.cognizant.cognizantits.engine.reporting.sync.zephyr.SoapZephyrUtil.loginToZephyr;
import static com.cognizant.cognizantits.engine.reporting.sync.zephyr.SoapZephyrUtil.sendSOAPMessage;
import static com.cognizant.cognizantits.engine.reporting.sync.zephyr.SoapZephyrUtil.updateTestStatus;
import static com.cognizant.cognizantits.engine.reporting.sync.zephyr.SoapZephyrUtil.uploadAttachment;
import com.cognizant.cognizantits.engine.reporting.util.TestInfo;
import com.cognizant.cognizantits.engine.support.DLogger;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPathExpressionException;
import org.json.simple.JSONObject;

/**
 *
 * 
 */
public class ZephyrSync implements Sync {

    private ZephyrClient conn;
    private String project = "";
    private String token;

    public ZephyrSync(String url, String username, String password, String project, String phase) {
        conn = new ZephyrClient(url, username, password, phase);
        this.project = project;
    }

    /**
     * 
     * @param options
     */
    public ZephyrSync(Properties options) {
        this(options.getProperty("ZephyrUrl"),
                options.getProperty("ZephyrUserName"),
                options.getProperty("ZephyrPassword"),
                options.getProperty("ZephyrProject"),
                options.getProperty("ZephyrTestPhase"));
    }

    @Override
    public String getModule() {
        return "Zephyr";
    }

    @Override
    public boolean isConnected() {
        try {
            token = login();
            ZephyrHttpClient jc = new ZephyrHttpClient(conn.url, conn.userName,
                    conn.password);
            return (token != null && conn.containsProject(project, jc));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private String login() throws Exception {
        String tokenStr = null;
        ZephyrHttpClient jc = new ZephyrHttpClient(conn.url, conn.userName,
                conn.password);
        SOAPMessage login = loginToZephyr(conn);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                "proxy.cognizant.com", 6050));
        SOAPMessage soapResponse = sendSOAPMessage(login, conn.url + SOAP_WSDL, proxy);
        tokenStr = SoapZephyrUtil.parseSOAPResponse(soapResponse, "//return");
        return tokenStr;
    }

    private static final Logger LOG = Logger.getLogger(ZephyrSync.class.getName());

    @Override
    public boolean updateResults(TestInfo ti, String status, List<File> list) {
        ZephyrHttpClient jc = new ZephyrHttpClient(conn.url, conn.userName, conn.password);
        String rls = RunManager.getGlobalSettings().getRelease();
        String tset = RunManager.getGlobalSettings().getTestSet();
        try {
            //Release and testset needs to be passed from Run Manager
            int eid = conn.updateResult(ti.testCase, tset, rls, project, jc);
            if (eid > 0) {
                updateResult(eid, getStatus(status), conn, list);
            } else {
                return false;
            }
            DLogger.LogE("Results updated for TestCase/Test [" + ti.testCase + "]");
            return true;
        } catch (Exception ex) {
            Logger.getLogger(ZephyrSync.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public String createIssue(JSONObject jsono, List<File> list) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void disConnect() {
        conn = null;
    }

    private int getStatus(String status) {
        switch (status.toUpperCase()) {
            case "PASSED":
                return 1;
            case "FAILED":
                return 2;
            case "WIP":
                return 3;
            default:
                return -1;

        }
    }

    private void updateResult(int eid, int status, ZephyrClient conn, List<File> list) throws XPathExpressionException {
        System.setProperty("java.net.useSystemProxies", "true");
        try {
            String url = conn.url + SOAP_WSDL;
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                    "proxy.cognizant.com", 6050));
            if (this.token == null) {
                login();
            }
            //Token shouldn't be null
            if (token != null) {
                //2. Update the Test status
                SOAPMessage updateTS = updateTestStatus(eid, status, token);
                SOAPMessage soapResponse = sendSOAPMessage(updateTS, url, proxy);
                DLogger.Log("Update test status ", SoapZephyrUtil.parseSOAPResponse(soapResponse, "//value"));

                //3. Upload the attachment
                for (File file : list) {
                    SOAPMessage uploadAttach = uploadAttachment(eid, token, file);
                    soapResponse = sendSOAPMessage(uploadAttach, url, proxy);
                    DLogger.Log("Attachment upload status ", SoapZephyrUtil.parseSOAPResponse(soapResponse, "//value"));
                }
            }
        } catch (SOAPException ex) {
            Logger.getLogger(ZephyrSync.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ZephyrSync.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ZephyrSync.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private static final String SOAP_WSDL = "/flex/services/soap/zephyrsoapservice-v1";
}
