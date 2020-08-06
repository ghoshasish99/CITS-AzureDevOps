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
package com.cognizant.cognizantits.engine.galenWrapper;

import com.galenframework.parser.ExpectRange;
import com.galenframework.parser.Expectations;
import com.galenframework.parser.StringCharReader;
import com.galenframework.specs.Location;
import com.galenframework.specs.Range;
import com.galenframework.specs.colors.ColorRange;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 
 */
public class Parser {

    public static Range parseRange(String Data) {
        return Data == null || Data.trim().isEmpty() ? Range.greaterThan(-1)
                : Expectations.range().read(new StringCharReader(Data));
    }

    public static Range parseRangePercent(String Data) {
        return Data == null || Data.trim().isEmpty() ? Range.greaterThan(-1)
                : getRange(Data);
    }

    private static Range getRange(String Data) {
        ExpectRange expectRange = new ExpectRange();
        expectRange.setEndingWord("%");
        return expectRange.read(new StringCharReader(Data));
    }

    public static List<Location> parseLocation(String Data) {
        return Data == null || Data.trim().isEmpty() ? new ArrayList<Location>() : Expectations.locations().read(new StringCharReader(Data));
    }

    public static List<ColorRange> parseColorRanges(String Data) {
        return Data == null || Data.trim().isEmpty() ? new ArrayList<ColorRange>() : Expectations.colorRanges().read(new StringCharReader(Data));
    }

    public static int parseInt(String Data) {
        return Data == null || Data.trim().isEmpty() ? 0 : parseInteger(Data);
    }

    public static int parseInt(Object Data) {
        return Data == null ? 0 : parseInt(Data.toString());
    }

    private static int parseInteger(String Data) {
        if (Data.matches("[0-9]+")) {
            return Integer.parseInt(Data);
        } else {
            return 0;
        }
    }

}
