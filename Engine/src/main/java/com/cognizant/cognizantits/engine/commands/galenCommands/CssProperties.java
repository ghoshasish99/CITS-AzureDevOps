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
import com.galenframework.specs.SpecCss;
import com.galenframework.specs.SpecText.Type;

/**
 *
 * 
 */
public class CssProperties extends General {

    public CssProperties(CommandControl cc) {
        super(cc);
    }

    private void assertElementCss(Type type) {
        SpecCss spec = SpecReader.reader().getSpecCSS(type, Data);
        spec.setOriginalText(getMessage(spec));
        validate(spec);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Assert if [<Object>]'s Css Property Equals [<Data>]", input = InputType.YES)
    public void assertElementCssPropEquals() {
        assertElementCss(Type.IS);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Assert if [<Object>]'s Css Property Contains [<Data>]", input = InputType.YES)
    public void assertElementCssPropContains() {
        assertElementCss(Type.CONTAINS);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Assert if [<Object>]'s Css Property StartsWith [<Data>]", input = InputType.YES)
    public void assertElementCssPropStartsWith() {
        assertElementCss(Type.STARTS);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Assert if [<Object>]'s Css Property EndsWith [<Data>]", input = InputType.YES)
    public void assertElementCssPropEndsWith() {
        assertElementCss(Type.ENDS);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Assert if [<Object>]'s Css Property Matches [<Data>]", input = InputType.YES)
    public void assertElementCssPropMatches() {
        assertElementCss(Type.MATCHES);
    }
    private String getMessage(SpecCss spec) {
        return String.format("%s's CssProperty %s %s %s ", ObjectName, spec.getCssPropertyName(), spec.getType().toString(), spec.getText());
    }
}
