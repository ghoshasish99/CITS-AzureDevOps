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
import com.cognizant.cognizantits.engine.galenWrapper.SpecValidation.SpecUrl;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import com.galenframework.specs.SpecText;
import java.util.Arrays;

/**
 *
 *
 */
public class Url extends General {

    public Url(CommandControl cc) {
        super(cc);
    }

    private void assertUrl(SpecText.Type type) {
        SpecUrl spec = SpecReader.reader().getSpecUrl(type, Data);
        spec.setOriginalText(getMessage(type));
        validate(spec, RelativeElement.None);
    }

    private void assertUrlI(SpecText.Type type) {
        SpecUrl spec = SpecReader.reader().getSpecUrl(type, Data.toLowerCase());
        spec.setOperations(Arrays.asList(new String[]{"lowercase"}));
        spec.setOriginalText(getMessage(type));
        validate(spec, RelativeElement.None);
    }

    @Action(object = ObjectType.BROWSER,
            desc = "Assert if Browser's Url Equals [<Data>]",
            input = InputType.YES)
    public void assertUrlEquals() {
        assertUrl(SpecText.Type.IS);
    }

    @Action(object = ObjectType.BROWSER,
            desc = "Assert if Browser's Url Contains [<Data>]",
            input = InputType.YES)
    public void assertUrlContains() {
        assertUrl(SpecText.Type.CONTAINS);
    }

    @Action(object = ObjectType.BROWSER,
            desc = "Assert if Browser's Url StartsWith [<Data>]",
            input = InputType.YES)
    public void assertUrlStartsWith() {
        assertUrl(SpecText.Type.STARTS);
    }

    @Action(object = ObjectType.BROWSER,
            desc = "Assert if Browser's Url EndsWith [<Data>]",
            input = InputType.YES)
    public void assertUrlEndsWith() {
        assertUrl(SpecText.Type.ENDS);
    }

    @Action(object = ObjectType.BROWSER, desc = "Assert if Browser's Url Matches [<Data>]", input = InputType.YES)
    public void assertUrlMatches() {
        assertUrl(SpecText.Type.MATCHES);
    }

    @Action(object = ObjectType.BROWSER,
            desc = "Assert if Browser's Url Equals [Ignorecase] [<Data>]",
            input = InputType.YES)
    public void assertUrlIEquals() {
        assertUrlI(SpecText.Type.IS);
    }

    @Action(object = ObjectType.BROWSER,
            desc = "Assert if Browser's Url Contains [Ignorecase] [<Data>]",
            input = InputType.YES)
    public void assertUrlIContains() {
        assertUrlI(SpecText.Type.CONTAINS);
    }

    @Action(object = ObjectType.BROWSER,
            desc = "Assert if Browser's Url StartsWith [Ignorecase] [<Data>]",
            input = InputType.YES)
    public void assertUrlIStartsWith() {
        assertUrlI(SpecText.Type.STARTS);
    }

    @Action(object = ObjectType.BROWSER,
            desc = "Assert if Browser's Url EndsWith [Ignorecase] [<Data>]",
            input = InputType.YES)
    public void assertUrlIEndsWith() {
        assertUrlI(SpecText.Type.ENDS);
    }

    private String getMessage(SpecText.Type type) {
        return String.format("%s's Url %s %s ", ObjectName, type.toString(), Data);
    }

}
