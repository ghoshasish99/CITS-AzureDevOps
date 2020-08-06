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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.JavascriptExecutor;

/**
 *
 * 
 */
public class Scroll extends General {

    private final String currentHScrollPos = "Math.max("
            + "document.body.scrollLeft, document.documentElement.scrollLeft,"
            + "document.body.offsetLeft, document.documentElement.offsetLeft,"
            + "document.body.clientLeft, document.documentElement.clientLeft)";
    private final String currentVScrollPos = "Math.max("
            + "document.body.scrollTop, document.documentElement.scrollTop,"
            + "document.body.offsetTop, document.documentElement.offsetTop,"
            + "document.body.clientTop, document.documentElement.clientTop)";
    private final String docScrollHeight = "Math.max("
            + "document.body.scrollHeight, document.documentElement.scrollHeight,"
            + "document.body.offsetHeight, document.documentElement.offsetHeight,"
            + "document.body.clientHeight, document.documentElement.clientHeight)";
    private final String docScrollWidth = "Math.max("
            + "document.body.scrollWidth, document.documentElement.scrollWidth,"
            + "document.body.offsetWidth, document.documentElement.offsetWidth,"
            + "document.body.clientWidth, document.documentElement.clientWidth)";

    public Scroll(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.BROWSER, desc ="Scroll horizondally to [<Data>]", input =InputType.YES)
    public void scrollHorizontallyTo() {
        if (Data != null && Data.trim().toLowerCase().matches("(left|right|\\d*)")) {
            scrollTo(getScrollData(Data), currentVScrollPos);
        } else {
            Report.updateTestLog(Action, "Invalid input[" + Data + "] It should be [(left|right|number)] ", Status.DEBUG);
        }
    }

    @Action(object = ObjectType.BROWSER, desc ="Scroll vertically to [<Data>]", input =InputType.YES)
    public void scrollVerticallyTo() {
        if (Data != null && Data.trim().toLowerCase().matches("(top|bottom|\\d*)")) {
            scrollTo(currentHScrollPos, getScrollData(Data));
        } else {
            Report.updateTestLog(Action, "Invalid input[" + Data + "] It should be [(top|bottom|number)] ", Status.DEBUG);
        }
    }

    @Action(object = ObjectType.BROWSER, desc ="Scroll to [<Data>]", input =InputType.YES)
    public void scrollTo() {
        if (Data != null && Data.trim().toLowerCase().matches("(left|right|\\d*),(top|bottom|\\d*)")) {
            scrollTo(getScrollData(Data.split(",")[0]), getScrollData(Data.split(",")[1]));
        } else {
            Report.updateTestLog(Action, "Invalid input[" + Data + "] It should be [(left|right|number),(top|bottom|number)] ", Status.DEBUG);
        }
    }

    @Action(object = ObjectType.BROWSER, desc ="Scroll to top")
    public void scrollToTop() {
        scrollTo(currentHScrollPos, "0");
    }

    @Action(object = ObjectType.BROWSER, desc ="Scroll to bottom")
    public void scrollToBottom() {
        scrollTo(currentHScrollPos, docScrollHeight);
    }

    @Action(object = ObjectType.BROWSER, desc ="Scroll to left")
    public void scrollToLeft() {
        scrollTo("0", currentVScrollPos);
    }

    @Action(object = ObjectType.BROWSER, desc ="Scroll to page")
    public void scrollToRight() {
        scrollTo(docScrollWidth, currentVScrollPos);
    }

    private String getScrollData(String val) {
        try {
            switch (val.trim().toLowerCase()) {
                case "top":
                    return "0";
                case "bottom":
                    return docScrollHeight;
                case "left":
                    return "0";
                case "right":
                    return docScrollWidth;
                default:
                    return Integer.valueOf(val).toString();
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
            System.out.println("Invalid value" + val);
        }
        return null;
    }

    private void scrollTo(String x, String y) {
        if (checkIfDriverIsAlive()) {
            ((JavascriptExecutor) Driver).executeScript("window.scrollTo(" + x + ", " + y + ");");
            Report.updateTestLog(Action, "Browser Scrolled to [" + Data + "]", Status.DONE);
        }

    }

}
