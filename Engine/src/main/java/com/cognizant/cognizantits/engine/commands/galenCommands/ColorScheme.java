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
import com.galenframework.specs.SpecColorScheme;

/**
 *
 * 
 */
public class ColorScheme extends General {

    public ColorScheme(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>] color scheme matches [<Data>] ", input =InputType.YES)
    public void assertElementColorScheme() {
        SpecColorScheme spec = SpecReader.reader().getSpecColorScheme(Data);
        spec.setOriginalText(getMessage());
        validate(spec, RelativeElement.None);
    }

    private String getMessage() {
        return String.format("%s's color scheme matches with %s", ObjectName, Data);
    }

}
