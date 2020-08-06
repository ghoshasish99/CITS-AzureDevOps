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
import com.galenframework.specs.SpecContains;

/**
 *
 * 
 */
public class Contains extends General {

    public Contains(CommandControl cc) {
        super(cc);
    }

    public void assertElementContains(Boolean isPartly) {
        SpecContains spec = SpecReader.reader().getSpecContains(getElementsList(), isPartly);
        spec.setOriginalText(getMessage(isPartly));
        validate(spec, RelativeElement.WebElementList);
    }

    
    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] contains <Object2> ", 
    	
    		condition = InputType.YES)
    public void assertElementContains() {
        assertElementContains(false);
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] partly contains  <Object2> ", 
    		input =InputType.NO, 
    		condition = InputType.YES)
    public void assertElementContainsPartly() {
        assertElementContains(true);
    }

    private String getMessage(Boolean isPartly) {
        String partly = isPartly ? " partly " : "";
        return String.format("%s %scontains %s", ObjectName, partly, Condition);
    }

}
