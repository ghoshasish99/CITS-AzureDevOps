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

import com.galenframework.page.PageElement;
import com.galenframework.page.selenium.SeleniumPage;
import com.galenframework.page.selenium.WebPageElement;
import com.galenframework.specs.page.Locator;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * 
 */
public class PageWrapper extends SeleniumPage {

    Map<String, WebElement> elementMap = new HashMap<>();
    private WebDriver driver;

    public PageWrapper(WebDriver driver) {
        super(driver);
        this.driver = driver;
    }

    public PageWrapper(WebDriver driver, Map<String, WebElement> elementMap) {
        this(driver);
        this.elementMap = elementMap;
    }

    public PageWrapper(WebDriver driver, String objectName, WebElement element) {
        super(driver);
        if (element != null) {
            this.elementMap.put(objectName, element);
        }
    }

    @Override
    public PageElement getObject(String objectName, Locator lctr) {
        return new WebPageElement(driver,objectName, elementMap.get(objectName), null);
    }

    public WebDriver getDriver() {
        return driver;
    }
}
