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
package com.cognizant.cognizantits.engine.core;

import com.cognizant.cognizantits.datalib.or.common.ObjectGroup;
import com.cognizant.cognizantits.datalib.or.image.ImageORObject;
import com.cognizant.cognizantits.engine.drivers.AutomationObject;
import com.cognizant.cognizantits.engine.drivers.AutomationObject.FindType;
import com.cognizant.cognizantits.engine.drivers.SeleniumDriver;
import com.cognizant.cognizantits.engine.execution.data.DataProcessor;
import com.cognizant.cognizantits.engine.execution.data.UserDataAccess;
import com.cognizant.cognizantits.engine.execution.exception.UnCaughtException;
import com.cognizant.cognizantits.engine.execution.run.TestCaseRunner;
import com.cognizant.cognizantits.engine.reporting.TestCaseReport;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.Step;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.openqa.selenium.WebElement;

public abstract class CommandControl {

    public SeleniumDriver seDriver;
    public AutomationObject AObject;
    public String Data;
    public String Action;
    public String ObjectName;
    public String Reference;
    public WebElement Element;

    public ObjectGroup<ImageORObject> imageObjectGroup;

    public String Condition;
    public String Description;
    public String Input;
    public TestCaseReport Report;
    public UserDataAccess userData;
    private HashMap<String, String> runTimeVars = new HashMap<>();
    private Stack<WebElement> runTimeElement = new Stack<>();

    public CommandControl(SeleniumDriver driver, TestCaseReport report) {
        seDriver = driver;
        userData = new UserDataAccess() {
            @Override
            public TestCaseRunner context() {
                return (TestCaseRunner) CommandControl.this.context();
            }
        };
        AObject = new AutomationObject(seDriver.driver);
        Report = (TestCaseReport) report;

    }

    public void refresh() {
        Data = ObjectName = Condition = Description = Input = Reference = Action = "";
        Element = null;
        imageObjectGroup = null;
    }

    public void sync(Step curr) throws UnCaughtException {
        refresh();
        AObject.setDriver(seDriver.driver);
        this.Description = curr.Description;
        this.Action = curr.Action;
        this.Input = curr.Input;
        this.Data = curr.Data;

        if (curr.Condition != null && curr.Condition.length() > 0) {
            this.Condition = curr.Condition;
        }

        if (curr.ObjectName != null && curr.ObjectName.length() > 0) {
            this.ObjectName = curr.ObjectName.trim();

            if (!(ObjectName.matches("(?i:app|browser|execute|executeclass)"))) {
                this.Reference = curr.Reference;
                if (!curr.Action.startsWith("img")) {
                    if (canIFindElement()) {
                        Element = AObject.findElement(ObjectName, Reference, FindType.fromString(Condition));
                    }
                } else {
                    imageObjectGroup = AObject.getImageObjects(Reference, ObjectName);
                }
            }
        }
    }

    private Boolean canIFindElement() {
        if (seDriver.isAlive()) {
            switch (Action) {
                case "waitForElementToBePresent":
                case "setObjectProperty":
                case "clickInputByLabel":
                    return false;
                default:
                    return true;
            }
        }
        return false;
    }

    abstract public void execute(String com, int sub);

    abstract public void executeAction(String Action);

    abstract public Object context();

    public void addVar(String key, String val) {

        if (runTimeVars.containsKey(key)) {
            System.err.println("runTimeVars already contains " + key + ".Forcing change to " + val);
            System.out.println("Already contains " + key);

        }
        System.out.println("Adding to runTimeVars " + key + ":" + val);
        runTimeVars.put(key, val);

    }

    public String getVar(String key) {

        System.out.println("Getting runTimeVar " + key);
        String val = getDynamicValue(key);
        if (val == null) {
            System.err.println("runTimeVars does not contain " + key + ".Returning Empty");
            Report.updateTestLog("Get Var", "Getting From runTimeVars " + key + " Failed", Status.WARNING);
            return "";
        } else {
            return val;
        }

    }

    public String getDynamicValue(String key) {
        if (!runTimeVars.containsKey(key)) {
            key = key.matches("\\%(\\S)+\\%") ? key.substring(1, key.length() - 1) : key;
            return getUserDefinedData(key);
        }
        return runTimeVars.get(key);
    }

    public String getUserDefinedData(String key) {
        return Control.getCurrentProject().getProjectSettings().getUserDefinedSettings().getProperty(key);
    }

    public void putUserDefinedData(String key, String value) {
        Control.getCurrentProject().getProjectSettings().getUserDefinedSettings().put(key, value);
    }

    public Stack<WebElement> getRunTimeElement() {
        return runTimeElement;
    }

    public void sync(Step curr, String subIter) throws Exception {
        curr.Data = DataProcessor.resolve(curr.Input, (TestCaseRunner) context(), subIter);
        sync(curr);
    }

    public Map<String, String> getRunTimeVars() {
        return runTimeVars;
    }

    public String getDataBaseProperty(String key) {
        return Control.getCurrentProject().getProjectSettings().getDatabaseSettings().getProperty(key);
    }
    
}
