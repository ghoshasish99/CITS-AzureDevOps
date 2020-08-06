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
public class TestDataNotFoundException extends DataNotFoundException {
    
    
	public String sheet;

    public TestDataNotFoundException(TestCaseRunner context, String sheet, String field, Cause c, String info) {
        super(String.format("Test Data Not Found, %s - %s is missing.", c.name(), info));
        this.context = context;
        this.field = field;
        this.sheet = sheet;
        this.cause = new CauseInfo(c, info);
    }

    @Override
    public String toString() {
        try {
            return getFormatted(getTemplate(context.isReusable()), getMessage(), context.executor().runEnv(), sheet,
                    field, context.getRoot().scenario(), context.getRoot().testcase(), context.scenario(),
                    context.testcase());
        } catch (Exception ex) {
            return super.toString();
        }
    }

    public static String getTemplate(Boolean isReusable) {
        return "{0} \n[Env : {1} | Sheet : {2} | Field : {3} | TestCase : {4}/{5}"
                + (isReusable ? " | Reusabe : {6}/{7} ]" : " ]");
    }

}
