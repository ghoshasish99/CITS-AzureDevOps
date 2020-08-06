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
package com.cognizant.cognizantits.engine.commands.mobile.nativ;

import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.execution.exception.element.ElementException;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.Keys;

/**
 *
 *
 */
@SuppressWarnings("rawtypes")
public class KeyActions extends MobileNativeCommand {

    public KeyActions(CommandControl cc) {
        super(cc);
    }

    /**
     * press enter key
     */
    @Action(object = ObjectType.BROWSER, desc = "Press enter key")
    public void enter() {
        try {
            if (!browserAction()) {
                if (Element != null) {
                    (Element).sendKeys(Keys.ENTER);
                    Report.updateTestLog(Action, "Enter pressed", Status.PASS);
                } else {
                    throw new ElementException(
                            ElementException.ExceptionType.Element_Not_Found, Condition);
                }
            } else {
                ((AndroidDriver) Driver).pressKeyCode(AndroidKeyCode.KEYCODE_ENTER);
                Report.updateTestLog(Action, "Enter pressed", Status.PASS);
            }
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(KeyActions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * press search key
     */
    @Action(object = ObjectType.BROWSER, desc = "Press search key(android)")
    public void search() {
        try {
            ((AndroidDriver) Driver).pressKeyCode(AndroidKeyCode.KEYCODE_SEARCH);
            Report.updateTestLog(Action, "Search pressed", Status.PASS);

        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(KeyActions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * press back key
     */
    @Action(object = ObjectType.BROWSER, desc = "Press search key(android)")
    public void back() {
        try {
            ((AndroidDriver) Driver).pressKeyCode(AndroidKeyCode.BACK);
            Report.updateTestLog(Action, "Back pressed", Status.PASS);

        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(KeyActions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * press home key
     */
    @Action(object = ObjectType.BROWSER, desc = "Press home key(android)")
    public void home() {
        try {
            ((AndroidDriver) Driver).pressKeyCode(AndroidKeyCode.HOME);
            Report.updateTestLog(Action, "home pressed", Status.PASS);

        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(KeyActions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * press home key
     */
    @Action(object = ObjectType.BROWSER, desc = "Press menu key(android)")
    public void menu() {
        try {
            ((AndroidDriver) Driver).pressKeyCode(AndroidKeyCode.MENU);
            Report.updateTestLog(Action, "Menu pressed", Status.PASS);

        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(KeyActions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * press settings key
     */
    @Action(object = ObjectType.BROWSER, desc = "Press settings key(android)")
    public void settings() {
        try {
            ((AndroidDriver) Driver).pressKeyCode(AndroidKeyCode.SETTINGS);
            Report.updateTestLog(Action, "settings pressed", Status.PASS);

        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(KeyActions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * press the given key in input
     */
    @Action(object = ObjectType.BROWSER, desc = "Press  key [<Data>](android)", input = InputType.YES)
    public void setKey() {
        try {
            Field f = AndroidKeyCode.class.getDeclaredField(Data);
            f.setAccessible(true);
            if (f.isAccessible()) {
                ((AndroidDriver) Driver).pressKeyCode((f.getInt(null)));
                Report.updateTestLog(Action, "Key '" + Data + "' pressed", Status.DONE);
            } else {
                Report.updateTestLog(Action, "Key '" + Data + "'not accessible/available", Status.DEBUG);
            }
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(KeyActions.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
