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

/**
 *
 * 
 */
public class AssertElement extends General {

    public AssertElement(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] is not present")
    public void assertElementNotPresent() {
        assertNotElement(!elementPresent());
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] is not selected")
    public void assertElementNotSelected() {
        assertNotElement(!elementSelected());
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] is not displayed")
    public void assertElementNotDisplayed() {
        assertNotElement(!elementDisplayed());
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] is not enabled")
    public void assertElementNotEnabled() {
        assertNotElement(!elementEnabled());
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] is present")
    public void assertElementPresent() {
        assertElement(elementPresent());
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] element is selected")
    public void assertElementSelected() {
        assertElement(elementSelected());
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] element is displayed")
    public void assertElementDisplayed() {
        assertElement(elementDisplayed());
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] is enabled on the current page")
    		
    public void assertElementEnabled() {
        assertElement(elementEnabled());
    }

    private void assertElement(Boolean status, String isNot) {
        String value = isNot + Action.replaceFirst("assertElement", "").replaceFirst("Not", "");
        String description = String.format("Element [%s] is %s", ObjectName, value);
        if (status) {
            Report.updateTestLog(Action, description, Status.PASS);
        } else {
            throw new ForcedException(Action, description);
        }
    }

    private void assertElement(Boolean status) {
        assertElement(status, status ? "" : "not ");
    }

    private void assertNotElement(Boolean status) {
        assertElement(status, status ? "not " : "");
    }

    /**
     * Function to assert the complete page source of the current page
     *
     *
     */
    @Action(object = ObjectType.BROWSER, 
    		desc ="Assert if Page source of current page is: [<Data>]", 
    		input =InputType.YES)
    public void assertPageSource() {
        if (Driver.getPageSource().equals(Data)) {
            Report.updateTestLog(
                    Action,
                    "Current Page Source is matched with the expected Page Source",
                    Status.DONE);
        } else {
            throw new ForcedException(Action,
                    "Current Page Source doesn't match with the expected Page Source");
        }
    }

    @Action(object = ObjectType.BROWSER, 
    		desc ="Assert if the Horizontal Scrollbar is present")
    public void assertHScrollBarPresent() {
        assertHScorllBar("", isHScrollBarPresent());
    }

    @Action(object = ObjectType.BROWSER, 
    		desc ="Assert if the Horizontal Scrollbar is not present")
    public void assertHScrollBarNotPresent() {
        assertHScorllBar("not", !isHScrollBarPresent());
    }

    @Action(object = ObjectType.BROWSER, desc ="Assert if the Vertical Scrollbar is present")
    public void assertVScrollBarPresent() {
        assertVScorllBar("", isvScrollBarPresent());
    }

    @Action(object = ObjectType.BROWSER, desc ="Assert if the Vertical Scrollbar is not present")
    public void assertVScrollBarNotPresent() {
        assertVScorllBar("not", !isvScrollBarPresent());
    }

    private void assertHScorllBar(String isNot, Boolean value) {
        assertScorllBar("Horizontal", isNot, value);
    }

    private void assertVScorllBar(String isNot, Boolean value) {
        assertScorllBar("Vertical", isNot, value);
    }

    private void assertScorllBar(String type, String isNot, Boolean value) {
        String desc = type + " Scrollbar is " + isNot + " present";
        if (value) {
            Report.updateTestLog(Action, desc, Status.PASS);
        } else {
            throw new ForcedException(Action, desc);
        }
    }

}
