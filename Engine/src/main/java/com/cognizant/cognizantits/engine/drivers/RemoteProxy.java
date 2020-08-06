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

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.remote.CommandInfo;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.internal.ApacheHttpClient;

public class RemoteProxy {

    public static HttpCommandExecutor getProxyExecutor(URL url, Properties prop) {

        prop = decrypt(prop);

        String proxyHost = prop.getProperty("proxyHost");
        int proxyPort = Integer.valueOf(prop.getProperty("proxyPort"));
        String proxyUserDomain = prop.getProperty("proxyUserDomain");
        String proxyUser = prop.getProperty("proxyUser");
        String proxyPassword = prop.getProperty("proxyPassword");

        HttpClientBuilder builder = HttpClientBuilder.create();
        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();

        credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort),
                new NTCredentials(proxyUser, proxyPassword, getWorkstation(), proxyUserDomain));
        if (url.getUserInfo() != null && !url.getUserInfo().isEmpty()) {
            credsProvider.setCredentials(new AuthScope(url.getHost(), (url.getPort() > 0 ? url.getPort() : url.getDefaultPort())),
                    new UsernamePasswordCredentials(proxyUser, proxyPassword));
        }
        builder.setProxy(proxy);
        builder.setDefaultCredentialsProvider(credsProvider);
        HttpClient.Factory factory = new SimpleHttpClientFactory(builder);
        return new HttpCommandExecutor(new HashMap<String, CommandInfo>(), url, factory);

    }

    private static Properties decrypt(Properties prop) {
        Properties dprop = new Properties();
        dprop.putAll(prop);
        dprop.forEach((key, val) -> {
            if (val.toString().matches((".* Enc"))) {
                dprop.put(key, new String(Base64.decodeBase64(val.toString().replace(" Enc", ""))));
            }
        });
        return dprop;
    }

    private static String getWorkstation() {
        Map<String, String> env = System.getenv();

        if (env.containsKey("COMPUTERNAME")) {
            // Windows
            return env.get("COMPUTERNAME");
        } else if (env.containsKey("HOSTNAME")) {
            // Unix/Linux/MacOS
            return env.get("HOSTNAME");
        } else {
            // From DNS
            try {
                return InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ex) {
                return "Unknown";
            }
        }
    }
}

class SimpleHttpClientFactory implements HttpClient.Factory {

    final HttpClientBuilder builder;

    public SimpleHttpClientFactory(HttpClientBuilder builder) {
        this.builder = builder;
    }

    @Override
    public org.openqa.selenium.remote.http.HttpClient createClient(URL url) {
        return new ApacheHttpClient(builder.build(), url);
    }

    @Override
    public void cleanupIdleClients() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
}
