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
import com.cognizant.cognizantits.engine.galenWrapper.SpecValidation.SpecTitle;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import com.galenframework.specs.SpecText;
import java.util.Arrays;

/**
 *
 * 
 */
public class Title extends General {

    public Title(CommandControl cc) {
        super(cc);
    }

    private void assertTitle(SpecText.Type type) {
        SpecTitle spec = SpecReader.reader().getSpecTitle(type, Data);
        spec.setOriginalText(getMessage(type));
        validate(spec, RelativeElement.None);
    }

    private void assertTitleI(SpecText.Type type) {
        SpecTitle spec = SpecReader.reader().getSpecTitle(type, Data.toLowerCase());
        spec.setOperations(Arrays.asList(new String[]{"lowercase"}));
        spec.setOriginalText(getMessage(type));
        validate(spec, RelativeElement.None);
    }

    @Action(object = ObjectType.BROWSER, desc ="Assert if Browser's Title Equals [<Data>]", input =InputType.YES)
    public void assertTitleEquals() {
        assertTitle(SpecText.Type.IS);
    }

    @Action(object = ObjectType.BROWSER, desc ="Assert if Browser's Title Contains [<Data>]", input =InputType.YES)
    public void assertTitleContains() {
        assertTitle(SpecText.Type.CONTAINS);
    }

    @Action(object = ObjectType.BROWSER, desc ="Assert if Browser's Title StartsWith [<Data>]", input =InputType.YES)
    public void assertTitleStartsWith() {
        assertTitle(SpecText.Type.STARTS);
    }

    @Action(object = ObjectType.BROWSER, desc ="Assert if Browser's Title EndsWith [<Data>]", input =InputType.YES)
    public void assertTitleEndsWith() {
        assertTitle(SpecText.Type.ENDS);
    }

    @Action(object = ObjectType.BROWSER, 
    		desc ="Assert if Browser's Title Matches [<Data>]", 
    		input =InputType.YES)
    public void assertTitleMatches() {
        assertTitle(SpecText.Type.MATCHES);
    }

    @Action(object = ObjectType.BROWSER, 
    		desc ="Assert if Browser's Title Equals [Ignorecase] [<Data>]", 
    		input =InputType.YES)
    public void assertTitleIEquals() {
        assertTitleI(SpecText.Type.IS);
    }

    @Action(object = ObjectType.BROWSER, 
    		desc ="Assert if Browser's Title Contains [Ignorecase] [<Data>]", 
    		input =InputType.YES)
    public void assertTitleIContains() {
        assertTitleI(SpecText.Type.CONTAINS);
    }

    @Action(object = ObjectType.BROWSER, 
    		desc ="Assert if Browser's Title StartsWith [Ignorecase] [<Data>]", 
    		input =InputType.YES)
    public void assertTitleIStartsWith() {
        assertTitleI(SpecText.Type.STARTS);
    }

    @Action(object = ObjectType.BROWSER, 
    		desc ="Assert if Browser's Title EndsWith [Ignorecase] [<Data>]", 
    		input =InputType.YES)
    public void assertTitleIEndsWith() {
        assertTitleI(SpecText.Type.ENDS);
    }

   
    private String getMessage(SpecText.Type type) {
        return String.format("%s's Title %s %s ", ObjectName, type.toString(), Data);
    }
}
