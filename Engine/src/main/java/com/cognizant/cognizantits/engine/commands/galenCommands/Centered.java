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
import com.galenframework.specs.SpecCentered;

/**
 *
 * 
 */
public class Centered extends General {

    public Centered(CommandControl cc) {
        super(cc);
    }

    private void assertElementCentered(SpecCentered.Alignment alignment, SpecCentered.Location location) {
        SpecCentered spec = SpecReader.reader().getSpecCentered(Condition, Data, location, alignment);
        spec.setOriginalText(getMessage(alignment, location, spec.getErrorRate()));
        validate(spec);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] is centeredAllOn [<Object2>] [<Data>]", input =InputType.OPTIONAL, condition = InputType.YES)
    public void assertElementCenteredAOn() {
        assertElementCentered(SpecCentered.Alignment.ALL, SpecCentered.Location.ON);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] is centeredAllInside [<Object2>] [<Data>]", input =InputType.OPTIONAL, condition = InputType.YES)
    public void assertElementCenteredAInside() {
        assertElementCentered(SpecCentered.Alignment.ALL, SpecCentered.Location.INSIDE);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] is centeredHorizontallyOn [<Object2>] [<Data>]", input =InputType.OPTIONAL, condition = InputType.YES)
    public void assertElementCenteredHOn() {
        assertElementCentered(SpecCentered.Alignment.HORIZONTALLY, SpecCentered.Location.ON);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] is centeredHorizontallyInside [<Object2>] [<Data>]", input =InputType.OPTIONAL, condition = InputType.YES)
    public void assertElementCenteredHInside() {
        assertElementCentered(SpecCentered.Alignment.HORIZONTALLY, SpecCentered.Location.INSIDE);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] is centeredVerticallyOn [<Object2>] [<Data>]", input =InputType.OPTIONAL, condition = InputType.YES)
    public void assertElementCenteredVOn() {
        assertElementCentered(SpecCentered.Alignment.VERTICALLY, SpecCentered.Location.ON);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] is centeredVerticallyInside [<Object2>] [<Data>]", input =InputType.OPTIONAL, condition = InputType.YES)
    public void assertElementCenteredVInside() {
        assertElementCentered(SpecCentered.Alignment.VERTICALLY, SpecCentered.Location.INSIDE);
    }

    private String getMessage(SpecCentered.Alignment alignment, SpecCentered.Location location, int errorRate) {
        String message = String.format("%s is centered %s %s %s", ObjectName, alignment.toString(), location.toString(), Condition);
        if (Data != null && !Data.trim().isEmpty()) {
            message += " With Error rate " + errorRate;
        }
        return message;
    }

}
