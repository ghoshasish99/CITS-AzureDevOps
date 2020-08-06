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
package com.cognizant.cognizantits.engine.core;

import com.cognizant.cognizantits.engine.drivers.WebDriverFactory.Browser;

public class RunContext {

    public String Scenario;
    public String TestCase;
    public String Description;
    public Browser Browser;
    public String BrowserName;
    public String BrowserVersion;
    public org.openqa.selenium.Platform Platform;
    public String Iteration;
    public String PlatformValue;
    public String BrowserVersionValue;
    public boolean useExistingDriver = false;

    public void print() {
        System.out.println("[Scenario:" + Scenario + "] [TestCase: " + TestCase + "]"
                + " [Description: " + Description + "] [Browser: " + BrowserName + "] "
                + "[BrowserVersion: " + BrowserVersion + "] [Platform: " + Platform.toString()
                + "][ExistingBrowser: " + useExistingDriver + "]"
        );
    }
    
    
    public String getName(){
        return String.format("%s_%s_%s_%s", Scenario,TestCase,Iteration,BrowserName);
    }

}
