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
package com.cognizant.cognizantits.engine.commands.image;

import com.cognizant.cognizantits.engine.constants.FilePath;
import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.drivers.customWebDriver.EmptyDriver;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.sikuli.script.App;
import org.sikuli.script.Key;

public class Application extends ImageCommand {

    public static Map<String, App> appList = new HashMap<>();

    public Application(CommandControl cc) {
        super(cc);
    }

    /**
     * Open the given Application
     */
    @Action(object = ObjectType.APP,
            desc = "Open the Application [<Data>]",
            input = InputType.YES)
    public void openApp() {
        try {
            String loc, id;
            if (Data.contains(",")) {
                loc = Data.split(",")[0];
                id = Data.split(",")[1];
            } else {
                id = loc = Data;
            }
            appList.put(id, App.open(loc));
            Report.updateTestLog(Action, "Open action is done", Status.DONE);
            Thread.sleep(1000);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.FAIL);
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Close the given application window (should opened with openApp )
     */
    @Action(object = ObjectType.APP, desc = "[<Data>] app is closed", input = InputType.YES)
    public void closeApp() {
        try {
            String param = Data;
            Thread.sleep(500);
            appList.get(param).close();
            Report.updateTestLog(Action, "Close action is done for app " + Data,
                    Status.DONE);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.FAIL);
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Focus the given application window (should opened with openApp )
     */
    @Action(object = ObjectType.APP, desc = "Perform keyboard shortcut  [<Data>]", input = InputType.YES)
    public void focusApp() {
        try {
            String param = Data;
            appList.get(param).focus();
            Report.updateTestLog(Action, "Focus action is done for app " + Data,
                    Status.DONE);
            Thread.sleep(1000);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.FAIL);
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Perform the given shortcut operation
     */
    @Action(object = ObjectType.APP, desc = "Perform keyboard shortcut  [<Data>]", input = InputType.YES)
    public void shortcutKeys() {
        try {
            String[] keys = Data.split("\\+", -1);
            shortcutKeys(new ArrayList<>(Arrays.asList(keys)));
            Report.updateTestLog(Action, "Short Cut " + Data + "  is done",
                    Status.DONE);
            Thread.sleep(1000);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.FAIL);
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Press keyboard keys (need manual release)
     */
    @Action(object = ObjectType.APP, desc = "Keyboard[<Data>] shortcut press action is enabled", input = InputType.YES)
    public void pressKeys() {
        try {
            String[] keys = Data.split("\\+", -1);
            pressKeys(new ArrayList<>(Arrays.asList(keys)));
            Report.updateTestLog(Action, "Key [" + Data + "] press  action is done",
                    Status.DONE);
            Thread.sleep(1000);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.FAIL);
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Release the keys pressed
     */
    @Action(object = ObjectType.APP, desc = "Release key [<Data>].", input = InputType.YES)
    public void releaseKeys() {
        try {
            String[] keys = Data.split("\\+", -1);
            releaseKeys(new ArrayList<>(Arrays.asList(keys)));
            Report.updateTestLog(Action, "Key [" + Data + "] release  action is done",
                    Status.DONE);
            Thread.sleep(1000);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.FAIL);
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Type single keyboard commands(Delete,Escape..)
     */
    @Action(object = ObjectType.APP, desc = "Press [<Data>] key on keyboard ", input = InputType.YES)
    public void keyboardKey() {

        try {
            SCREEN.type(getKeyCode(Data));
            Report.updateTestLog(Action, "Key [" + Data + "] action is done",
                    Status.DONE);

        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.FAIL);
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Press page down given number of times
     */
    @Action(object = ObjectType.APP, desc = "Perform page down [<Data>]  times", input = InputType.YES)
    public void pageDown() {
        try {
            int count = 1;
            if (Data != null) {
                count = Integer.valueOf(Data);
            }
            for (int i = 1; i <= count; i++) {
                SCREEN.type(Key.PAGE_DOWN);
            }

            Report.updateTestLog(Action, "PageDown [" + Data + "] action is done",
                    Status.DONE);

        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.FAIL);
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Function to click the give text on SCREEN
     */
    @Action(object = ObjectType.APP, desc = "Click the [<Object>] on the text[<Data>]", input = InputType.YES)
    public void clickOn() {
        try {
            if (!Data.isEmpty()) {
                SCREEN.click(Data);
                Report.updateTestLog(Action, "Clicke on text " + Data, Status.DONE);
                Thread.sleep(500);
            } else {
                Report.updateTestLog(Action, "Action not performed,(Empty value received!)", Status.DONE);
            }

        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.FAIL);
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Function to store values into Data sheet. Input : [data
     * sheet],[column],[value]
     */
    @Action(object = ObjectType.ANY, desc = "Store variable value in to datasheet", input = InputType.YES)
    public void storeData() {
        try {
            String sheet = Data.split(",", -1)[0];
            String col = Data.split(",", -1)[1];
            String val = Data.split(",", -1)[2];
            if (val.startsWith("%") && val.endsWith("%")) {
                val = this.getVar(val);
            }
            userData.putData(sheet, col, val);
            Report.updateTestLog(Action, "Store  action is done", Status.DONE);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.FAIL);
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Action(object = ObjectType.APP, desc = "Take Screenshot of the Desktop")
    public void takeDesktopScreenShot() {
        try {
            String ssName = Report.getNewScreenShotName();
            File location = new File(FilePath.getCurrentResultsPath() + ssName);
            File srcFile = ((TakesScreenshot) (new EmptyDriver())).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(srcFile, location);
            Report.updateTestLog(Action, "ScreenShot Taken", Status.PASS, ssName);
        } catch (IOException ex) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, ex);
            Report.updateTestLog(Action, "Couldn't Take ScreenShot", Status.DEBUG);
        }
    }
}
