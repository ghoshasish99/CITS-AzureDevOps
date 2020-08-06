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
package com.cognizant.cognizantits.engine.util.data;

import com.cognizant.cognizantits.engine.constants.FilePath;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 
 */
public class KeyMap {

    public static final Pattern CONTEXT_VARS = Pattern.compile("\\{(.+?)\\}");
    public static final Pattern ENV_VARS = Pattern.compile("\\$\\{(.+?)\\}");
    public static final Pattern USER_VARS = Pattern.compile("%(.+?)%");
    
    private static Map<Object,Object> systemVars;
    
    public static Map<Object, Object> getSystemVars(){
        if(systemVars==null){
            systemVars=new HashMap<>();
            systemVars.put("app.lib", FilePath.getLibPath());
            systemVars.put("app.root", FilePath.getAppRoot());
            systemVars.put("app.config", FilePath.getConfigurationPath()); 
            systemVars.putAll(System.getProperties());  
            systemVars.putAll(System.getenv());
        }
        return systemVars;
    }
    public static String resolveContextVars(String in, Map<?,?> vMap) {
        return replaceKeys(in, CONTEXT_VARS, true, 1, vMap);
    }

    public static String resolveEnvVars(String in) {
        return replaceKeys(in, ENV_VARS);
    }

    /**
     * replace the given pattern with the key-map value
     *
     * @param in input string
     * @param pattern pattern to match
     * @param preserveKeys true to preserve key pattern if its not in key-map
     * @param passes no times to resolve
     * <br> n for n- level of keys (level -> keys inside keys)
     * @param maps key-map list
     * @return resolved string
     */
    public static String replaceKeys(String in, Pattern pattern, boolean preserveKeys, int passes, Map<?,?>... maps) {
        String out = in;
        for (int pass = 1; pass <= passes; pass++) {
            Matcher m = pattern.matcher(in);
            String match, key;
            while (m.find()) {
                match = m.group();
                key = m.group(1);
                Boolean resolved = false;
                if (maps != null) {
                    for (Map<?, ?> map : maps) {
                        if ((resolved = map.containsKey(key))) {
                            out = out.replace(match, Objects.toString(map.get(key)));
                            break;
                        }
                    }
                }
                if (!resolved && !preserveKeys) {
                    out = out.replace(match, key);
                }
            }
            in=out;
        }
        return out;
    }

    /**
     *
     * @param in input string
     * @param p pattern to match
     * @return resolved string
     */
    public static String replaceKeys(String in, Pattern p) {
        return replaceKeys(in, p, false, 1, System.getProperties(), System.getenv());
    }

    public static String resolveSystemVars(String in) {
       return replaceKeys(in, CONTEXT_VARS, true, 1, getSystemVars());
    }

}
