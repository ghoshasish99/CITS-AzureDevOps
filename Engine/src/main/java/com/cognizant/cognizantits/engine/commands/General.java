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
import com.cognizant.cognizantits.engine.execution.exception.element.ElementException.ExceptionType;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;

/**
 *
 * 
 */
public class General extends Command {

    public General(CommandControl cc) {
        super(cc);
    }

    public Boolean checkIfDriverIsAlive() {
        if (isDriverAlive()) {
            return true;
        } else {
            throw new RuntimeException("Seems like Connection with the driver is lost/driver is closed");
        }
    }

    public Boolean elementPresent() {
        return checkIfDriverIsAlive() && Element != null;
    }

    public Boolean elementSelected() {
        if (!elementDisplayed()) {
            throw new ElementException(ExceptionType.Element_Not_Visible, ObjectName);
        }
        return Element.isSelected();
    }

    public Boolean elementDisplayed() {
        if (!elementPresent()) {
            throw new ElementException(ExceptionType.Element_Not_Found, ObjectName);
        }
        return Element.isDisplayed();
    }

    public Boolean elementEnabled() {
        if (!elementDisplayed()) {
            throw new ElementException(ExceptionType.Element_Not_Visible, ObjectName);
        }
        return Element.isEnabled();
    }

    public boolean isHScrollBarPresent() {
        return (boolean) ((JavascriptExecutor) Driver)
                .executeScript("return document.documentElement.scrollWidth>document.documentElement.clientWidth;");
    }

    public boolean isvScrollBarPresent() {
        return (boolean) ((JavascriptExecutor) Driver)
                .executeScript("return document.documentElement.scrollHeight>document.documentElement.clientHeight;");
    }

    public boolean isAlertPresent() {
        try {
            Driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, e);
            return false;
        }
    }

}
