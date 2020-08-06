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
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import com.galenframework.specs.SpecText;
import com.galenframework.specs.SpecText.Type;
import java.util.Arrays;
import org.openqa.selenium.support.ui.Select;

/**
 *
 *
 */
public class Text extends General {

    public Text(CommandControl cc) {
        super(cc);
    }

    private void assertElementText(Type type) {
        checkElementTypeBeforeProcessing();
        SpecText spec = SpecReader.reader().getSpecText(type, Data);
        spec.setOriginalText(getMessage(type));
        validate(spec);
    }

    private void assertElementTextI(Type type) {
        checkElementTypeBeforeProcessing();
        SpecText spec = SpecReader.reader().getSpecText(type, Data.toLowerCase());
        spec.setOperations(Arrays.asList(new String[]{"lowercase"}));
        spec.setOriginalText(getMessage(type));
        validate(spec);
    }

    private void checkElementTypeBeforeProcessing() {
        if (Element != null) {
            if (Element.getTagName().equalsIgnoreCase("select")) {
                Select select = new Select(Element);
                Element = select.getFirstSelectedOption();
                System.out.println("As it is Select Element assserting "
                        + "the text of first selected Element");
            }
        }
    }

    @Action(object = ObjectType.SELENIUM,
            desc = "Assert if [<Object>]'s Text Equals [<Data>]",
            input = InputType.YES)
    public void assertElementTextEquals() {
        assertElementText(Type.IS);
    }
    /***********************************/
    @Action(object = ObjectType.SELENIUM,
            desc = "Assert if [<Object>]'s Text Equals [<Data>]",
            input = InputType.YES)
    public void assertElementTextEqualsIfDataPresent() {
        if(!Data.isEmpty())
        {
        assertElementText(Type.IS);
        }
        else
            Report.updateTestLog(Action, "Validation skipped!", Status.DONE); 
    }
/*************************************/
    @Action(object = ObjectType.SELENIUM,
            desc = "Assert if [<Object>]'s Text Contains [<Data>]",
            input = InputType.YES)
    public void assertElementTextContains() {
        assertElementText(Type.CONTAINS);
    }

    @Action(object = ObjectType.SELENIUM,
            desc = "Assert if [<Object>]'s Text StartsWith [<Data>]",
            input = InputType.YES)
    public void assertElementTextStartsWith() {
        assertElementText(Type.STARTS);
    }

    @Action(object = ObjectType.SELENIUM,
            desc = "Assert if [<Object>]'s Text EndsWith [<Data>]",
            input = InputType.YES)
    public void assertElementTextEndsWith() {
        assertElementText(Type.ENDS);
    }

    @Action(object = ObjectType.SELENIUM,
            desc = "Assert if [<Object>]'s Text Matches [<Data>]",
            input = InputType.YES)
    public void assertElementTextMatches() {
        assertElementText(Type.MATCHES);
    }

    @Action(object = ObjectType.SELENIUM,
            desc = "Assert if [<Object>]'s Text Equals [Ignorecase] [<Data>]",
            input = InputType.YES)
    public void assertElementTextIEquals() {
        assertElementTextI(Type.IS);
    }

    @Action(object = ObjectType.SELENIUM,
            desc = "Assert if [<Object>]'s Text Contains [Ignorecase] [<Data>]",
            input = InputType.YES)
    public void assertElementTextIContains() {
        assertElementTextI(Type.CONTAINS);
    }

    @Action(object = ObjectType.SELENIUM,
            desc = "Assert if [<Object>]'s Text StartsWith [Ignorecase] [<Data>]",
            input = InputType.YES)
    public void assertElementTextIStartsWith() {
        assertElementTextI(Type.STARTS);
    }

    @Action(object = ObjectType.SELENIUM,
            desc = "Assert if [<Object>]'s Text EndsWith [Ignorecase] [<Data>]",
            input = InputType.YES)
    public void assertElementTextIEndsWith() {
        assertElementTextI(Type.ENDS);
    }

    private String getMessage(Type type) {
        return String.format("%s's Text %s %s ", ObjectName, type.toString(), Data);
    }
}
