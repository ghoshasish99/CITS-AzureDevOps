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
import com.galenframework.specs.Range;
import com.galenframework.specs.SpecAbove;
import com.galenframework.specs.SpecBelow;
import com.galenframework.specs.SpecLeftOf;
import com.galenframework.specs.SpecRightOf;

/**
 *
 * 
 */
public class Direction extends General {

    public Direction(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] is  above [<Data>]", input =InputType.OPTIONAL, condition = InputType.YES)
    public void assertElementAbove() {
        SpecAbove spec = SpecReader.reader().getSpecAbove(Condition, Data);
        spec.setOriginalText(getMessage("above", spec.getRange()));
        validate(spec);
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] is  below [<Object2>] [<Data>]", 
    		input =InputType.OPTIONAL, 
    		condition = InputType.YES)
    public void assertElementBelow() {
        SpecBelow spec = SpecReader.reader().getSpecBelow(Condition, Data);
        spec.setOriginalText(getMessage("below", spec.getRange()));
        validate(spec);
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] is leftof [<Object2>] [<Data>]", 
    		input =InputType.OPTIONAL,
    		condition = InputType.YES)
    public void assertElementLeftOf() {
        SpecLeftOf spec = SpecReader.reader().getSpecLeftOf(Condition, Data);
        spec.setOriginalText(getMessage("left of", spec.getRange()));
        validate(spec);
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] is rightof [<Object2>] [<Data>]", 
    		input =InputType.OPTIONAL,condition = InputType.YES)
    public void assertElementRightOf() {
        SpecRightOf spec = SpecReader.reader().getSpecRightOf(Condition, Data);
        spec.setOriginalText(getMessage("right of", spec.getRange()));
        validate(spec);
    }

    private String getMessage(String direction, Range errorRate) {
        String message = String.format("%s is %s %s ", ObjectName, direction, Condition);
        if (errorRate != null && !errorRate.holds(0)) {
            message += " With Range " + errorRate.toString();
        }
        return message;
    }
}
