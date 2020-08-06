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
import com.galenframework.specs.SpecHeight;
import com.galenframework.specs.SpecWidth;

/**
 *
 * 
 */
public class WidthAndHeight extends General {

    public WidthAndHeight(CommandControl cc) {
        super(cc);
    }

    private void assertWidth(RelativeElement rElement) {
        SpecWidth spec = SpecReader.reader().getSpecWidth(rElement, Data, Condition);
        spec.setOriginalText(getMessage("width", rElement));
        validate(spec, rElement);
    }

    private void assertHeight(RelativeElement rElement) {
        SpecHeight spec = SpecReader.reader().getSpecHeight(rElement, Data, Condition);
        spec.setOriginalText(getMessage("height", rElement));
        validate(spec, rElement);
    }

    
    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>]'s width is [<Data>] ", 
    		input =InputType.YES,
    		condition = InputType.OPTIONAL)
    public void assertElementWidth() {
        assertWidth(RelativeElement.None);
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>] 's width is [<Data>] of [<Object2>]", 
    		input =InputType.YES, 
    		condition = InputType.YES
    		)
    public void assertElementWidthElement() {
        assertWidth(RelativeElement.WebElement);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>]'s height is [<Data>] ", input =InputType.YES, condition = InputType.OPTIONAL)
    public void assertElementHeight() {
        assertHeight(RelativeElement.None);
    }
    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] 's height is [<Data>] of [<Object2>]", input =InputType.YES, condition = InputType.YES)
    public void assertElementHeightElement() {
        assertHeight(RelativeElement.WebElement);
    }

    private String getMessage(String type, RelativeElement rElement) {
        String message = String.format("%s's %s is %s", ObjectName, type, Data);
        if (rElement.equals(RelativeElement.WebElement)) {
            message += " of " + Condition;
        }
        return message;
    }

}
