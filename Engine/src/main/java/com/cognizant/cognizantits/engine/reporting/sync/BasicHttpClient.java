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
package com.cognizant.cognizantits.engine.reporting.sync;

import com.cognizant.cognizantits.engine.support.DLogger;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.json.simple.JSONObject;

public class BasicHttpClient extends AbstractHttpClient {

    private static final Logger LOG = Logger.getLogger(BasicHttpClient.class.getName());

    public final URL url;
    private CloseableHttpClient client;
    private HttpContext context;
    /**
     * false - if the server has untrusted SSL (accept all cert) true - for
     * default system keystore
     */
    private boolean trusted = false;
    private UsernamePasswordCredentials creds;
    private HttpHost proxy;

    public BasicHttpClient(URL url, String userName, String password) {
        this(url, userName, password, null);
    }

    public BasicHttpClient(URL url, String userName, String password, Map<String, String> config) {
        this.url = url;
        try {
            client = trusted ? getSystemClient() : getCustomClient();
            creds = new UsernamePasswordCredentials(userName, password);
            context = createContext(url.toURI(), creds);
            if (config != null && Boolean.valueOf(config.get("useProxy"))) {
                this.proxy = new HttpHost(config.get("proxyHost"),
                        Integer.valueOf(config.get("proxyPort")));
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creating HttpClient!", ex);
        }
    }

    /**
     * basic auth implementation
     *
     * @param req
     * @throws AuthenticationException
     */
    public void auth(HttpRequest req) throws AuthenticationException {
        req.addHeader(new BasicScheme().authenticate(creds, req, context));
    }

    /**
     * execute the given URI request
     *
     * @param req
     * @return
     * @throws Exception
     */
    @Override
    public CloseableHttpResponse execute(HttpUriRequest req) throws Exception {
        DLogger.Log(req.toString());
        Optional.ofNullable(proxy).ifPresent((p)
                -> ((HttpRequestBase) req).setConfig(RequestConfig.custom().setProxy(p).build()));
        return client.execute(req, context);
    }

// <editor-fold defaultstate="collapsed" desc="PUT implementation">
    /**
     * Http Post request for given data as JSON string
     *
     * @param targetUrl
     * @param data
     * @return
     * @throws Exception
     */
    public JSONObject put(URL targetUrl, String data) throws Exception {
        HttpPut httpput = new HttpPut(targetUrl.toURI());
        setPutEntity(data, httpput);
        auth(httpput);
        setHeader(httpput);
        return parseResponse(doPut(httpput));
    }

    /**
     * custom header for respective client
     *
     * @param httpput
     */
    public void setHeader(HttpPut httpput) {
        httpput.addHeader("Accept", "application/json");
    }

    public void setPutEntity(String xmlstr, HttpPut httpput) throws UnsupportedEncodingException {
        StringEntity input = new StringEntity(xmlstr);
        if (xmlstr != null && !xmlstr.isEmpty()) {
            input.setContentType("application/json");
        }
        httpput.setEntity(input);
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="POST implementation">
    /**
     * Http Post request for given data as JSON string
     *
     * @param targetUrl
     * @param payload
     * @return
     * @throws Exception
     */
    public JSONObject post(URL targetUrl, String payload) throws Exception {
        HttpPost httppost = new HttpPost(targetUrl.toURI());
        setPostEntity(payload, httppost);
        return parseResponse(doPost(httppost));
    }

    /**
     * Http Post request for uploading files
     *
     * @param targetUrl
     * @param toUplod
     * @return
     * @throws Exception
     */
    public JSONObject post(URL targetUrl, File toUplod) throws Exception {
        HttpPost httppost = new HttpPost(targetUrl.toURI());
        setPostEntity(toUplod, httppost);
        return parseResponse(doPost(httppost));
    }

    /**
     * Http Post request for uploading files
     *
     * @param targetUrl
     * @param data
     * @param toUplod
     * @return
     * @throws Exception
     */
    public JSONObject post(URL targetUrl, String data, File toUplod) throws Exception {
        HttpPost httppost = new HttpPost(targetUrl.toURI());
        setPostEntity(data, toUplod, httppost);
        return parseResponse(doPost(httppost));
    }

    /**
     * custom header for respective client
     *
     * @param httppost
     */
    public void setHeader(HttpPost httppost) {
        httppost.addHeader("Accept", "application/json");
    }

    @Override
    public HttpResponse doPost(HttpPost httpPost) throws Exception {
        auth(httpPost);
        setHeader(httpPost);
        return super.doPost(httpPost);
    }

    public void setPostEntity(File toUplod, HttpPost httppost) {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", toUplod,
                ContentType.APPLICATION_OCTET_STREAM, toUplod.getName());
        httppost.setEntity(builder.build());
    }

    public void setPostEntity(String data, File file, HttpPost httppost) {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
        builder.addTextBody("body", data, ContentType.APPLICATION_XML);
        httppost.setEntity(builder.build());
    }

    public void setPostEntity(String jsonStr, HttpPost httppost) throws UnsupportedEncodingException {
        StringEntity input = new StringEntity(jsonStr);
        input.setContentType("application/json");
        httppost.addHeader("accept", "application/json");
        httppost.setEntity(input);
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="PATCH implementation">
    public JSONObject patch(URL targetUrl, String payload) throws Exception {
        HttpPatch httppatch = new HttpPatch(targetUrl.toURI());
        auth(httppatch);
        setHeader(httppatch);
        setPatchEntity(payload, httppatch);
        return parseResponse(doPatch(httppatch));
    }

    /**
     * custom header for respective client
     *
     * @param httppatch
     */
    public void setHeader(HttpPatch httppatch) {
        httppatch.addHeader("Accept", "application/json");
    }

    public void setPatchEntity(String jsonStr, HttpPatch httppatch) throws UnsupportedEncodingException {
        StringEntity input = new StringEntity(jsonStr);
        input.setContentType("application/json");
        httppatch.setEntity(input);
    }
// </editor-fold>

// <editor-fold defaultstate="collapsed" desc="GET implementation">
    /**
     * Http Get request for given url
     *
     * @param targetUrl
     * @return
     * @throws Exception
     */
    public JSONObject Get(URL targetUrl) throws Exception {
        return Get(targetUrl.toURI());
    }

    public JSONObject Get(URL targetUrl, String key, String val) throws Exception {
        URIBuilder builder = new URIBuilder(targetUrl.toString());
        builder.setParameter(key, val);
        return Get(builder.build());
    }

    /**
     * Http Get request for given params as JSON string
     *
     * @param targetUrl
     * @param jsonStr
     * @return
     * @throws Exception
     */
    public JSONObject Get(URL targetUrl, String jsonStr) throws Exception {
        URIBuilder builder = new URIBuilder(targetUrl.toString());
        setParams(builder, jsonStr);
        return Get(setParams(builder, jsonStr).build());
    }

    /**
     * custom header for respective client
     *
     * @param httpGet
     */
    public void setHeader(HttpGet httpGet) {
        httpGet.addHeader("Accept", "application/json");
    }

    private JSONObject Get(URI uri) throws Exception {
        HttpGet httpGet = new HttpGet(uri);
        auth(httpGet);
        setHeader(httpGet);
        return parseResponse(doGet(httpGet));
    }
// </editor-fold>

}
