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

import com.cognizant.cognizantits.datalib.or.image.ImageORObject;
import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.execution.exception.ForcedException;
import com.cognizant.cognizantits.engine.execution.exception.element.ElementException.ExceptionType;
import com.cognizant.cognizantits.engine.support.Flag;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Region;

public class CommonImageMethods extends ImageCommand {

    public CommonImageMethods(CommandControl cc) {
        super(cc);
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // ----------------------ACTIONS--------------------
    /**
     * Find and click on a image in SCREEN
     */
    @Action(object = ObjectType.IMAGE, desc = "[<Object>] image is clicked", input = InputType.OPTIONAL)
    public void imgClick() {

        try {
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            if (target != null) {
                if (SCREEN.click(target, getKeyModifier()) == 1) {
                    Report.updateTestLog(Action, "clicked on " + ObjectName,
                            Status.DONE);
                    return;
                }

            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);

        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Action(object = ObjectType.IMAGE, desc = "[<Object>] image is clicked if present", input = InputType.OPTIONAL)
    public void imgClickIfExists() {
        try {
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            if (target != null) {
                if (SCREEN.click(target, getKeyModifier()) == 1) {
                    Report.updateTestLog(Action, "Clicked on " + ObjectName, Status.DONE);
                }
            } else {
                Report.updateTestLog(Action, ObjectName + " not found on screen",
                        Status.DONE);
            }

        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Type the given text on the image
     */
    @Action(object = ObjectType.IMAGE, desc = "text [<Data>] is typed in the image [<Object>]", input = InputType.YES)
    public void imgType() {

        try {
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            if (target != null) {
                if (SCREEN.type(target, Data) == 1) {
                    Report.updateTestLog(Action, "Typed " + Data + " on " + ObjectName,
                            Status.DONE);
                    return;
                }
            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Type the given text on the SCREEN(focused element)
     */
    @Action(object = ObjectType.APP, desc = "Type [<Data>] on the screen", input = InputType.YES)

    public void typeOnScreen() {
        try {
            String param = Data;
            SCREEN.type(param);
            Report.updateTestLog(Action, " Typed " + Data + " on screen",
                    Status.DONE);
            Thread.sleep(1000);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.FAIL);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * clear all text in the given image
     */
    @Action(object = ObjectType.IMAGE,
            desc = "Clear the text present inside the [<Object>]"
    )
    public void imgClearText() {

        try {
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            if (target != null) {
                ((Region) target).click();

                robot.keyPress(getKeyEvent("Ctrl"));
                robot.keyPress(KeyEvent.VK_A);
                robot.keyRelease(KeyEvent.VK_A);
                robot.keyRelease(getKeyEvent("Ctrl"));
                robot.keyPress(getKeyEvent("BACKSPACE"));
                robot.keyRelease(getKeyEvent("BACKSPACE"));
                Report.updateTestLog(Action,
                        "Cleared Text  on " + ObjectName, Status.DONE);
                return;

            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * clear and set the value on a image
     */
    @Action(object = ObjectType.IMAGE,
            desc = "clear text from image[<Object>] and enters the new text [<Data>]", input = InputType.YES)
    public void imgClearAndSet() {
        try {
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            if (target != null) {
                ((Region) target).click();
                robot.keyPress(getKeyEvent("Ctrl"));
                robot.keyPress(KeyEvent.VK_A);
                robot.keyRelease(KeyEvent.VK_A);
                robot.keyRelease(getKeyEvent("Ctrl"));
                robot.keyPress(getKeyEvent("BACKSPACE"));
                robot.keyRelease(getKeyEvent("BACKSPACE"));
                Thread.sleep(99);
                SCREEN.paste(Data);
                Report.updateTestLog(Action,
                        "Clear and Set on " + ObjectName + " is done", Status.DONE);
                return;

            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Mouse over on a image
     */
    @Action(object = ObjectType.IMAGE, desc = "mouse hovering is done on the image [<Data>].")
    public void imgHover() {

        try {
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            if (target != null) {
                if (SCREEN.hover(target) == 1) {
                    Report.updateTestLog(Action, "Hovered on " + ObjectName,
                            Status.DONE);
                    return;
                }
            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * paste/set the given text on the image
     */
    @Action(object = ObjectType.IMAGE, desc = " [<Data>] is set in the image [<Object>].", input = InputType.YES)
    public void imgSet() {

        try {
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            if (target != null) {
                if (SCREEN.paste(target, Data) == 1) {
                    Report.updateTestLog(Action, "Paste action is done on " + ObjectName,
                            Status.DONE);
                    return;
                }
            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * decrypt and paste/set the given text on the image
     */
    @Action(object = ObjectType.IMAGE,
            desc = "Enters encrypted text [<Data>] in the image[<Object>]",
            input = InputType.YES)
    public void imgSetEncrypted() {

        try {
            if (Data.endsWith(" Enc")) {
                Data = Data.substring(0, Data.lastIndexOf(" Enc"));
                byte[] valueDecoded = Base64.decodeBase64(Data);
                Data = new String(valueDecoded);
            }
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            if (target != null) {
                if (SCREEN.paste(target, Data) == 1) {
                    Report.updateTestLog(Action,
                            "Encrypted text set on " + ObjectName, Status.DONE);
                    return;
                }
            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(),
                    Status.DEBUG);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Double click on the Image
     */
    @Action(object = ObjectType.IMAGE, desc = "Double click on image [<Object>]")
    public void imgDoubleClick() {

        try {
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            if (target != null) {
                if (SCREEN.doubleClick(target, getKeyModifier()) == 1) {
                    Report.updateTestLog(Action,
                            "DoubleClick action is done on " + ObjectName, Status.DONE);
                    return;
                }
            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Right click on the image
     */
    @Action(object = ObjectType.IMAGE, desc = "Perform  Right click  action on the image [<Object>]")
    public void imgRightClick() {
        try {
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            if (target != null) {
                if (SCREEN.rightClick(target, getKeyModifier()) == 1) {
                    Report.updateTestLog(Action,
                            "RightClick action is done on " + ObjectName, Status.DONE);
                    return;
                }
            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * wait for given image to appear (default time is 10 seconds)
     */
    @Action(object = ObjectType.IMAGE, desc = "wait for the [<Object>] to be present on the screen is done", input = InputType.OPTIONAL)
    public void imgWait() {
        try {
            int timeout = parseToInt(Data, 10), timepassed = 0;
            Date startTime = new Date(), curTime;
            while (timepassed < timeout) {
                curTime = new Date();
                timepassed = (int) ((curTime.getTime() - startTime.getTime()) / 1000);
                target = findTarget(imageObjectGroup, Flag.REGION_ONLY,
                        Flag.MATCH_ONLY);
                if (target != null) {
                    Report.updateTestLog(Action, "Wait action is done on " + ObjectName,
                            Status.DONE);
                    return;
                }
                Thread.sleep(200);
                System.out.println("checking------------->");
            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * wait for the image to vanish
     */
    @Action(object = ObjectType.IMAGE, desc = "Wait for the [<Object>] to get vanish from the screen", input = InputType.OPTIONAL)
    public void imgWaitVanish() {

        double msec = parseToDouble(Data, SCREEN.getAutoWaitTimeout());
        boolean found = false;
        try {
            for (ImageORObject obj : imageObjectGroup.getObjects()) {
                target = getPattern(obj);
                setSettings(obj);
                try {
                    if (SCREEN.find(target) != null) {
                        found = true;
                        if (SCREEN.waitVanish(target, msec)) {
                            Report.updateTestLog(Action,
                                    "WaitVanish action is done on " + ObjectName, Status.DONE);
                            return;
                        }
                    }
                } catch (FindFailed ex) {
                    Logger.getLogger(CommonImageMethods.class.getName()).log(Level.WARNING, null, ex);
                }
            }
            Report.updateTestLog(
                    Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group
                            : (found ? " is not vanishing" : ExceptionType.Not_Found_on_Screen)),
                    Status.FAIL);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * verify the image present on SCREEN
     */
    @Action(object = ObjectType.IMAGE, desc = "Verify if the image [<Data>] is present on the screen.")
    public void imgVerifyImage() {

        try {
            target = findTarget(imageObjectGroup, Flag.REGION_ONLY, Flag.MATCH_ONLY);
            if (target != null) {
                ((Region) target).highlight(1);
                Report.updateTestLog(Action, "The Image " + ObjectName + " Exists on screen",
                        Status.PASS);
                return;
            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.DEBUG);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * assert the image present on SCREEN (stop execution if fails on break on
     * error mode)
     */
    @Action(object = ObjectType.IMAGE, desc = "Assert if the image [<Data>] is present on the screen.")
    public void imgAssertImage() {

        try {
            target = findTarget(imageObjectGroup, Flag.REGION_ONLY, Flag.MATCH_ONLY);
            if (target != null) {
                Report.updateTestLog(Action, "The Image " + ObjectName + " Exists on screen",
                        Status.PASS, getObjectAreas(target, ObjectName));
                return;
            }
            throw new Exception(ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen));
        } catch (Exception ex) {
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
            throw new ForcedException(Action, ex.getMessage());
        }

    }

    /**
     * Fine the image on the web page by Page down for given timeout (default 20
     * seconds)
     */
    @Action(object = ObjectType.IMAGE,
            desc = "Find the image  [<Data>] inside the webpage/application on screen within desired time",
            input = InputType.YES)
    public void imgFindinPage() {
        try {
            int timeout = parseToInt(Data, 20), timepassed = 0;
            Date startTime = new Date(), curTime;
            while (timepassed < timeout) {

                curTime = new Date();
                timepassed = (int) ((curTime.getTime() - startTime.getTime()) / 1000);

                target = findTarget(imageObjectGroup, Flag.REGION_ONLY,
                        Flag.MATCH_ONLY);
                if (target != null) {
                    ((Region) target).highlight(1);
                    Thread.sleep(700);
                    Report.updateTestLog(Action, "Finding "
                            + ObjectName + " action is done", Status.DONE);
                    return;
                }
                pageDownBrowser(20);
                Thread.sleep(500);
            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);

        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.FAIL);
            Logger.getLogger(CommonImageMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
