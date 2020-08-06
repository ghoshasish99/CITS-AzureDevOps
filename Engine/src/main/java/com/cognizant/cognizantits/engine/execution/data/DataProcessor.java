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
package com.cognizant.cognizantits.engine.execution.data;

import com.cognizant.cognizantits.engine.constants.SystemDefaults;
import com.cognizant.cognizantits.engine.core.Control;
import com.cognizant.cognizantits.engine.core.RunManager;
import com.cognizant.cognizantits.engine.execution.exception.data.DataNotFoundException;
import com.cognizant.cognizantits.engine.execution.run.TestCaseRunner;
import com.cognizant.cognizantits.engine.util.data.KeyMap;
import com.cognizant.cognizantits.engine.util.data.fx.FParser;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 *
 */
public class DataProcessor {

    private final static Pattern RT_VAR = Pattern.compile("(%.+?%)");

    public static String trimFirst(String v) {
        if (v.length() > 1) {
            return v.substring(1);
        } else {
            return "";
        }
    }

    public static String resolve(Object raw) {
        String inp = resolveKeyMapVars(Objects.toString(raw, ""), 2);
        inp = resolveIn(inp);
        return resolveKeyMapVars(inp, 2);
    }

    public static synchronized String resolve(String raw, TestCaseRunner context,
            String subIter) throws DataNotFoundException {
        String inp = Objects.toString(raw, "");
        //resolveKeyMapVars(Objects.toString(raw, ""), 2, context.getControl().getRunTimeVars());
        if (inp.matches("(^@|=|>|%)(.*)") || inp.startsWith("<")|| inp.startsWith("{")) {
            inp = resolveDynamic(resolveIn(inp), context);
        } else if ((!inp.startsWith("<")||!inp.startsWith("{") ) && inp.contains(":")) {
            String[] args = inp.split(":");
            if (!context.isIterResolved(args[0])) {
                context.setIter(args[0], DataAccess.getIterations(context, args[0]));
            }
            inp = DataAccess.getData(context, args[0], args[1], context.iteration(), subIter);
        }
        return resolveKeyMapVars(inp, 2, context.getControl().getRunTimeVars());
    }

    private static String resolveIn(String inp) {
        if (inp.startsWith("@")) {
            inp = trimFirst(inp);
        } 
        
        else if (inp.startsWith("=")) {
            inp = Objects.toString(FParser.eval(trimFirst(inp)), "");
        } else if (inp.startsWith(">")) {
            inp = Objects.toString(FParser.evaljs(trimFirst(inp)), "");
        }
        return inp;
    }

    private static String resolveDynamic(String data, TestCaseRunner context) {
        Matcher matcher = RT_VAR.matcher(data);
        while (matcher.find()) {
            String var = matcher.group();
            String inp = context.getControl().getDynamicValue(var);
            if (inp != null) {
                System.out.println(String.format("%s changed to %s", var, inp));
                data = data.replace(var, inp);
            }
        }
        return data;
    }

    public static String resolve(Object raw,
            TestCaseRunner context, String field) throws DataNotFoundException {
        String inp = resolveKeyMapVars(Objects.toString(raw, ""), 2,
                context.getControl().getRunTimeVars());
        inp = resolveDynamic(resolveIn(inp), context);
        if (inp.startsWith("#")) {
            inp = DataAccess.getGlobalData(context, inp, field);
        }

        return resolveKeyMapVars(inp, 2, context.getControl().getRunTimeVars());
    }

    public static String resolveKeyMapVars(String inp, int pass, Map<String, String> runTimeVars) {
        inp = KeyMap.replaceKeys(inp, KeyMap.USER_VARS, true, pass, runTimeVars,
                Control.getCurrentProject().getProjectSettings().getUserDefinedSettings());
        inp = KeyMap.replaceKeys(inp, KeyMap.CONTEXT_VARS, true, pass,
                Control.exe.getExecSettings().getRunSettings(),
                Control.exe.getExecSettings().getTestMgmgtSettings(),
                Control.getCurrentProject().getProjectSettings().getTestMgmtModule().asMap(),
                RunManager.getGlobalSettings(),
                Control.getCurrentProject().getProjectSettings().getDriverSettings(),
                Control.getCurrentProject().getProjectSettings().getUserDefinedSettings(),
                SystemDefaults.EnvVars,
                SystemDefaults.CLVars);
        inp = KeyMap.resolveEnvVars(inp);
        return inp;
    }

    public static String resolveKeyMapVars(String inp, int pass) {
        return resolveKeyMapVars(inp, pass, new HashMap<>());
    }

}
