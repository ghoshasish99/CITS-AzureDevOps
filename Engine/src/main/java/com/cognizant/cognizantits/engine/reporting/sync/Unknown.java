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

import com.cognizant.cognizantits.engine.reporting.util.TestInfo;
import java.io.File;
import java.util.List;
import org.json.simple.JSONObject;

public class Unknown implements Sync {

    @Override
    public boolean isConnected() {
        System.out.println("UNKNOWN CONNECTION!!");
        return false;
    }

    @Override
    public boolean updateResults(TestInfo TestCase, String status,
            List<File> attach) {
        System.out.println("UNKNOWN CONNECTION!!");
        return false;
    }

    @Override
    public void disConnect() {

    }

    @Override
    public String getModule() {
        return "UNKNOWN";
    }

    @Override
    public String createIssue(JSONObject issue, List<File> attach) {
        return null;
    }

}
