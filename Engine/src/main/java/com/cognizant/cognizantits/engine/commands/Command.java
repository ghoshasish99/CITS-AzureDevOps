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
package com.cognizant.cognizantits.engine.commands;

import com.cognizant.cognizantits.datalib.or.common.ObjectGroup;
import com.cognizant.cognizantits.datalib.or.image.ImageORObject;
import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.drivers.AutomationObject;
import com.cognizant.cognizantits.engine.drivers.SeleniumDriver;
import com.cognizant.cognizantits.engine.execution.data.UserDataAccess;
import com.cognizant.cognizantits.engine.reporting.TestCaseReport;
import java.util.Stack;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Command {

    public WebDriver Driver;
    public AutomationObject AObject;
    public String Data;
    public String ObjectName;
    public WebElement Element;
    public ObjectGroup<ImageORObject> imageObjectGroup;
    public String Description;
    public String Condition;
    public String Input;
    public String Action;
    public TestCaseReport Report;
    public String Reference;
    private final CommandControl Commander;
    public UserDataAccess userData;

    public Command(CommandControl cc) {
        Commander = cc;
        Driver = Commander.seDriver.driver;
        AObject = Commander.AObject;
        Data = Commander.Data;
        ObjectName = Commander.ObjectName;
        Element = Commander.Element;
        imageObjectGroup = Commander.imageObjectGroup;
        Description = Commander.Description;
        Condition = Commander.Condition;
        Input = Commander.Input;
        Report = Commander.Report;
        Reference = Commander.Reference;
        Action = Commander.Action;
        userData = Commander.userData;
    }

    public void addVar(String key, String val) {
        Commander.addVar(key, val);
    }

    public String getVar(String key) {
        return Commander.getVar(key);
    }

    public void addGlobalVar(String key, String val) {
        if (key.matches("%.*%")) {
            key = key.substring(1, key.length() - 1);
        }
        Commander.putUserDefinedData(key, val);
    }

    public String getUserDefinedData(String key) {
        return Commander.getUserDefinedData(key);
    }

    public String getDataBaseData(String key) {
        return Commander.getDataBaseProperty(key);
    }
    
 
    public Stack<WebElement> getRunTimeElement() {
        return Commander.getRunTimeElement();
    }

    public void executeMethod(String Action) {
        Commander.executeAction(Action);
    }

    public void executeMethod(WebElement element, String Action, String Input) {
        setElement(element);
        setInput(Input);
        executeMethod(Action);
    }

    public void executeMethod(String Action, String Input) {
        setInput(Input);
        executeMethod(Action);
    }

    public void executeMethod(WebElement element, String Action) {
        setElement(element);
        executeMethod(Action);
    }

    public SeleniumDriver getDriverControl() {
        return Commander.seDriver;
    }

    public Boolean isDriverAlive() {
        return getDriverControl().isAlive();
    }

    private void setElement(WebElement element) {
        Commander.Element = element;
    }

    private void setInput(String input) {
        Commander.Data = input;
    }

    public String getCurrentBrowserName() {
        return Commander.seDriver.getCurrentBrowser();
    }

    public CommandControl getCommander() {
        return Commander;
    }

    public void executeTestCase(String scenarioName, String testCaseName, int subIteration) {
        Commander.execute(scenarioName + ":" + testCaseName, subIteration);
    }

    public void executeTestCase(String scenarioName, String testCaseName) {
        executeTestCase(scenarioName, testCaseName, userData.getSubIterationAsNumber());
    }

    public boolean browserAction() {
        return "browser".equalsIgnoreCase(ObjectName);
    }
}
