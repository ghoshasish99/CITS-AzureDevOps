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
package com.cognizant.cognizantits.engine.reporting.performance.metrics.pagespeed;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * page speed insights helper object with validation and rules
 *
 * 
 * @param <K> key
 * @param <V> value
 * 
 */
@SuppressWarnings("unchecked")
public class Insight<K, V> extends JSONObject {

    private static final long serialVersionUID = 1L;

    public Insight(String name, int score) {
        put("name", name);
        put("score", score);
    }

    public void desc(String desc) {
        put("desc", desc);
    }

    public void addDescData(String data) {
        if (this.get("data") == null) {
            put("data", new JSONArray());
        }
        ((JSONArray) this.get("data")).add(parseData(data));

    }

    /**
     * check the data toe url and comments if comments exists add it as separate
     * entry
     *
     * @param dataS data to parse
     * @return data entry
     */
    public Object parseData(String dataS) {
        Matcher m = Pattern.compile("(.*) \\((.*)\\)").matcher(dataS);

        JSONObject data = new JSONObject();
        if (m.matches()) {
            data.put("url", m.group(1));
            data.put("comments", m.group(2));
        } else {
            data.put("url", dataS);
        }

        return data;
    }

}
