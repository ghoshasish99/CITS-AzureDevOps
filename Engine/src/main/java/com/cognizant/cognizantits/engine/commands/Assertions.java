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

import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.execution.exception.ForcedException;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

public class Assertions extends General {

    public Assertions(CommandControl cc) {
        super(cc);
    }

    /**
     * ******************************************
     * Function to assert if a given Text is Present in the WebPage
     * ******************************************
     */
    @Action(object = ObjectType.BROWSER,
            desc = "Assert if text: [<Data>] is present on the page",
            input = InputType.YES)
    public void assertTextPresentInPage() throws RuntimeException {

        try {
            String strObj = Data;
            if (Driver.findElement(By.tagName("html")).getText()
                    .contains(strObj)) {
                System.out.println("assertTextPresent passed");
                Report.updateTestLog("assertTextPresentInPage",
                        "Expected text '" + strObj
                        + "' is  present in the page", Status.PASS);

            } else {
                System.out.println("assertTextPresentInPage failed");
                throw new Exception("Expected text  '" + strObj
                        + "' is not present in the page");
            }

        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, e);
            throw new ForcedException("assertTextPresentInPage", e.getMessage());
        }
    }

    /**
     * ******************************************
     * Function to assert the variable
     * ******************************************
     */
    @Action(object = ObjectType.BROWSER,
            desc = "Assert if Key:Value -> [<Data>] is valid",
            input = InputType.YES)
    public void assertVariable() throws RuntimeException {
        try {
            String strObj = Data;
            String[] strTemp = strObj.split("=", 2);
            String strAns = strTemp[0].matches("%.+%") ? getVar(strTemp[0]) : strTemp[0];
            if (strAns.equals(strTemp[1])) {
                System.out.println("Condition '" + Input + "' is true ");
                Report.updateTestLog("assertVariable",
                        "Variable matched with Provided data", Status.PASS);

            } else {
                 System.out.println("Condition '" + Input + "' is false ");
                throw new Exception("Variable did not match with provided data");
            }
        } catch (Exception ex) {
            Logger.getLogger(Assertions.class.getName()).log(Level.SEVERE, null, ex);
            throw new ForcedException("assertVariable", ex.getMessage());
        }
    }

    /**
     * ******************************************
     * Function to assert cookies present
     *
     * ******************************************
     */
    @Action(object = ObjectType.BROWSER, desc = "Assert if cookie name: [<Data>] is present", input = InputType.YES)
    public void assertCookiePresent() {
        try {
            String strCookieName = Data;
            if ((Driver.manage().getCookieNamed(strCookieName) != null)) {
                System.out.println("assertCookiePresent Passed");
                Report.updateTestLog("assertCookiePresent",
                        "Cookie name matched with the data provided",
                        Status.PASS);
            } else {
                throw new Exception(
                        "Cookie name did not match with data provided");
            }
        } catch (Exception ex) {
            System.out.println("assertCookiePresent Failed");
            Logger.getLogger(Assertions.class.getName()).log(Level.SEVERE, null, ex);
            throw new ForcedException("assertCookiePresent", ex.getMessage());
        }
    }

    /**
     * ******************************************
     * Function to assert cookies by name
     * ******************************************
     */
    @Action(object = ObjectType.BROWSER, desc = "Assert if cookie: [<Object>] has name: [<Data>]", input = InputType.YES)
    public void assertCookieByName() {
        try {

            String strCookieName = Data.split(":", 2)[0];
            String strCookieValue = Data.split(":", 2)[1];
            if (Driver.manage().getCookieNamed(strCookieName) != null) {
                if ((Driver.manage().getCookieNamed(strCookieName).getValue()
                        .equals(strCookieValue))) {
                    System.out.println("assertCookieByName Passed");
                    Report.updateTestLog("assertCookieByName",
                            "Cookie name matched with provided data",
                            Status.PASS);

                } else {
                    throw new Exception(
                            "Cookie value did not match with provided data");
                }
            } else {
                throw new Exception("Cookie  with the name '" + strCookieName
                        + "' did not exist");
            }
        } catch (Exception ex) {
            System.out.println("assertCookieByName Failed");
            Logger.getLogger(Assertions.class.getName()).log(Level.SEVERE, null, ex);
            throw new ForcedException("assertCookieByName", ex.getMessage());
        }
    }

    /**
     * ******************************************
     * Function to assert AlertText ******************************************
     */
    @Action(object = ObjectType.BROWSER, desc = "Assert if an alert with text: [<Data>] is present", input = InputType.YES)
    public void assertAlertText() {

        try {
            String strExpAlertText = Data;
            if (isAlertPresent()) {
                if ((Driver.switchTo().alert().getText()
                        .equals(strExpAlertText))) {
                    System.out.println("assertAlertText Passed");
                    Report.updateTestLog("assertAlertText",
                            "Alert text matched with provided data",
                            Status.PASS);
                } else {
                    throw new Exception(
                            "Alert text did not match with the provided data");
                }
            } else {
                throw new Exception("Alert not present");
            }

        } catch (Exception ex) {
            System.out.println("assertAlertText Failed");
            Logger.getLogger(Assertions.class.getName()).log(Level.SEVERE, null, ex);
            throw new ForcedException("assertAlertText", ex.getMessage());
        }
    }

    /**
     * ******************************************
     * Function to assert AlertTextPresent
     * ******************************************
     */
    @Action(object = ObjectType.BROWSER, desc = "Assert if an alert is present ")
    public void assertAlertPresent() {
        try {
            if ((isAlertPresent())) {
                System.out.println("assertAIertPresent Passed");
                Report.updateTestLog("assertAIertPresent", "Alert present",
                        Status.PASS);
            } else {
                throw new Exception("Alert not present");
            }
        } catch (Exception ex) {
            System.out.println("assertAIertPresent Failed");
            Logger.getLogger(Assertions.class.getName()).log(Level.SEVERE, null, ex);
            throw new ForcedException("assertAIertPresent", ex.getMessage());
        }
    }

    /**
     * ******************************************
     * Function to assert to evaluate JS expression
     * ******************************************
     */
    @Action(object = ObjectType.BROWSER,
            desc = "Assert if the evaluated javascript expression equals [<Data>]",
            input = InputType.YES)
    public void assertEval() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) Driver;

            String strExpScript = Data.split(":", 2)[0];
            String strExpValue = Data.split(":", 2)[1];
            Object result = js.executeScript(strExpScript);
            if (result != null && result.toString().trim().equals(strExpValue)) {
                System.out.println("assertEval Passed");
                Report.updateTestLog(
                        "assertEval",
                        "JS script return value matched with the expected result",
                        Status.DONE);
            } else {
                throw new Exception(
                        "JS script return value did not match with the expected result");
            }
        } catch (Exception ex) {
            System.out.println("assertEval Failed");
            Logger.getLogger(Assertions.class.getName()).log(Level.SEVERE, null, ex);
            throw new ForcedException("assertEval", ex.getMessage());
        }
    }

    /**
     * ******************************************
     * Function to assert the variable with the value from DataSheet
     * *****************************************
     */
    @Action(object = ObjectType.BROWSER,
            desc = "Assert if  the  variable value matches with given value from datasheet(variable:datasheet->  [<Data>] )",
            input = InputType.YES,
            condition = InputType.YES)
    public void assertVariableFromDataSheet() throws RuntimeException {
        try {
            String strAns = getVar(Condition);
            if (strAns.equals(Data)) {
                System.out.println("Variable " + Condition + " equals "
                        + Input);
                Report.updateTestLog(Action,
                        "Variable is matched with the expected result", Status.DONE);

            } else {
                System.out.println("Variable " + Condition + " is not equal "
                        + Input);
                throw new ForcedException(Action,
                        "Variable did not match with provided data");
            }
        } catch (Exception e) {
            Logger.getLogger(Assertions.class.getName()).log(Level.SEVERE, null, e);
            throw new ForcedException("assertVariableFromDataSheet", e.getMessage());
        }
    }

}
