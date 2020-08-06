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

import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.galenWrapper.SpecValidation.SpecReader;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import com.galenframework.specs.Location;
import com.galenframework.specs.SpecInside;
import java.util.List;

/**
 *
 * 
 */
public class Inside extends General {

    public Inside(CommandControl cc) {
        super(cc);
    }

    private void assertElement(Boolean isPartly) {
        SpecInside spec = SpecReader.reader().getSpecInside(Condition, Data, isPartly);
        spec.setOriginalText(getMessage(isPartly, spec.getLocations()));
        validate(spec);
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] is inside [<Object2>] [<Data>]", 
    		input =InputType.OPTIONAL,
    		condition = InputType.YES)
    public void assertElementInside() {
        assertElement(false);
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] is partly inside [<Object2>] [<Data>]", 
    		input =InputType.OPTIONAL,
    		condition = InputType.YES)
    public void assertElementInsidePartly() {
        assertElement(true);
    }

    private String getMessage(Boolean isPartly, List<Location> locations) {
        String partly = isPartly ? " partly " : "";
        String message = String.format("%s is %sinside %s", ObjectName, partly, Condition);
        if (!locations.isEmpty()) {
            message += " over location" + Data;
        }
        return message;
    }

}
