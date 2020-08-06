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
package com.cognizant.cognizantits.engine.reporting.aXe;

import com.cognizant.cognizantits.engine.constants.FilePath;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 *
 * 
 */
public class AXE {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static String axejs;

    private static final Map<String, String> RESULTS = new LinkedHashMap<>();

    /**
     * @return Contents of the axe.js or axe.min.js script with a configured
     * reporter.
     */
    private static String getContents() {
        if (axejs == null) {
            try {
                URL scriptUrl = AXE.class.getResource("/reporting/aXe/axe.min.js");
                return IOUtils.toString(scriptUrl, Charset.defaultCharset());
            } catch (IOException ex) {
                throw new RuntimeException("Could Not load Axe Js", ex);
            }
        }
        return axejs;
    }

    /**
     * Recursively injects aXe into all iframes and the top level document.
     *
     * @param driver WebDriver instance to inject into
     */
    public static void inject(final WebDriver driver) {
        final String script = getContents();
        final ArrayList<WebElement> parents = new ArrayList<>();

        injectIntoFrames(driver, script, parents);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        driver.switchTo().defaultContent();
        js.executeScript(script);
    }

    /**
     * Recursively find frames and inject a script into them.
     *
     * @param driver An initialized WebDriver
     * @param script Script to inject
     * @param parents A list of all toplevel frames
     */
    private static void injectIntoFrames(final WebDriver driver, final String script, final ArrayList<WebElement> parents) {
        final JavascriptExecutor js = (JavascriptExecutor) driver;
        final List<WebElement> frames = driver.findElements(By.tagName("iframe"));

        frames.stream().map((frame) -> {
            driver.switchTo().defaultContent();
            if (parents != null) {
                parents.stream().forEach((parent) -> {
                    driver.switchTo().frame(parent);
                });
            }
            driver.switchTo().frame(frame);
            return frame;
        }).forEach((frame) -> {
            js.executeScript(script);
            if (parents != null) {
                ArrayList<WebElement> localParents = (ArrayList<WebElement>) parents.clone();
                localParents.add(frame);

                injectIntoFrames(driver, script, localParents);
            }
        });
    }

    /**
     * Writes a raw object out to a JSON file with the specified name.
     *
     * @param file file to store json
     * @param output Object to write. Most useful if you pass in either the
     * Builder.analyze() response or the violations array it contains.
     */
    private static void writeResults(final File file, final Object output) {
        try {
            FileUtils.writeStringToFile(file, Objects.toString(output), "utf-8");
        } catch (IOException ex) {
            Logger.getLogger(AXE.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static int getViolationCount(String result) {
        try {
            Map<String, Object> map = MAPPER.readValue(result, Map.class);
            List list = (List) map.get("violations");
            return list.size();
        } catch (IOException ex) {
            Logger.getLogger(AXE.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public static void add(String name, String result) {
        if (getViolationCount(result) > 0) {
            if (RESULTS.containsKey(name)) {
                int i = 1;
                String mName = name + i++;
                while (RESULTS.containsKey(mName)) {
                    mName = name + i++;
                }
                name = mName;
            }
            RESULTS.putIfAbsent(name, result);
        }
    }

    public static void finishReports() {
        if (!RESULTS.isEmpty()) {
            createReportsIfNotPresent();
            createDataList();
        }
    }

    public static Boolean hasReports() {
        return !RESULTS.isEmpty();
    }

    public static void reset() {
        RESULTS.clear();
    }

    private static void createReportsIfNotPresent() {
        File file = new File(FilePath.getCurrentResultsPath(), "aXe");
        if (!file.exists()) {
            file.mkdirs();
            try {
                FileUtils.copyDirectory(new File(FilePath.getaXeReportTemplatePath()), file);
            } catch (IOException ex) {
                Logger.getLogger(AXE.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void createDataList() {
        File file = new File(FilePath.getCurrentResultsPath(), "aXe//data//data-list.js");
        try {
            String var = "var dataList = ";
            String result = var + AXE.MAPPER.writeValueAsString(RESULTS);
            FileUtils.writeStringToFile(file, result, "utf-8");
        } catch (IOException ex) {
            Logger.getLogger(AXE.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Chainable builder for invoking aXe. Instantiate a new Builder and
     * configure testing with the include(), exclude(), and options() methods
     * before calling analyze() to run.
     */
    public static class Builder {

        private final WebDriver driver;
        private final List<String> includes = new ArrayList<>();
        private final List<String> excludes = new ArrayList<>();
        private String options = "null";

        /**
         * Injects the aXe script into the WebDriver.
         *
         * @param driver An initialized WebDriver
         */
        public Builder(final WebDriver driver) {
            this.driver = driver;
            AXE.inject(this.driver);
        }

        /**
         * Set the aXe options.
         *
         * @param options Options object as a JSON string
         * @return
         */
        public Builder options(final String options) {
            if (options != null) {
                this.options = options;
            }
            return this;
        }

        /**
         * Include a selector.
         *
         * @param selector Any valid CSS selector
         * @return
         */
        public Builder include(final String selector) {
            if (selector != null) {
                if (selector.contains(",")) {
                    this.includes.addAll(Arrays.asList(selector.split(",")));
                } else {
                    this.includes.add(selector);
                }
            }
            return this;
        }

        /**
         * Exclude a selector.
         *
         * @param selector Any valid CSS selector
         * @return
         */
        public Builder exclude(final String selector) {
            if (selector != null) {
                if (selector.contains(",")) {
                    this.excludes.addAll(Arrays.asList(selector.split(",")));
                } else {
                    this.excludes.add(selector);
                }
            }
            return this;
        }

        /**
         * Run aXe against the page.
         *
         * @return An aXe results document
         */
        public String analyze() {
            String command;

            if (!includes.isEmpty() && !excludes.isEmpty()) {
                command = String.format("axe.a11yCheck({include: [%s], exclude: [%s]}, %s, arguments[arguments.length - 1]);",
                        "['" + StringUtils.join(includes, "'],['") + "']",
                        "['" + StringUtils.join(excludes, "'],['") + "']",
                        options);
            } else if (!excludes.isEmpty()) {
                command = String.format("axe.a11yCheck({exclude: [%s]}, %s, arguments[arguments.length - 1]);",
                        "['" + StringUtils.join(excludes, "'],['") + "']",
                        options);
            } else if (!includes.isEmpty()) {
                command = String.format("axe.a11yCheck({include: [%s]}, %s, arguments[arguments.length - 1]);",
                        "['" + StringUtils.join(includes, "'],['") + "']",
                        options);
            } else if (includes.size() == 1) {
                command = String.format("axe.a11yCheck('%s', %s, arguments[arguments.length - 1]);", includes.get(0).replace("'", ""), options);
            } else {
                command = String.format("axe.a11yCheck(document, %s, arguments[arguments.length - 1]);", options);
            }

            return execute(command);
        }

        /**
         * Run aXe against a specific WebElement.
         *
         * @param context A WebElement to test
         * @return An aXe results document
         */
        public String analyze(final WebElement context) {
            String command = String.format("axe.a11yCheck(arguments[0], %s, arguments[arguments.length - 1]);", options);

            return execute(command, context);
        }

        private String execute(final String command, final Object... args) {
            this.driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
            JavascriptExecutor js = ((JavascriptExecutor) this.driver);
            Object response = js.executeAsyncScript(command, args);
            String result = (String) js.executeScript("return JSON.stringify(arguments[0]);", response);
            return result;
        }

    }
}
