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

import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.execution.exception.element.ElementException.ExceptionType;
import com.cognizant.cognizantits.engine.support.Flag;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sikuli.script.Button;
import org.sikuli.script.Region;

public class Mouse extends ImageCommand {

    public Mouse(CommandControl cc) {
        super(cc);
    }

    /**
     * perform mouse down on the given image
     */
    @Action(object = ObjectType.IMAGE, 
    		desc ="Perform  Mouse key Down action on the image [<Object>]", 
    		input =InputType.YES)
    public void imgmouseDown() {
        int button = parseToInt(Data, Button.LEFT);
        try {
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            if (target != null) {
                ((Region) target).mouseDown(button);
                Report.updateTestLog(Action, "Mouse Down action is done on " + ObjectName,
                        Status.DONE);
                return;

            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);

        } catch (Exception ex) {
            Report.updateTestLog(Action,
                    ex.getMessage(), Status.DEBUG);
            Logger.getLogger(Mouse.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * perform mouse up on the given image
     */
    @Action(object = ObjectType.IMAGE, desc ="Perform  Mouse key Up  action on the image [<Object>]", input =InputType.YES)
    public void imgmouseUp() {
        int button = parseToInt(Data, Button.LEFT);
        try {
            target = findTarget(imageObjectGroup, Flag.SET_OFFSET, Flag.MATCH_ONLY);
            if (target != null) {
                ((Region) target).mouseUp(button);
                Report.updateTestLog(Action, "Mouse Up action is done on " + ObjectName,
                        Status.DONE);
                return;

            }
            Report.updateTestLog(Action,
                    ObjectName
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen),
                    Status.FAIL);

        } catch (Exception ex) {
            Report.updateTestLog(Action,
                    ex.getMessage(), Status.DEBUG);
            Logger.getLogger(Mouse.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Action(object = ObjectType.APP, desc ="Move the mouse to the location [<Data>]", input =InputType.YES)
    public void moveMouseTO() {
        try {
            Robot r = new Robot();
            int x = Integer.valueOf(Data.split(",")[0]);
            int y = Integer.valueOf(Data.split(",")[1]);
            //use graphic environment lib. if its is a multi SCREEN 
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (x < 0) {
                x += screenSize.getWidth();//x pixels left of right end
            }
            if (y < 0) {
                y += screenSize.getHeight();//y pixels above bottom end
            }
            r.mouseMove(x, y);
            Report.updateTestLog(Action, "Mouse moved to '" + x + "," + y + "' ", Status.DONE);
        } catch (Exception ex) {
            Report.updateTestLog(Action, ex.getMessage(), Status.FAIL);
            Logger.getLogger(Mouse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
