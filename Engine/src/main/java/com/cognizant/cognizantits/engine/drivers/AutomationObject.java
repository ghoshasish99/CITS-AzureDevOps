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
package com.cognizant.cognizantits.engine.drivers;

import com.cognizant.cognizantits.datalib.or.ObjectRepository;
import com.cognizant.cognizantits.datalib.or.common.ORAttribute;
import com.cognizant.cognizantits.datalib.or.common.ObjectGroup;
import com.cognizant.cognizantits.datalib.or.image.ImageORObject;
import com.cognizant.cognizantits.datalib.or.mobile.MobileORObject;
import com.cognizant.cognizantits.datalib.or.mobile.MobileORPage;
import com.cognizant.cognizantits.datalib.or.web.WebORObject;
import com.cognizant.cognizantits.datalib.or.web.WebORPage;
import com.cognizant.cognizantits.engine.constants.SystemDefaults;
import com.cognizant.cognizantits.engine.core.Control;
import com.cognizant.cognizantits.engine.drivers.findObjectBy.support.ByObjectProp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * 
 */
public class AutomationObject {

    WebDriver driver;

    String pageName;
    String objectName;
    FindType findType;
    private Integer waitTime;

    public static HashMap<String, Map<String, Map<String, String>>> dynamicValue = new HashMap<>();
    public static HashMap<String, String> globalDynamicValue = new HashMap<>();

    public enum FindType {
        GLOBAL_OBJECT,
        DEFAULT;

        public static FindType fromString(String val) {
            switch (val.toLowerCase()) {
                case "globalobject":
                    return GLOBAL_OBJECT;
                default:
                    return DEFAULT;
            }
        }
    }

    public AutomationObject() {
    }

    public AutomationObject(WebDriver Driver) {
        this.driver = Driver;
    }

    /**
     *
     * @param objectKey ObjectName in pageKey in OR
     * @param pageKey PageName in OR
     * @return
     */
    public WebElement findElement(String objectKey, String pageKey) {
        WebElement e = findElement(objectKey, pageKey, FindType.DEFAULT);
        return e;
    }

    /**
     *
     * @param element Driver or WebElement
     * @param objectKey ObjectName in pageKey in OR
     * @param pageKey PageName in OR
     * @return
     */
    public WebElement findElement(SearchContext element, String objectKey, String pageKey) {
        return findElement(element, objectKey, pageKey, FindType.DEFAULT);
    }

    public WebElement findElement(String objectKey, String pageKey, String Attribute) {
        return findElement(objectKey, pageKey, Attribute, FindType.DEFAULT);
    }

    public WebElement findElement(SearchContext element, String objectKey, String pageKey, String Attribute) {
        return findElement(element, objectKey, pageKey, Attribute, FindType.DEFAULT);
    }

    public WebElement findElement(String objectKey, String pageKey, FindType condition) {
        return findElement(driver, objectKey, pageKey, condition);
    }

    public WebElement findElement(SearchContext element, String objectKey, String pageKey, FindType condition) {
        pageName = pageKey;
        objectName = objectKey;
        findType = condition;
        return getElementFromList(findElements(element,
                getORObject(pageKey, objectKey), null));
    }

    public WebElement findElement(String objectKey, String pageKey, String Attribute, FindType condition) {
        return findElement(driver, objectKey, pageKey, Attribute, condition);
    }

    public WebElement findElement(SearchContext element, String objectKey, String pageKey, String Attribute, FindType condition) {
        pageName = pageKey;
        objectName = objectKey;
        findType = condition;
        return getElementFromList(findElements(element,
                getORObject(pageKey, objectKey), Attribute));
    }

    public List<WebElement> findElements(String objectKey, String pageKey) {
        return findElements(objectKey, pageKey, FindType.DEFAULT);
    }

    public List<WebElement> findElements(String objectKey, String pageKey, String Attribute) {
        return findElements(objectKey, pageKey, Attribute, FindType.DEFAULT);
    }

    public List<WebElement> findElements(String objectKey, String pageKey, FindType condition) {
        return findElements(driver, objectKey, pageKey, condition);
    }

    public List<WebElement> findElements(String objectKey, String pageKey, String Attribute, FindType condition) {
        return findElements(driver, objectKey, pageKey, Attribute, condition);
    }

    public List<WebElement> findElements(SearchContext element, String objectKey, String pageKey) {
        return findElements(element, objectKey, pageKey, FindType.DEFAULT);
    }

    public List<WebElement> findElements(SearchContext element, String objectKey, String pageKey, String Attribute) {
        return findElements(element, objectKey, pageKey, Attribute, FindType.DEFAULT);
    }

    public List<WebElement> findElements(SearchContext element, String objectKey, String pageKey, FindType condition) {
        pageName = pageKey;
        objectName = objectKey;
        findType = condition;
        return findElements(element, getORObject(pageKey, objectKey), null);
    }

    public List<WebElement> findElements(SearchContext element, String objectKey, String pageKey, String Attribute, FindType condition) {
        pageName = pageKey;
        objectName = objectKey;
        findType = condition;
        return findElements(element, getORObject(pageKey, objectKey), Attribute);
    }

    private WebElement getElementFromList(List<WebElement> elements) {
        return elements != null && !elements.isEmpty() ? elements.get(0) : null;
    }

    public ObjectGroup<?> getORObject(String page, String object) {
        ObjectRepository objRep = Control.getCurrentProject().getObjectRepository();
        if (objRep.getWebOR().getPageByName(page) != null) {
            return objRep.getWebOR().getPageByName(page).getObjectGroupByName(object);
        } else if (objRep.getMobileOR().getPageByName(page) != null) {
            return objRep.getMobileOR().getPageByName(page).getObjectGroupByName(object);
        }
        return null;
    }

    public String getObjectProperty(String pageName, String objectName, String propertyName) {
        return getWebObject(pageName, objectName).getAttributeByName(propertyName);
    }

    public ObjectGroup<WebORObject> getWebObjects(String page, String object) {
        ObjectRepository objRep = Control.getCurrentProject().getObjectRepository();
        if (objRep.getWebOR().getPageByName(page) != null) {
            return objRep.getWebOR().getPageByName(page).getObjectGroupByName(object);
        }
        return null;
    }

    public WebORObject getWebObject(String page, String object) {
        ObjectRepository objRep = Control.getCurrentProject().getObjectRepository();
        if (objRep.getWebOR().getPageByName(page) != null) {
            return objRep.getWebOR().getPageByName(page).getObjectGroupByName(object).getObjects().get(0);
        }
        return null;
    }

    public ObjectGroup<MobileORObject> getMobileObjects(String page, String object) {
        ObjectRepository objRep = Control.getCurrentProject().getObjectRepository();
        if (objRep.getMobileOR().getPageByName(page) != null) {
            return objRep.getMobileOR().getPageByName(page).getObjectGroupByName(object);
        }
        return null;
    }

    public MobileORObject getMobileObject(String page, String object) {
        ObjectRepository objRep = Control.getCurrentProject().getObjectRepository();
        if (objRep.getMobileOR().getPageByName(page) != null) {
            return objRep.getMobileOR().getPageByName(page).getObjectGroupByName(object).getObjects().get(0);
        }
        return null;
    }

    public ObjectGroup<ImageORObject> getImageObjects(String page, String object) {
        ObjectRepository objRep = Control.getCurrentProject().getObjectRepository();
        if (objRep.getImageOR().getPageByName(page) != null) {
            return objRep.getImageOR().getPageByName(page).getObjectGroupByName(object);
        }
        return null;
    }

    public ImageORObject getImageObject(String page, String object) {
        ObjectRepository objRep = Control.getCurrentProject().getObjectRepository();
        if (objRep.getImageOR().getPageByName(page) != null) {
            return objRep.getImageOR().getPageByName(page).getObjectGroupByName(object).getObjects().get(0);
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private synchronized List<WebElement> findElements(SearchContext context, ObjectGroup objectGroup, String prop) {
        if (objectGroup != null && !objectGroup.getObjects().isEmpty()) {
            if (objectGroup.getObjects().get(0) instanceof WebORObject) {
                return getWElements(context, objectGroup, prop);
            } else if (objectGroup.getObjects().get(0) instanceof MobileORObject) {
                return getMElements(context, objectGroup, prop);
            }
        }
        return null;
    }

    private List<WebElement> getWElements(SearchContext context, ObjectGroup<WebORObject> objectGroup, String prop) {
        long startTime = System.nanoTime();
        List<WebElement> elements = null;
        for (WebORObject object : objectGroup.getObjects()) {
            switchFrame(object.getFrame());
            elements = getElements(context, object.getAttributes(), prop);
            if (elements != null && !elements.isEmpty()) {
                break;
            }
        }
        printStats(elements, objectGroup, startTime, System.nanoTime());
        return elements;
    }

    private List<WebElement> getMElements(SearchContext context, ObjectGroup<MobileORObject> objectGroup, String prop) {
        long startTime = System.nanoTime();
        List<WebElement> elements = null;
        for (MobileORObject object : objectGroup.getObjects()) {
            elements = getElements(context, object.getAttributes(), prop);
            if (elements != null && !elements.isEmpty()) {
                break;
            }
        }
        printStats(elements, objectGroup, startTime, System.nanoTime());
        return elements;
    }

    private void printStats(List<?> elements,
            ObjectGroup<?> objectGroup, long startTime, long stopTime) {
        if (elements != null) {
            System.out.println(foundElementIn(objectGroup, stopTime, startTime));
        } else {
            System.out.println(notFoundIn(objectGroup));
        }
    }

    private static String foundElementBy(String attr, String val) {
        return String.format("Using @%s [%s], ", attr, val);
    }

    private static String foundElementIn(ObjectGroup<?> objectGroup, long stopTime, long startTime) {
        return String.format("Object '%s' Found in %s ms",
                objectGroup.getName(), (stopTime - startTime) / 1000000);
    }

    private String notFoundIn(ObjectGroup<?> objectGroup) {
        return String.format("Couldn't find Object '%s' in stipulated Time '%s' Seconds",
                objectGroup.getName(), getWaitTime());
    }

    private List<WebElement> getElements(final SearchContext context, final List<ORAttribute> attributes, final String prop) {
        WebDriverWait wait = new WebDriverWait(driver, getWaitTime());
        try {
            return wait.until((ExpectedCondition<List<WebElement>>) (WebDriver driver) -> {
                for (ORAttribute attr : attributes) {
                    if (!attr.getValue().trim().isEmpty()) {
                        if (prop == null || attr.getName().equals(prop)) {
                            List<WebElement> elements;
                            String tag = attr.getName();
                            String value = getRuntimeValue(attr.getValue());
                            By by = ByObjectProp.get().getBy(tag, value);
                            if (by != null) {
                                elements = context.findElements(by);
                                if (elements != null && !elements.isEmpty()) {
                                    System.out.print(foundElementBy(tag, value));
                                    return elements;
                                }
                            }
                        }
                    }
                }
                return null;
            });
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
            return null;
        }
    }

    private void switchFrame(String frameData) {
        try {
            if (frameData != null && !frameData.trim().isEmpty()) {
                driver.switchTo().defaultContent();
                if (frameData.trim().matches("[0-9]+")) {
                    driver.switchTo().frame(Integer.parseInt(frameData.trim()));
                } else {
                    WebDriverWait wait = new WebDriverWait(driver,
                            SystemDefaults.waitTime.get());
                    wait.until(ExpectedConditions
                            .frameToBeAvailableAndSwitchToIt(frameData));
                }

            }
        } catch (Exception ex) {
            //Error while switching to frame
        }
    }

    private String getRuntimeValue(String value) {
        if (findType != null && findType.equals(FindType.GLOBAL_OBJECT)) {
            for (String Key : globalDynamicValue.keySet()) {
                value = value.replace(Key, globalDynamicValue.get(Key));
            }
        }
        if (dynamicValue.containsKey(pageName)
                && dynamicValue.get(pageName).containsKey(objectName)) {
            for (String Key : dynamicValue.get(pageName).get(objectName).keySet()) {
                value = value.replace(Key,
                        dynamicValue.get(pageName).get(objectName).get(Key));
            }
        }

        return value;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;

    }

    /**
     * Get Object Details
     *
     * @param page
     * @return
     */
    public Map<String, WebElement> findAllElementsFromPage(String page) {
        return findElementsByRegex("*", page);
    }

    public Map<String, WebElement> findElementsByRegex(String regexObject, String page) {
        if (page == null || page.trim().isEmpty()) {
            throw new RuntimeException("Page Name is empty please give a valid pageName");
        }
        ObjectRepository objRep = Control.getCurrentProject().getObjectRepository();
        WebORPage wPage = null;
        MobileORPage mPage = null;
        if (objRep.getWebOR().getPageByName(page) != null) {
            wPage = objRep.getWebOR().getPageByName(page);
        } else if (objRep.getMobileOR().getPageByName(page) != null) {
            mPage = objRep.getMobileOR().getPageByName(page);
        }
        if (wPage == null && mPage == null) {
            throw new RuntimeException("Page [" + page + "] is not available in ObjectRepository");
        }
        Map<String, WebElement> elementList = new HashMap<>();
        if (wPage != null) {
            for (ObjectGroup<WebORObject> objectgroup : wPage.getObjectGroups()) {
                if (objectgroup.getName().matches(regexObject)) {
                    WebElement element = getElementFromList(getWElements(driver, objectgroup, null));
                    if (element != null) {
                        elementList.put(objectgroup.getName(), element);
                    }
                }
            }
        } else if (mPage != null) {
            for (ObjectGroup<MobileORObject> objectgroup : mPage.getObjectGroups()) {
                if (objectgroup.getName().matches(regexObject)) {
                    WebElement element = getElementFromList(getMElements(driver, objectgroup, null));
                    if (element != null) {
                        elementList.put(objectgroup.getName(), element);
                    }
                }
            }
        }
        return elementList;
    }

    public List<String> getObjectList(String page, String regexObject) {
        if (page == null || page.trim().isEmpty()) {
            throw new RuntimeException("Page Name is empty please give a valid pageName");
        }
        ObjectRepository objRep = Control.getCurrentProject().getObjectRepository();
        WebORPage wPage = null;
        MobileORPage mPage = null;
        if (objRep.getWebOR().getPageByName(page) != null) {
            wPage = objRep.getWebOR().getPageByName(page);
        } else if (objRep.getMobileOR().getPageByName(page) != null) {
            mPage = objRep.getMobileOR().getPageByName(page);
        }
        if (wPage == null && mPage == null) {
            throw new RuntimeException("Page [" + page + "] is not available in ObjectRepository");
        }
        List<String> elementList = new ArrayList<>();
        if (wPage != null) {
            for (ObjectGroup<WebORObject> objectgroup : wPage.getObjectGroups()) {
                if (objectgroup.getName().matches(regexObject)) {
                    elementList.add(regexObject);
                }
            }
        } else if (mPage != null) {
            for (ObjectGroup<MobileORObject> objectgroup : mPage.getObjectGroups()) {
                if (objectgroup.getName().matches(regexObject)) {
                    elementList.add(regexObject);
                }
            }
        }
        return elementList;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public void resetWaitTime() {
        this.waitTime = null;
    }

    private int getWaitTime() {
        return this.waitTime != null ? this.waitTime : SystemDefaults.elementWaitTime.get();
    }

}
