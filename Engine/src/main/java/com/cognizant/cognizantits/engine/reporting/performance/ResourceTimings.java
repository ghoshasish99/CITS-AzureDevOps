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
package com.cognizant.cognizantits.engine.reporting.performance;

import com.cognizant.cognizantits.engine.reporting.performance.har.MIME;

/**
 * Resource Timings api
 *
 * 
 */
public class ResourceTimings {

    public Double connectEnd,
            connectStart,
            domainLookupEnd,
            domainLookupStart,
            duration,
            fetchStart,
            redirectEnd,
            redirectStart,
            requestStart,
            responseEnd,
            responseStart,
            secureConnectionStart,
            startTime;
    public String entryType,
            initiatorType,
            name;

    /**
     * initial adjustment makes sure timings in the desired order
     */
    public void adjust() {

        fetchStart = Math.max(fetchStart, startTime);
        connectStart = Math.max(fetchStart, connectStart);
        redirectStart = Math.max(redirectStart, startTime);
        responseEnd = Math.max(redirectStart, responseEnd);
        domainLookupStart = Math.max(domainLookupStart, fetchStart);
        domainLookupEnd = Math.max(domainLookupStart, domainLookupEnd);
        secureConnectionStart = Math.max(secureConnectionStart, connectStart);
        connectEnd = Math.max(secureConnectionStart, connectEnd);
        requestStart = Math.max(requestStart, connectEnd);
        responseStart = Math.max(requestStart, responseStart);

    }

    public String mimeType() {

        String mime = com.cognizant.cognizantits.engine.util.data.mime.MIME.getType(name);
        if (mime != null && !mime.isEmpty()) {
            return mime;
        }
        if ("script".equalsIgnoreCase(initiatorType) || name.endsWith(".js")) {
            return MIME.JS.val();
        } else if ("image".equalsIgnoreCase(initiatorType)) {
            return "image/" + (name.contains(".") ?
                    name.substring(name.lastIndexOf(".") + 1) : initiatorType);
        }
        return this.initiatorType;
    }

    /**
     * java script to extract resource timings
     *
     * @return
     */
    public static String script() {
        return "var dmp=window.performance.getEntriesByType('resource');"
                + "var resources=[];"
                + "for(var r in dmp){"
                + "var resource={};"
                + "for(var k in dmp[r]){resource[k]=dmp[r][k];}"
                + "resource.toJSON=undefined;"
                + "resources.push(resource);}"
                + "return JSON.stringify(resources);";
    }

}
