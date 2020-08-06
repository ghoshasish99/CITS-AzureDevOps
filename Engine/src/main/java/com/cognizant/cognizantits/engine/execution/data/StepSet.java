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
package com.cognizant.cognizantits.engine.execution.data;

/**
 *
 * 
 */
public class StepSet {

    public final int from;
    public int to;
    private int times;
    private int counter;
    public Boolean isLoop = false;
    public Boolean isSubIterDynamic = false;

    public StepSet(int from) {
        this.from = from;
        to = -1;
        times = 1;
        counter = 1;
    }

    public int next() {
        times--;
        return ++counter;
    }

    public int current() {
        return counter;
    }

    public void breakIt() {
        counter += times;
        times = 0;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int resolvedTimes) {
        if (resolvedTimes >= 0) {
            times = Math.max(1, resolvedTimes) - 1;
        } else {
            isSubIterDynamic = true;
            times = Integer.MAX_VALUE;
        }
    }

}
