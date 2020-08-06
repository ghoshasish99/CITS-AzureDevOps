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
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;

/**
 *
 * 
 */
public class VerifyElement extends General {

    public VerifyElement(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Verify if [<Object>] element is not present")
    public void verifyElementNotPresent() {
        verifyNotElement(!elementPresent());
    }

    @Action(object = ObjectType.SELENIUM, desc = "Verify if [<Object>] element is not selected")
    public void verifyElementNotSelected() {
        verifyNotElement(!elementSelected());
    }

    @Action(object = ObjectType.SELENIUM, desc = "Verify if [<Object>] element is not displayed")
    public void verifyElementNotDisplayed() {
        verifyNotElement(!elementDisplayed());
    }

    @Action(object = ObjectType.SELENIUM, desc = "Verify if [<Object>] element is not enabled")
    public void verifyElementNotEnabled() {
        verifyNotElement(!elementEnabled());
    }

    @Action(object = ObjectType.SELENIUM, desc = "Verify if [<Object>] element is present")
    public void verifyElementPresent() {
        verifyElement(elementPresent());
    }

    @Action(object = ObjectType.SELENIUM, desc = "Verify if [<Object>] element is selected")
    public void verifyElementSelected() {
        verifyElement(elementSelected());
    }

    @Action(object = ObjectType.SELENIUM, desc = "Verify if [<Object>] element is displayed")
    public void verifyElementDisplayed() {
        verifyElement(elementDisplayed());
    }

    @Action(object = ObjectType.SELENIUM, desc = "Verify if [<Object>] element is enabled")
    public void verifyElementEnabled() {
        verifyElement(elementEnabled());
    }

    private void verifyElement(Boolean status, String isNot) {
        String value = isNot + Action.replaceFirst("verifyElement", "").replaceFirst("Not", "");
        String description = String.format("Element [%s] is %s", ObjectName, value);
        Report.updateTestLog(Action, description, Status.getValue(status));
    }

    private void verifyElement(Boolean status) {
        verifyElement(status, status ? "" : "not ");
    }

    private void verifyNotElement(Boolean status) {
        verifyElement(status, status ? "not " : "");
    }

    @Action(object = ObjectType.BROWSER, desc = "Verify if Page source of current page is: [<Data>]", input = InputType.YES)
    public void verifyPageSource() {
        boolean value = Driver.getPageSource().equals(Data);
        Report.updateTestLog(
                Action,
                "Current Page Source is" + (value ? "" : " not") + " matched with the expected Page Source",
                Status.getValue(value));
    }

    @Action(object = ObjectType.BROWSER, desc = "Verify if the HScrollBar is present")
    public void verifyHScrollBarPresent() {
        verifyHScorllBar("", isHScrollBarPresent());
    }

    @Action(object = ObjectType.BROWSER, desc = "Verify if the HScrollBar is not present")
    public void verifyHScrollBarNotPresent() {
        verifyHScorllBar("not", !isHScrollBarPresent());
    }

    @Action(object = ObjectType.BROWSER, desc = "Verify if the VScrollBar is present")
    public void verifyVScrollBarPresent() {
        verifyVScorllBar("", isvScrollBarPresent());
    }

    @Action(object = ObjectType.BROWSER, desc = "Verify if the VScrollBar is not present")
    public void verifyVScrollBarNotPresent() {
        verifyVScorllBar("not", !isvScrollBarPresent());
    }

    private void verifyHScorllBar(String isNot, Boolean value) {
        verifyScorllBar("Horizontal", isNot, value);
    }

    private void verifyVScorllBar(String isNot, Boolean value) {
        verifyScorllBar("Vertical", isNot, value);
    }

    private void verifyScorllBar(String type, String isNot, Boolean value) {
        String desc = type + " Scrollbar is " + isNot + " present";
        Report.updateTestLog(Action, desc, Status.getValue(value));
    }
}
