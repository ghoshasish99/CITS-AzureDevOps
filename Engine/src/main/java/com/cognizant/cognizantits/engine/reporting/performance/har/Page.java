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
package com.cognizant.cognizantits.engine.reporting.performance.har;

import com.cognizant.cognizantits.engine.reporting.performance.PerformanceTimings;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Page helper object for har
 *
 * 
 * 
 * @see Har.java
 */
@SuppressWarnings("unchecked")
public class Page extends JSONObject {

    private static final long serialVersionUID = 1L;
    private static final String DF = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    PerformanceTimings pt;

    public Page(String navTimings, int index) {
        super();
        Gson gson = new Gson();
        //parse string(json) result form timings api to #PerformanceTimings.class
        pt = gson.fromJson(navTimings, PerformanceTimings.class);
        put("startedDateTime", getMillstoDate(pt.navigationStart));
        put("id", "page_" + index);
        put("title", pt.url == null ? "" : pt.url);
        put("pageTimings", new PageTimings(pt));
        put("raw",JSONValue.parse(navTimings));        
    }

    public static String getMillstoDate(long nStart) {
        SimpleDateFormat df = new SimpleDateFormat(DF);
        return df.format(new Date(nStart));
    }

    public String getID() {
        return get("id").toString();
    }

    class PageTimings extends JSONObject {

        private static final long serialVersionUID = 1L;

        public PageTimings(PerformanceTimings pt) {
            put("onContentLoad", pt.domContentLoadedEventStart - pt.navigationStart);
            //get max of l.e.start || dom.Complete || dom.c.l.e.end
            put("onLoad", Math.max(pt.loadEventStart,Math.max(pt.domComplete, pt.domContentLoadedEventEnd))
                    - pt.navigationStart);
        }
    }
}
