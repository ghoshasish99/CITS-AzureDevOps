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
package com.cognizant.cognizantits.engine.reporting.sync.sapi;

import com.cognizant.cognizantits.engine.constants.SystemDefaults;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * 
 */
public class ApiLink {

    private final RunStatus status;
    private final SAPIClient client;

    public ApiLink() throws MalformedURLException {
        status = new RunStatus();
        status.nopassTests = status.nofailTests = 0;      
        client = new SAPIClient(SystemDefaults.EnvVars);

    }
    

    public void setNoTests(int nos) {
        status.noTests = nos;
    }

    public void passed() {
        status.nopassTests++;
    }

    public void failed() {
        status.nofailTests++;
    }

    public void update() {
        try {
            client.put(client.url, new ObjectMapper().writeValueAsString(status));
        } catch (Exception ex) {
            Logger.getLogger(ApiLink.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void update(int pass, int fail) {
        status.nopassTests = pass;
        status.nofailTests = fail;
        update();
    }

    public void setThreads(int threadCount) {
        status.maxThreads = threadCount;
    }

    public void setStartTime(String runTime) {
        status.startTime = runTime;
    }

    public void setRunName(String runName) {
        status.runName = runName;
    }

    public void setExeMode(String mode) {
        status.runConfiguration = mode;
    }

    public void setIterMode(String mode) {
        status.iterationMode = mode;
    }

    public void clientData(JSONObject testSetData) {
        status.data = testSetData;
    }

}
