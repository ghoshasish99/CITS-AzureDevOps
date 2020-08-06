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
import com.galenframework.specs.Side;
import com.galenframework.specs.SpecOn;
import java.util.List;

/**
 *
 * 
 */
public class On extends General {

    public On(CommandControl cc) {
        super(cc);
    }

    private void asssertElementOn(Side horizontal, Side vertical) {
        SpecOn spec = SpecReader.reader().getSpecOn(Condition, horizontal, vertical, Data);
        spec.setOriginalText(getMessage(horizontal, vertical, spec.getLocations()));
        validate(spec);
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] is  on top left of [<Object2>] [<Data>]", 
    		input =InputType.OPTIONAL, 
    		condition = InputType.YES)
    public void assertElementOnTopLeft() {
        asssertElementOn(Side.TOP, Side.LEFT);
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] is  on top right of [<Object2>] [<Data>]", 
    		input =InputType.OPTIONAL,
    		condition = InputType.YES)
    public void assertElementOnTopRight() {
        asssertElementOn(Side.TOP, Side.RIGHT);
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] is  on bottom left of [<Object2>] [<Data>]", 
    		input =InputType.OPTIONAL,
    		condition = InputType.YES)
    public void assertElementOnBottomLeft() {
        asssertElementOn(Side.BOTTOM, Side.LEFT);
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] is  on bottom right of [<Object2>] [<Data>]", 
    		input =InputType.OPTIONAL, 
    		condition = InputType.YES)
    public void assertElementOnBottomRight() {
        asssertElementOn(Side.BOTTOM, Side.RIGHT);
    }

    private String getMessage(Side horizontal, Side vertical, List<Location> locations) {
        String message = String.format("%s is On %s-%s of %s", ObjectName, horizontal.toString(), vertical.toString(), Condition);
        if (!locations.isEmpty()) {
            message += " over location" + Data;
        }
        return message;
    }
}
