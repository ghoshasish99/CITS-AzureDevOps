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

import com.cognizant.cognizantits.engine.reporting.sync.BasicHttpClient;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;

/**
 *
 * 
 */
public class ZephyrHttpClient extends BasicHttpClient {

    String TOKEN;

    public ZephyrHttpClient(URL urL, String userName, String password) {
        super(urL, userName, password);
        encodeCredentials(userName, password);
    }

    @Override
    public void setHeader(HttpGet httpget) {
        httpget.setHeader("Authorization", "Basic " + TOKEN);
        httpget.setHeader("Accept", "application/json");
    }

    @Override
    public void setHeader(HttpPost httppost) {
        httppost.setHeader("Authorization", "Basic " + TOKEN);
        httppost.setHeader("Content-Type", "application/json");
    }

    @Override
    public void setHeader(HttpPatch httppatch) {
        httppatch.setHeader("Authorization", "Basic " + TOKEN);
        httppatch.setHeader("Content-Type", "application/json");
    }

    private void encodeCredentials(String userName, String password) {
        String creds = userName + ":" + password;
        TOKEN = new String(Base64.encodeBase64(creds.getBytes()));
    }
}

