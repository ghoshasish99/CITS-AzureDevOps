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

import com.cognizant.cognizantits.engine.reporting.sync.BasicHttpClient;
import java.net.URL;
import java.util.Map;
import org.apache.http.client.methods.HttpPost;

public class JIRAHttpClient extends BasicHttpClient {

    public JIRAHttpClient(URL url, String userName, String password,Map config) {
        super(url, userName, password,config);
    }

    @Override
    public void setHeader(HttpPost httppost) {
        httppost.setHeader("X-Atlassian-Token", "nocheck");
    }

}
