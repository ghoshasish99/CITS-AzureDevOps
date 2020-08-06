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
//import org.openqa.selenium.security.UserAndPassword;

public class BrowserUtility extends Command {

    public BrowserUtility(CommandControl cc) {
        super(cc);
    }

    /**
     * Maximizes the browser window
     */
    @Action(object = ObjectType.BROWSER, desc = "Maximize the browser.")
    public void maximize() {
        try {
            Driver.manage().window().maximize();
            Report.updateTestLog("maximize", " Window is maximized ", Status.DONE);
        } catch (Exception ex) {
            Report.updateTestLog("maximize", "Unable to maximize the Window ", Status.FAIL);
            Logger.getLogger(BrowserUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

}
