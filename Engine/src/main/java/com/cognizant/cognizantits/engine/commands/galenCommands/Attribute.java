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
import com.cognizant.cognizantits.engine.galenWrapper.SpecValidation.SpecAttribute;
import com.cognizant.cognizantits.engine.galenWrapper.SpecValidation.SpecReader;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import com.galenframework.specs.SpecText;
import java.util.Arrays;

/**
 *
 * 
 */
public class Attribute extends General {

    public Attribute(CommandControl cc) {
        super(cc);
    }

    private void assertElementAttr(SpecText.Type type) {
        SpecAttribute spec = SpecReader.reader().getSpecAttribute(type, Data);
        spec.setOriginalText(getMessage(spec));
        validate(spec, RelativeElement.None);
    }

    private void assertElementAttrI(SpecText.Type type) {
        SpecAttribute spec = SpecReader.reader().getSpecAttribute(type, Data.toLowerCase());
        spec.setOperations(Arrays.asList(new String[]{"lowercase"}));
        spec.setOriginalText(getMessage(spec));
        validate(spec, RelativeElement.None);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>]'s Attribute Equals [<Data>]", input =InputType.YES)
    public void assertElementAttrEquals() {
        assertElementAttr(SpecText.Type.IS);
    }

    
    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>]'s Attribute Contains [<Data>]", input =InputType.YES)
    public void assertElementAttrContains() {
        assertElementAttr(SpecText.Type.CONTAINS);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>]'s Attribute StartsWith [<Data>]", input =InputType.YES)
    public void assertElementAttrStartsWith() {
        assertElementAttr(SpecText.Type.STARTS);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>]'s Attribute EndsWith [<Data>]", input =InputType.YES)
    public void assertElementAttrEndsWith() {
        assertElementAttr(SpecText.Type.ENDS);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>]'s Attribute Matches [<Data>]", input =InputType.YES)
    public void assertElementAttrMatches() {
        assertElementAttr(SpecText.Type.MATCHES);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>]'s Attribute Equals [Ignorecase] [<Data>]", input =InputType.YES)
    public void assertElementAttrIEquals() {
        assertElementAttrI(SpecText.Type.IS);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>]'s Attribute Contains [Ignorecase] [<Data>]", input =InputType.YES)
    public void assertElementAttrIContains() {
        assertElementAttrI(SpecText.Type.CONTAINS);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>]'s Attribute StartsWith [Ignorecase] [<Data>]", input =InputType.YES)
    public void assertElementAttrIStartsWith() {
        assertElementAttrI(SpecText.Type.STARTS);
    }

    @Action(object = ObjectType.SELENIUM, desc ="Assert if [<Object>]'s Attribute EndsWith [Ignorecase] [<Data>]", input =InputType.YES)
    public void assertElementAttrIEndsWith() {
        assertElementAttrI(SpecText.Type.ENDS);
    }

    private String getMessage(SpecAttribute spec) {
        return String.format("%s's Attribute %s %s %s ", ObjectName, spec.getAtributeName(), spec.getType().toString(), spec.getText());
    }

}
