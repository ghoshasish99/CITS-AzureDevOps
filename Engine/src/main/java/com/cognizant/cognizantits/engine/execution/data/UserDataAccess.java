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

import com.cognizant.cognizantits.datalib.testdata.view.TestDataView;
import com.cognizant.cognizantits.engine.execution.run.TestCaseRunner;

/**
 *
 *
 */
public abstract class UserDataAccess {

    abstract public TestCaseRunner context();

    public String getCurrentScenario() {
        return context().scenario();
    }

    public String getCurrentTestCase() {
        return context().testcase();
    }

    public String getScenario() {
        return context().getRoot().scenario();
    }

    public String getTestCase() {
        return context().getRoot().testcase();
    }

    public String getIteration() {
        return context().iteration();
    }

    public String getTestCaseSubIteration() {
        return context().subIteration();
    }

    public String getSubIteration() {
        return context().getCurrentSubIteration();
    }

    public int getSubIterationAsNumber() {
        return Integer.valueOf(getSubIteration());
    }

    public String getGlobalData(String globalDataID, String columnName) {
        return DataAccess.getGlobalData(this.context(), globalDataID, columnName);
    }

    public void putGlobalData(String globalDataID, String columnName, String value) {
        DataAccess.putGlobalData(this.context(), globalDataID, columnName, value);
    }

    public String getData(String Sheet, String Column) {
        return DataAccess.getData(context(), Sheet, Column, getIteration(), getSubIteration());
    }

    public String getData(String Sheet, String Column, String Iteration, String SubIteration) {
        return DataAccess.getData(context(), Sheet, Column, Iteration, SubIteration);
    }

    public String getData(String sheet, String column, String scenario, String testcase, String iteration,
            String subiteration) {
        return DataAccess.getData(context(), sheet, column, scenario, testcase, iteration, subiteration);
    }

    public void putData(String sheet, String column, String value) {
        putData(sheet, column, value, getIteration(), getSubIteration());
    }

    public void putData(String sheet, String column, String value, String iteration, String subIteration) {
        DataAccess.putData(context(), sheet, column, value, iteration, subIteration);
    }

    public void putData(String sheet, String column, String value, String scenario, String testcase, String iteration,
            String subIteration) {
        DataAccess.putData(context(), sheet, column, value, scenario, testcase, iteration, subIteration);
    }

    public TestDataView getTestData(String sheetName) {
        return DataAccess.getTestData(context(), sheetName).withSubIter(context().scenario(), context().testcase(),
                context().iteration(), context().subIteration());
    }

}
