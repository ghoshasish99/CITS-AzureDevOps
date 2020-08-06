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
import com.galenframework.specs.SpecHorizontally;
import com.galenframework.specs.SpecVertically;

/**
 *
 * 
 */
public class Align extends General {

    public Align(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] is aligned horizontally [<Data>] with [<Object2>]", input =InputType.YES, condition = InputType.YES)
    public void assertElementAlignedHoriz() {
        SpecHorizontally spec = SpecReader.reader().getSpecHorizontally(Condition, Data);
        spec.setOriginalText(getMessage("Horizontally", spec.getErrorRate()));
        validate(spec);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] is aligned vertically [<Data>] with [<Object2>]", input =InputType.YES, condition = InputType.YES)
    public void assertElementAlignedVert() {
        SpecVertically spec = SpecReader.reader().getSpecVertically(Condition, Data);
        spec.setOriginalText(getMessage("Vertically", spec.getErrorRate()));
        validate(spec);
    }

    private String getMessage(String align, int errorRate) {
        String message = String.format("%s is aligned %s with %s", ObjectName, align, Condition);
        if (errorRate != 0) {
            message += " With Error rate " + errorRate;
        }
        return message;
    }

}
