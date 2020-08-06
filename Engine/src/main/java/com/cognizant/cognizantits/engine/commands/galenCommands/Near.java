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
import com.galenframework.specs.SpecNear;
import java.util.List;

/**
 *
 * 
 */
public class Near extends General {

    public Near(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] is near [<Object2>] [<Data>]", 
    		input =InputType.OPTIONAL, 
    		condition = InputType.YES)
    public void assertElementNear() {
        SpecNear spec = SpecReader.reader().getSpecNear(Condition, Data);
        spec.setOriginalText(getMessage(spec.getLocations()));
        validate(spec);
    }

    private String getMessage(List<Location> locations) {
        return String.format("%s is Near %s over location %s", ObjectName, Condition, Data);
    }
}
