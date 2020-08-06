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
package com.cognizant.cognizantits.engine.reporting.sync.qc.rest.util;

/**
 * QC GET query-param templates
 *
 * 
 *
 */
public class Q {
    /**
     * query-param key
     */
    public static final String QUERY = "query";
    /**
     * query with name
     */
    public static final String NAME = "{name['%s']}";
    /**
     * query with name and parent id
     */
    public static final String NAME_PARENTID = "{name['%s'];parent-id[%s]}";
    /**
     * query with cycle id
     */
    public static final String TESTSET_OR_CYCLE_ID = "{cycle-id[%s]}";
    
}
