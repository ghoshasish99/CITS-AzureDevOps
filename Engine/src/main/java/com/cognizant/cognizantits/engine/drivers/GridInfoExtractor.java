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
package com.cognizant.cognizantits.engine.drivers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.openqa.selenium.remote.SessionId;

/**
 * extract selenium grid node information source:
 *
 */
public class GridInfoExtractor {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String[] getHostNameAndPort(String hostName, int port, SessionId session) {
        String[] hostAndPort = new String[2];
        String errorMsg = "Failed to acquire remote webdriver node and port info. Root cause: \n";

        try {
            HttpHost host = new HttpHost(hostName, port);
            CloseableHttpClient client = HttpClients.createSystem();
            URL sessionURL = new URL("http://" + hostName + ":" + port + "/grid/api/testsession?session=" + session);
            BasicHttpEntityEnclosingRequest r = new BasicHttpEntityEnclosingRequest("POST", sessionURL.toExternalForm());
            HttpResponse response = client.execute(host, r);
            String url = extractUrlFromResponse(response);
            if (url != null) {
                URL myURL = new URL(url);
                if ((myURL.getHost() != null) && (myURL.getPort() != -1)) {
                    hostAndPort[0] = myURL.getHost();
                    hostAndPort[1] = Integer.toString(myURL.getPort());
                }
            }
        } catch (Exception e) {
            Logger.getLogger(GridInfoExtractor.class.getName()).log(Level.SEVERE, null, errorMsg + e);
        }
        return hostAndPort;
    }

    @SuppressWarnings("unchecked")
	private static String extractUrlFromResponse(HttpResponse resp) {
        StringBuilder s;
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()))) {
            s = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                s.append(line);
            }
            if (s.length() > 0) {
                Map<Object, Object> val = mapper.readValue(s.toString(), Map.class);
                if (!val.isEmpty()) {
                    return val.get("proxyId").toString();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(GridInfoExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
