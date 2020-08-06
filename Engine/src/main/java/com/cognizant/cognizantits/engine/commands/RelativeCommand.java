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
import com.cognizant.cognizantits.engine.execution.exception.element.ElementException;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import org.openqa.selenium.WebElement;

/**
 *
 *
 */
public class RelativeCommand extends Command {

    private enum RelativeAction {

        CLICK, SET
    };

    public RelativeCommand(CommandControl cc) {
        super(cc);
    }

    private Boolean isConditionValid() {
        return !Condition.matches("((Start|End) (Loop|Param)(:\\s)*)|Global Object|Continue");
    }

    private void doRelative(RelativeAction action) {
        if (isConditionValid()) {
            WebElement parent = AObject.findElement(Condition, Reference);
            if (parent != null) {
                Element = AObject.findElement(parent, ObjectName, Reference);
                if (Element != null) {
                    getCommander().Element = Element;
                    switch (action) {
                        case CLICK:
                            new Basic(getCommander()).Click();
                            break;
                        case SET:
                            new Basic(getCommander()).Set();
                            break;
                    }
                } else {
                    throw new ElementException(ElementException.ExceptionType.Element_Not_Found, ObjectName);
                }
            } else {
                throw new ElementException(ElementException.ExceptionType.Element_Not_Found, Condition);
            }
        } else {
            Report.updateTestLog(Action, "No Relative Element Found in Condition Column", Status.DEBUG);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Click on element based on parent [<Object>]",
            condition = InputType.YES)
    public void click_Relative() {
        doRelative(RelativeAction.CLICK);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Set [<Data>] on element based on parent [<Object>]", input = InputType.YES, condition = InputType.YES)
    public void set_Relative() {
        doRelative(RelativeAction.SET);
    }

}
