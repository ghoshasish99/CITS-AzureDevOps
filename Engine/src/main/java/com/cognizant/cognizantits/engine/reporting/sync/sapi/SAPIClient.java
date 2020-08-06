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

import com.cognizant.cognizantits.engine.reporting.sync.BasicHttpClient;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHeader;

/**
 *
 * 
 */
public class SAPIClient extends BasicHttpClient {

    Header auth;

    public SAPIClient(String url, String userName, String password) throws MalformedURLException {
        super(new URL(url), userName, password);
    }

    public SAPIClient(Map<String, String> op) throws MalformedURLException {
        this(op.get("api.status.link"), "", "");
        auth = new BasicHeader(HttpHeaders.AUTHORIZATION,
                op.get("api.status.auth").replaceAll(",$", ""));
    }

    @Override
    public void auth(HttpRequest req) {
        req.addHeader(auth);
    }

    
    public boolean hasProxy() {
        return false;
    }

}
