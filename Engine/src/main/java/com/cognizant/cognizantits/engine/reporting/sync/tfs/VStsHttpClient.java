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

import com.cognizant.cognizantits.engine.reporting.sync.BasicHttpClient;
import java.net.URL;
import java.util.Map;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;

public class VStsHttpClient extends BasicHttpClient {

    final String ACCESSTOKEN;

    final String encodedToken;

    public VStsHttpClient(URL urL, String PAT,Map config) {
        super(urL, "", "",config);
        ACCESSTOKEN = PAT;
        encodedToken = java.util.Base64.getEncoder().encodeToString((ACCESSTOKEN + ":").getBytes());
    }

    @Override
    public void auth(HttpRequest req) throws AuthenticationException {
    }

    @Override
    public void setHeader(HttpGet httpget) {
        httpget.setHeader("Authorization", "Basic " + encodedToken);
        httpget.setHeader("Accept", "application/json");
    }

    @Override
    public void setHeader(HttpPost httppost) {
        httppost.setHeader("Authorization", "Basic " + encodedToken);
        httppost.setHeader("Content-Type", "application/json");
    }

    @Override
    public void setHeader(HttpPatch httppatch) {
        httppatch.setHeader("Authorization", "Basic " + encodedToken);
        httppatch.setHeader("Content-Type", "application/json");
    }

}
