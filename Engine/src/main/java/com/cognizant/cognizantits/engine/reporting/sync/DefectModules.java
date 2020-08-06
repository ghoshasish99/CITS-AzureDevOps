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

import com.cognizant.cognizantits.engine.constants.FilePath;
import com.cognizant.cognizantits.engine.reporting.sync.jira.JIRAClient;
import com.cognizant.cognizantits.engine.reporting.sync.jira.JIRASync;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;

public class DefectModules {

    public static String uploadDefect(String moduleId,
            LinkedHashMap<String, String> fields, List<File> attach) {
        String project = fields.get("project");
        String result = null;
        Sync sync;
        try {
            Data.initData();
            switch (moduleId) {
                case "JIRA":
                    sync = new JIRASync(Data.server, Data.uname, Data.pass, project, Data.options);
                    if (sync.isConnected()) {
                        JSONObject data = JIRAClient.getJsonified(fields);
                        result = sync.createIssue(data, attach);
                    } else {
                        result = "Error: Unable to Connect to Server";
                    }
                    break;
                case "QC":
                    result = "Not suported yet!";
                    break;

            }
        } catch (Exception ex) {
            result = "Error: " + ex.getMessage();
            Logger.getLogger(DefectModules.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }
}

class Data {

    public static String server = "", uname = "", pass = "", project = "DEMO",
            domain = "";
    public static Properties options = new Properties();

    public static void initData() throws Exception {
        options.load(new FileReader(FilePath.getExplorerConfig()));
        server = options.getProperty("URL");
        checkServer(server);
        uname = getDecoded(options.getProperty("UserName"));
        pass = getDecoded(options.getProperty("Password"));
        domain = options.getProperty("Domain");
        project = options.getProperty("Project");
    }

    static String getDecoded(String val) {
        byte[] valueDecoded = Base64.decodeBase64(val);
        return new String(valueDecoded);
    }

    static void checkServer(String url) throws Exception {
        if (url == null || url.isEmpty()) {
            throw new Exception("Server URL is Empty!!");
        }
        new URI(url);
    }

}
