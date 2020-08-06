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
package com.cognizant.cognizantits.engine.reporting.util;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

    private static final String DATE_FORMAT_NOW = "dd-MMM-yyyy";

    private static final String TIME_FORMAT_NOW = "HH:mm:ss.sss";

    private static final String DATE_FORMAT_FOR_FOLDER = "dd-MMM-yyyy";

    private static final String TIME_FORMAT_FOR_FOLDER = "HH-mm-ss";
    private static final String FORMAT = "%02d:%02d:%02d";
    public final long startTime;

    public DateTimeUtils() {
        startTime = System.currentTimeMillis();
    }

    public static String TimeNow() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }
   

    public static String DateNow() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    public static String DateNowForFolder() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_FOR_FOLDER);
        return sdf.format(cal.getTime());
    }

    public static String TimeNowForFolder() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_FOR_FOLDER);
        return sdf.format(cal.getTime());
    }

    public static String DateTimeNow() {
        return DateTimeUtils.DateNow() + " " + DateTimeUtils.TimeNow();
    }

    public static String DateTimeNowForFolder() {
        return DateTimeUtils.DateNowForFolder() + " " + DateTimeUtils.TimeNowForFolder();
    }

    public String timeRun() {
        final long duration = System.currentTimeMillis() - startTime;
        return parseTime(duration);
    }

    public static String parseTime(long milliseconds) {
        return String.format(FORMAT,
                TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(milliseconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }


}
