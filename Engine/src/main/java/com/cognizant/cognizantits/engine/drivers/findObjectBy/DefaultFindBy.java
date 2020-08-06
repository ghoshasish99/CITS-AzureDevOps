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
package com.cognizant.cognizantits.engine.drivers.findObjectBy;

import com.cognizant.cognizantits.engine.drivers.findObjectBy.support.SProperty;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;

/**
 *
 * 
 */
public class DefaultFindBy {

    @SProperty(name = "id")
    public By getById(String id) {
        return By.id(id);
    }

    @SProperty(name = "name")
    public By getByName(String name) {
        return By.name(name);
    }

    @SProperty(name = "xpath")
    public By getByXpath(String xpath) {
        return By.xpath(xpath);
    }

    @SProperty(name = "relative_xpath")
    public By getByRXpath(String xpath) {
        return By.xpath(xpath);
    }

    @SProperty(name = "css")
    public By getByCss(String css) {
        return By.cssSelector(css);
    }

    @SProperty(name = "link_text")
    public By getByLinkText(String linkText) {
        return By.linkText(linkText);
    }

    @SProperty(name = "partialLinkText")
    public By getByPartialLinkText(String linkText) {
        return By.partialLinkText(linkText);
    }

    @SProperty(name = "class")
    public By getByClass(String className) {
        if (className.contains(" ")) {
            return getByXpath("//*[@className='" + className + "']");
        }
        return By.className(className);
    }
   
    @SProperty(name = "type")
    public By getByType(String tagName) {
         return By.tagName(tagName);
    }

    @SProperty(name = "Accessibility")
    public By getByAccess(String access) {
        return MobileBy.AccessibilityId(access);
    }

    @SProperty(name = "UiAutomator")
    public By getByUiAutomator(String uiAutomator) {
        return MobileBy.AndroidUIAutomator(uiAutomator);
    }

    @SProperty(name = "UiAutomation")
    public By getByUiAutomation(String uiAutomation) {
        return MobileBy.IosUIAutomation(uiAutomation);
    }
}
