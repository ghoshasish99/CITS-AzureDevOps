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

/**
 *
 * 
 */
@SuppressWarnings("serial")
public class GlobalDataNotFoundException extends DataNotFoundException {   
	
	public String gid;

    public GlobalDataNotFoundException(TestCaseRunner context,
            String gid, String field) {
        super("Global Data Not Found..");
        this.context = context;
        this.field = field;
        this.gid = gid;
    }

    @Override
    public String toString() {
        try {
            return getFormatted(getTemplate(context.isReusable()), getMessage(),
                    context.executor().runEnv(), field, gid,
                    context.getRoot().scenario(), context.getRoot().testcase(),
                    context.scenario(), context.testcase());
        } catch (Exception ex) {
            return super.toString();
        }
    }

    public static String getTemplate(Boolean isReusable) {
        return "{0} \n[Env : {1} | Field : {2} | GID : {3} | TestCase : {4}/{5}"
                + (isReusable ? " | Reusabe : {6}/{7} ]" : " ]");
    }

}
