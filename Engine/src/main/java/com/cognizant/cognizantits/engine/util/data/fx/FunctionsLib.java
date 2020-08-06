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
package com.cognizant.cognizantits.engine.util.data.fx;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 *
 * 
 */
public class FunctionsLib {

    public Object getDate(int dx) {
        return getDate(dx, "dd/MM/yyyy");
    }

    public Object getDate(int dx, String format) {
        SimpleDateFormat date = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, dx);
        return date.format(cal.getTime());
    }

    public Object getRound(Double val) {
        return Math.round(val);
    }

    public Object getRandom(Double from, Double to) {
        Random rn = new Random(System.currentTimeMillis());
        return from + (rn.nextDouble() * (to - from));
    }

    public Object getRandom(Double len) {
        return getRandom(Math.pow(10d, len - 1), Math.pow(10d, len) - 1);
    }

    public Object getPow(Double a, Double b) {
        return Math.pow(a, b);
    }

    public Object getMin(Double a, Double b) {
        return Math.min(a, b);
    }

    public Object getMax(Double a, Double b) {
        return Math.max(a, b);
    }

}
