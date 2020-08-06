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

import com.cognizant.cognizantits.engine.commands.Command;
import com.cognizant.cognizantits.engine.core.CommandControl;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * 
 */
public class MobileNativeCommand extends Command {

    public MobileNativeCommand(CommandControl cc) {
        super(cc);
    }

    public int getInt(String Data, int i, int def) {
        try {
            return Integer.parseInt(Data.split(",")[i]);
        } catch (Exception ex) {
            Logger.getLogger(MobileNativeCommand.class.getName()).log(Level.WARNING, null, ex);
            return def;
        }
    }

    public int getInt(String Data, int def) {
        try {
            return Integer.parseInt(Data);
        } catch (Exception ex) {
            Logger.getLogger(MobileNativeCommand.class.getName()).log(Level.WARNING, null, ex);
            return def;
        }
    }

    public void waitfor(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException ex) {
            Logger.getLogger(MobileNativeCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
