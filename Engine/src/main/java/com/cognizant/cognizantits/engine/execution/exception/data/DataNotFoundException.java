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
package com.cognizant.cognizantits.engine.execution.exception.data;

import com.cognizant.cognizantits.engine.execution.run.TestCaseRunner;
import java.text.MessageFormat;

/**
 *
 * 
 */
public class DataNotFoundException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -8606941865608503468L;
    public TestCaseRunner context;
    public String field;
    public CauseInfo cause;

    protected DataNotFoundException(String name) {
        super(name);
    }
    
    public static String getFormatted(String template, Object... args) {
        return MessageFormat.format(template, args);
    }

    public static String getTemplate(Boolean isReusable) {
        return "{0} \n[Env : {1} | Field : {2} | TestCase : {4}/{5}"
                + (isReusable ? " | Reusabe : {6}/{7} ]" : " ]");
    }

    public enum Cause {
        Data, Iteration, SubIteration
    }

    public class CauseInfo {

        public Cause type;
        public String info;

        public CauseInfo(Cause c, String info) {
            this.type = c;
            this.info = info;
        }

        public boolean isIter() {
            return type == Cause.Iteration;
        }

        public boolean isSubIter() {
            return type == Cause.SubIteration;
        }

    }

}
