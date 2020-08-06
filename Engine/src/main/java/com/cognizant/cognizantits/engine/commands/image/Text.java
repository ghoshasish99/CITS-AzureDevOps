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
import com.cognizant.cognizantits.engine.execution.exception.ForcedException;
import com.cognizant.cognizantits.engine.execution.exception.element.ElementException.ExceptionType;
import com.cognizant.cognizantits.engine.support.Flag;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sikuli.script.Region;

public class Text extends ImageCommand {

    public Text(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.IMAGE, desc ="Assert if the text [<Data>] is present inside the image [<Object>].", input =InputType.YES)
    public void imgAssertText() {
        assertText("");
    }

    @Action(object = ObjectType.IMAGE, desc ="Find Image [<Object>]  and assert text on above  matches [<Data>]", input =InputType.YES)
    public void imgAssertTextAbove() {
        assertText("Above");
    }

    @Action(object = ObjectType.IMAGE, desc ="Find Image [<Object>]  and assert text on below  matches [<Data>]", input =InputType.YES)
    public void imgAssertTextBelow() {
        assertText("Below");
    }

    @Action(object = ObjectType.IMAGE, desc ="Find Image [<Object>]  and assert text on right  matches [<Data>]", input =InputType.YES)
    public void imgAssertTextRight() {
        assertText("Right");
    }

    @Action(object = ObjectType.IMAGE, desc ="Find Image [<Object>]  and assert text on left  matches [<Data>]", input =InputType.YES)
    public void imgAssertTextLeft() {
        assertText("Left");
    }

    @Action(object = ObjectType.IMAGE, desc ="Verify if the text [<Data>] is present inside the image [<Object>].", input =InputType.YES)
    public void imgVerifyText() {
        verifyText("");
    }

    @Action(object = ObjectType.IMAGE, desc ="Find Image [<Object>]  and verify text above  matches [<Data>]", input =InputType.YES)
    public void imgVerifyTextAbove() {
        verifyText("Above");
    }

    @Action(object = ObjectType.IMAGE, desc ="Find Image [<Object>]  and verify text below  matches [<Data>]", input =InputType.YES)
    public void imgVerifyTextBelow() {
        verifyText("Below");
    }

    @Action(object = ObjectType.IMAGE, desc ="Find Image [<Object>]  and verify text on right  matches [<Data>]", input =InputType.YES)
    public void imgVerifyTextRight() {
        verifyText("Right");
    }

    @Action(object = ObjectType.IMAGE, desc ="Find Image [<Object>]  and verify text on left  matches [<Data>]", input =InputType.YES)
    public void imgVerifyTextLeft() {
        verifyText("Left");
    }

    @Action(object = ObjectType.IMAGE, desc ="Store image text into  the the variable  [<Data>].", input =InputType.YES)
    public void imgStoreText() {
        storeText("");
    }

    @Action(object = ObjectType.IMAGE, 
    		desc ="Find Image [<Object>]  and store text above into variable [<Data>] .", 
    		input =InputType.YES)
    public void imgStoreTextAbove() {
        storeText("Above");
    }

    @Action(object = ObjectType.IMAGE, desc ="Find Image [<Object>]  and store text below  into variable [<Data>] .", input =InputType.YES)
    public void imgStoreTextBelow() {
        storeText("Below");
    }

    @Action(object = ObjectType.IMAGE, desc ="Find Image [<Object>]  and store text on right into variable [<Data>] .", input =InputType.YES)
    public void imgStoreTextRight() {
        storeText("Right");
    }

    @Action(object = ObjectType.IMAGE, desc ="Find Image [<Object>]  and store text on left into variable [<Data>] .", input =InputType.YES)
    public void imgStoreTextLeft() {
        storeText("Left");
    }

    private void storeText(String where) {
        try {
            String varName = Data.contains(",") ? Data.split(",", -1)[0] : Data;
            if (varName.startsWith("%") && varName.endsWith("%")) {
                target = findTarget(imageObjectGroup, Flag.REGION_ONLY,
                        Flag.SET_COORDINATES, Flag.IMAGE_ONLY);
                if (target != null) {
                    Region rx = getRegion((Region) target, where);
                    rx.highlight(1);
                    String text = rx.text();
                    if (text != null) {
                        addVar(varName, text);
                        Report.updateTestLog(Action, "Text '"
                                + text + "' is stored in '" + varName + "'",
                                Status.DONE);
                        return;
                    }
                }
                Report.updateTestLog(Action,
                        ObjectName
                        + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group :ExceptionType.Not_Found_on_Screen),
                        Status.FAIL);
            } else {
                Report.updateTestLog(Action,
                        "Variable format is not correct", Status.FAIL);
            }
        } catch (Exception ex) {
            Report.updateTestLog(Action,
                    ex.getMessage(), Status.DEBUG);
            Logger.getLogger(Text.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void verifyText(String where) {
        try {
            Region rx;
            boolean found = false;
            rx = (Region) findTarget(imageObjectGroup, Flag.REGION_ONLY,
                    Flag.SET_COORDINATES, Flag.IMAGE_ONLY);

            if (rx != null) {
                found = true;
                rx = getRegion(rx, where);
                if (rx.text().contains(Data)) {
                    rx.highlight(1);
                    Report.updateTestLog(Action, "The Text \""
                            + Data + "\" is  Exists", Status.PASS);
                    return;
                }
            }
            Report.updateTestLog(Action, (found ? Data
                    : ObjectName)
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group : ExceptionType.Not_Found_on_Screen), Status.FAIL);

        } catch (Exception ex) {
            Report.updateTestLog(Action,
                    ex.getMessage(), Status.DEBUG);
            Logger.getLogger(Text.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void assertText(String where) {
        try {
            Region rx;
            boolean found = false;
            rx = (Region) findTarget(imageObjectGroup, Flag.REGION_ONLY,
                    Flag.SET_COORDINATES, Flag.IMAGE_ONLY);

            if (rx != null) {
                found = true;
                rx = getRegion(rx, where);
                if (rx.text().contains(Data)) {
                    rx.highlight(1);
                    Report.updateTestLog(Action, "The Text \""
                            + Data + "\" is  Exists", Status.PASS);
                    return;
                }
            }
            throw new Exception((found ? Data
                    : ObjectName)
                    + (imageObjectGroup.isLeaf() ? ExceptionType.Empty_Group :ExceptionType.Not_Found_on_Screen));

        } catch (Exception ex) {
            Logger.getLogger(Text.class.getName()).log(Level.SEVERE, null, ex);
            throw new ForcedException(Action, ex.getMessage());
        }

    }

}
