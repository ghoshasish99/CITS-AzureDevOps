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

import com.cognizant.cognizantits.engine.reporting.sync.BasicHttpClient;
import com.cognizant.cognizantits.engine.util.data.mime.MIME;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

/**
 * HTTP Helper for QC REST
 * <br><br>
 * <b>Handle</b>:
 * <br>Headers for QC REST
 * <br>Cookies for QC REST
 * <br>Parsing response for QC REST
 * <br>Data frame level custom headers and attributes
 *
 * 
 *
 */
public class QCRestHttpClient extends BasicHttpClient {

    private static final Logger LOG = Logger.getLogger(QCRestHttpClient.class.getName());
    public static final String LWSSO_COOKIE_KEY = "LWSSO_COOKIE_KEY";
    Object LOGIN_KEY;
    Header[] COOKIES = new Header[]{};

    public QCRestHttpClient(URL url, String userName, String password,Map config) {
        super(url, userName, password,config);
    }

    @Override
    public void setHeader(HttpPost req) {
        addCookies(req);
        setAccept(req);
    }

    @Override
    public void setHeader(HttpPut req) {
        addCookies(req);
        setAccept(req);
    }

    /**
     * custom header for respective client
     *
     * @param req
     */
    @Override
    public void setHeader(HttpGet req) {
        addCookies(req);
        setAccept(req);
    }

    public void addCookies(HttpUriRequest req) {
        if (Objects.nonNull(COOKIES)) {
            for (Header h : COOKIES) {
                req.addHeader(h);
            }
        }
    }

    public void setAccept(HttpUriRequest req) {
        req.addHeader("Accept", "application/xml");
    }

    @Override
    public void setPostEntity(String xmlstr, HttpPost httppost) throws UnsupportedEncodingException {
        StringEntity input = new StringEntity(xmlstr);
        if (xmlstr != null && !xmlstr.isEmpty()) {
            input.setContentType("application/xml");
        }
        httppost.setEntity(input);
    }

    @Override
    public void setPutEntity(String xmlstr, HttpPut httpput) throws UnsupportedEncodingException {
        StringEntity input = new StringEntity(xmlstr);
        if (xmlstr != null && !xmlstr.isEmpty()) {
            input.setContentType("application/xml");
        }
        httpput.setEntity(input);
    }

    @Override
    public void setPostEntity(File file, HttpPost httppost) {
        httppost.setHeader("Content-Type", "application/octet-stream");
        httppost.setHeader("Slug", file.getName());
        HttpEntity e = new FileEntity(file, ContentType.create(MIME.getType(file)));
        httppost.setEntity(e);
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject parseResponse(HttpResponse response) throws Exception {
        JSONObject jobj = new JSONObject();
        HttpEntity entity = response.getEntity();
        String resp;
        try {
            if (entity != null) {
                resp = EntityUtils.toString(entity);
                jobj.put("res", resp);
                jobj.put("status", response.getStatusLine().getStatusCode());
                if (LOGIN_KEY == null) {
                    setLoginCookie(jobj, response);
                }
                EntityUtils.consume(entity);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return jobj;
    }

    /**
     * set login/session cookie from a request
     *
     * @param jobj
     * @param response
     */
    @SuppressWarnings("unchecked")
    private void setLoginCookie(JSONObject jobj, HttpResponse response) {
        jobj.put("COOKIE", getCookies(response));
    }

    public Header[] getCookies(HttpResponse response) {
        return response.getHeaders("Set-Cookie");
    }

}
