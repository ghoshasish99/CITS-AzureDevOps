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

import com.cognizant.cognizantits.engine.constants.SystemDefaults;
import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.execution.exception.ForcedException;
import com.cognizant.cognizantits.engine.execution.exception.element.ElementException;
import com.cognizant.cognizantits.engine.execution.exception.element.ElementException.ExceptionType;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class Basic extends General {

    public Basic(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Click the [<Object>] ")
    public void Click() {
        if (elementEnabled()) {
            Element.click();
            Report.updateTestLog(Action, "Clicking on " + ObjectName, Status.DONE);
        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Click the [<Object>] if it exists")
    public void ClickIfExists() {
        if (Element != null) {
            Click();
        } else {
            Report.updateTestLog(Action, "Element [" + ObjectName + "] not Exists", Status.DONE);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Click the [<Object>] if it is displayed")
    public void ClickIfVisible() {
        if (Element != null) {
            if (Element.isDisplayed()) {
                Click();
            } else {
                Report.updateTestLog(Action, "Element [" + ObjectName + "] not Visible", Status.DONE);
            }
        } else {
            Report.updateTestLog(Action, "Element [" + ObjectName + "] not Exists", Status.DONE);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Submit action on the browser")
    public void Submit() {
        if (elementEnabled()) {
            Element.submit();
            Report.updateTestLog(Action, "[" + ObjectName + "] Submitted successfully ", Status.DONE);

        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Submit the [<Object>] if it exists")
    public void SubmitIfExists() {
        if (Element != null) {
            Submit();
        } else {
            Report.updateTestLog(Action, "Element [" + ObjectName + "] not Exists", Status.DONE);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Enter the value [<Data>] in the Field [<Object>]", input = InputType.YES)
    public void Set() {
        if (elementEnabled()) {
            Element.clear();
            Element.sendKeys(Data);
            Report.updateTestLog(Action, "Entered Text '" + Data + "' on '"
                    + ObjectName + "'", Status.DONE);
        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Enter the value [<Data>] in the [<Object>] if it exists", input = InputType.YES)
    public void SetIfExists() {
        if (Element != null) {
            Set();
        } else {
            Report.updateTestLog(Action, "Element [" + ObjectName + "] not Exists", Status.DONE);
        }
    }

    /**
     * **********************************************
     */
    @Action(object = ObjectType.SELENIUM, desc = "Enter the value [<Data>] in the [<Object>] if Data exists", input = InputType.YES)
    public void SetIfDataPresentByJS() {
        if (!Data.isEmpty()) {
            if (elementPresent()) {
                try {

                    JavascriptExecutor js = (JavascriptExecutor) Driver;
                    js.executeScript("arguments[0].value='" + Data + "'", Element);
                    Report.updateTestLog(Action, "Entered Text '" + Data + "' on '" + ObjectName + "'", Status.DONE);
                } catch (Exception ex) {
                    Logger.getLogger(JSCommands.class.getName()).log(Level.SEVERE, null, ex);
                    Report.updateTestLog(Action, "Couldn't set value on " + ObjectName + " - Exception " + ex.getMessage(),
                            Status.FAIL);
                }
            } else {
                throw new ElementException(ElementException.ExceptionType.Element_Not_Found, ObjectName);
            }
        } else {
            Report.updateTestLog(Action, "Data does not exist", Status.DONE);
        }
    }

    /**
     * ***********************************************
     */
    @Action(object = ObjectType.SELENIUM, desc = "Enter the value [<Data>] in the [<Object>] if Data exists", input = InputType.YES)
    public void SetIfDataPresent() {
        if (!Data.isEmpty()) {
            if (Element != null) {
                Set();
            } else {
                Report.updateTestLog(Action, "Element [" + ObjectName + "] not Exists", Status.DONE);
            }
        } else {
            Report.updateTestLog(Action, "Data does not exist", Status.DONE);
        }
    }

    /**
     * ***********************************************
     */
    @Action(object = ObjectType.SELENIUM, desc = "Enter the value [<Data>] in the Field [<Object>] and check [<Data>] matches with [<Object>] value", input = InputType.YES)
    public void SetAndCheck() {
        if (elementEnabled()) {
            Element.clear();
            Element.sendKeys(Data);
            if (Element.getAttribute("value").equals(Data)) {
                Report.updateTestLog("Set", "Entered Text '" + Data + "' on '"
                        + ObjectName + "'", Status.DONE);
            } else {
                Report.updateTestLog("Set", "Unable Enter Text '" + Data
                        + "' on '" + ObjectName + "'", Status.FAIL);
            }
        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Clear text [<Data>] from object [<Object>].")
    public void clear() {
        if (elementEnabled()) {
            Element.clear();
            Report.updateTestLog("Clear", "Cleared Text on '" + ObjectName + "'", Status.DONE);
        } else {
            throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Enter the Decrypted value [<Data>] in the Field [<Object>]", input = InputType.YES)
    public void setEncrypted() {
        if (Data != null && Data.matches(".* Enc")) {
            if (elementEnabled()) {
                try {
                    Element.clear();
                    Data = Data.substring(0, Data.lastIndexOf(" Enc"));
                    byte[] valueDecoded = Base64.decodeBase64(Data);
                    Element.sendKeys(new String(valueDecoded));
                    Report.updateTestLog(Action, "Entered Encrypted Text " + Data + " on " + ObjectName, Status.DONE);
                } catch (Exception ex) {
                    Report.updateTestLog("setEncrypted", ex.getMessage(), Status.FAIL);
                    Logger.getLogger(Basic.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                throw new ElementException(ExceptionType.Element_Not_Enabled, ObjectName);
            }
        } else {
            Report.updateTestLog(Action, "Data not encrypted '" + Data + "'", Status.DEBUG);
        }
    }

    @Action(object = ObjectType.SELENIUM,
            desc = "Move the Browser View to the specified element [<Object>]")
    public void moveTo() {
        if (elementDisplayed()) {
            if (Data != null && Data.matches("(\\d)+,(\\d)+")) {
                int x = Integer.valueOf(Data.split(",")[0]);
                int y = Integer.valueOf(Data.split(",")[1]);
                new Actions(Driver).moveToElement(Element, x, y).build().perform();
            } else {
                new Actions(Driver).moveToElement(Element).build().perform();
            }
            Report.updateTestLog(Action, "Viewport moved to" + ObjectName, Status.DONE);
        } else {
            throw new ElementException(ExceptionType.Element_Not_Visible, ObjectName);
        }
    }

    @Action(object = ObjectType.BROWSER, desc = "This a dummy function helpful with testing.")
    public void filler() {

    }

    @Action(object = ObjectType.BROWSER,
            desc = "Open the Url [<Data>] in the Browser",
            input = InputType.YES)
    public void Open() {
        Boolean pageTimeOut = false;
        try {
            if (Condition.matches("[0-9]+")) {
                setPageTimeOut(Integer.valueOf(Condition));
                pageTimeOut = true;
            }
            Driver.get(Data);
            Report.updateTestLog("Open", "Opened Url: " + Data, Status.DONE);
        } catch (TimeoutException e) {
            Report.updateTestLog("Open",
                    "Opened Url: " + Data + " and cancelled page load after " + Condition + " seconds",
                    Status.DONE);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, e);
            Report.updateTestLog("Open", e.getMessage(), Status.FAIL);
            throw new ForcedException("Open", e.getMessage());
        }
        if (pageTimeOut) {
            setPageTimeOut(300);
        }
    }

    private void setPageTimeOut(int sec) {
        try {
            Driver.manage().timeouts().pageLoadTimeout(sec, TimeUnit.SECONDS);
        } catch (Exception ex) {
            System.out.println("Couldn't set PageTimeOut to " + sec);
        }
    }

    @Action(object = ObjectType.BROWSER, desc = "Start a specified browser", input = InputType.YES)
    public void StartBrowser() {
        try {
            getDriverControl().StartBrowser(Data);
            Report.setDriver(getDriverControl());
            Report.updateTestLog("StartBrowser", "Browser Started: " + Data,
                    Status.DONE);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, e);
            Report.updateTestLog("StartBrowser", "Error: " + e.getMessage(),
                    Status.FAIL);
        }

    }

    @Action(object = ObjectType.BROWSER, desc = "Restarts the Browser")
    public void RestartBrowser() {
        try {
            getDriverControl().RestartBrowser();
            Report.setDriver(getDriverControl());
            Report.updateTestLog("RestartBrowser", "Restarted Browser",
                    Status.DONE);
        } catch (Exception ex) {
            Report.updateTestLog("RestartBrowser", "Unable Restart Browser",
                    Status.FAIL);
            Logger.getLogger(Basic.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Action(object = ObjectType.BROWSER, desc = "Stop the current browser")

    public void StopBrowser() {
        getDriverControl().StopBrowser();
        Report.updateTestLog("StopBrowser", "Browser Stopped: ", Status.DONE);
    }

    @Action(object = ObjectType.BROWSER, desc = "Add a variable to access within testcase", input = InputType.YES, condition = InputType.YES)
    public void AddVar() {
        addVar(Condition, Data);
        if (getVar(Condition) != null) {
            Report.updateTestLog("addVar", "Variable " + Condition
                    + " added with value " + Data, Status.DONE);
        } else {
            Report.updateTestLog("addVar", "Variable " + Condition
                    + " not added ", Status.DEBUG);
        }
    }

    @Action(object = ObjectType.BROWSER, desc = "Add a Global variable to access across test set", input = InputType.YES, condition = InputType.YES)
    public void AddGlobalVar() {
        addGlobalVar(Condition, Data);
        if (getVar(Condition) != null) {
            Report.updateTestLog(Action, "Variable " + Condition
                    + " added with value " + Data, Status.DONE);
        } else {
            Report.updateTestLog(Action, "Variable " + Condition
                    + " not added ", Status.DEBUG);
        }
    }

    @Action(object = ObjectType.BROWSER, desc = "changing wait time by [<Data>] seconds", input = InputType.YES)
    public void changeWaitTime() {
        try {
            int t = Integer.parseInt(Data);
            if (t > 0) {
                SystemDefaults.waitTime.set(t);
                Report.updateTestLog("changeWaitTime", "Wait time changed to "
                        + t + " second/s", Status.DONE);
            } else {
                Report.updateTestLog("changeWaitTime",
                        "Couldn't change Wait time (invalid input)",
                        Status.DEBUG);
            }

        } catch (NumberFormatException ex) {
            Report.updateTestLog("changeWaitTime",
                    "Couldn't change Wait time ", Status.DEBUG);
            Logger.getLogger(Basic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Action(object = ObjectType.BROWSER,
            desc = "Change Default Element finding wait time by [<Data>] seconds",
            input = InputType.YES)
    public void setElementTimeOut() {
        if (Data != null && Data.matches("[0-9]+")) {
            SystemDefaults.elementWaitTime.set(Integer.valueOf(Data));
            Report.updateTestLog(Action, "Element Wait time changed to "
                    + Data + " second/s", Status.DONE);
        } else {
            Report.updateTestLog(Action,
                    "Couldn't change Element Wait time (invalid input) " + Data,
                    Status.DEBUG);
        }

    }

    @Action(object = ObjectType.BROWSER, desc = "Changes the browser size into [<Data>]", input = InputType.YES)
    public void setBrowserSize() {
        try {
            if (Data.matches("\\d*(x|,| )\\d*")) {
                String size = Data.replaceFirst("(x|,| )", " ");
                String[] sizes = size.split(" ", 2);
                Driver.manage().window().setSize(new Dimension(Integer.parseInt(sizes[0]), Integer.parseInt(sizes[1])));
                Report.updateTestLog(Action, " Browser is resized to " + Data,
                        Status.DONE);
            } else {
                Report.updateTestLog(Action, " Invalid Browser size [" + Data + "]",
                        Status.DEBUG);
            }
        } catch (Exception ex) {
            Report.updateTestLog(Action, "Unable to resize the Window ",
                    Status.FAIL);
            Logger.getLogger(Basic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Highlight the element [<Object>]", input = InputType.OPTIONAL)
    public void highlight() {
        if (elementDisplayed()) {
            if (Data != null && !Data.trim().isEmpty()) {
                highlightElement(Element, Data);
            } else {
                highlightElement(Element);
            }
            Report.updateTestLog(Action, "Element Highlighted",
                    Status.PASS);
        }
    }

    private void highlightElement(WebElement element, String color) {
        JavascriptExecutor js = (JavascriptExecutor) Driver;
        js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, " outline:" + color + " solid 2px;");
    }

    public void highlightElement(WebElement element) {
        highlightElement(element, "#f00");
    }

}
