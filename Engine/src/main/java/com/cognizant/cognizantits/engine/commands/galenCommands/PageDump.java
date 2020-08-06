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
package com.cognizant.cognizantits.engine.commands.galenCommands;

import com.cognizant.cognizantits.engine.constants.FilePath;
import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.core.Control;
import com.cognizant.cognizantits.engine.galenWrapper.GalenWrapper;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * 
 */
public class PageDump extends General {

    public PageDump(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.BROWSER, desc = "Creates Page dump for Page [<Data>]", input = InputType.YES)
    public void createPageDump() {
        checkIfDumpResourceExists();
        Reference = Data;
        Condition = "(.*)";
        try {
            GalenWrapper.dumpPage(getPageValidation(RelativeElement.WebElementList),
                    Data,
                    Report.getTestCaseName(),
                    new File(FilePath.getPageDumpLocation() + File.separator + Report.getTestCaseName() + File.separator + Data));
            Report.updateTestLog(Action, "Page Dump created for page" + Data, Status.DONE);
        } catch (Exception ex) {
            Report.updateTestLog(Action, "Page Dump creation -Failed " + ex.getMessage(), Status.DEBUG);
            Logger.getLogger(PageDump.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void checkIfDumpResourceExists() {
        File file = new File(Control.getCurrentProject().getLocation() + File.separator + "PageDump");
        if (!file.exists() || file.listFiles() == null || file.listFiles().length == 0) {
            file.mkdirs();
            try {
                FileUtils.copyDirectory(new File(FilePath.getPageDumpResourcePath()), file);
            } catch (IOException ex) {
                Logger.getLogger(PageDump.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
