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
package com.cognizant.cognizantits.engine.reporting.sync.tfs;

import java.io.File;
import java.util.List;

/**
 *
 * 
 */
public class VStsTestData {
    
    String project;
    int testPlanId;
    String suite;
    String testcase;
    String status;
    List<File> attach;

    public VStsTestData(String project, int testPlanId, String suite, String testcase, String status, List<File> attach){
        this.project = project;
        this.testPlanId = testPlanId;
        this.suite = suite;
        this.testcase = testcase;
        this.status = status;
        this.attach = attach;
    }
    
}
