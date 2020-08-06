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

import com.cognizant.cognizantits.datalib.settings.ProjectSettings;
import com.cognizant.cognizantits.datalib.settings.emulators.Emulator;
import com.cognizant.cognizantits.engine.constants.FilePath;
import com.cognizant.cognizantits.engine.constants.SystemDefaults;
import com.cognizant.cognizantits.engine.core.Control;
import com.cognizant.cognizantits.engine.core.RunContext;
import static com.cognizant.cognizantits.engine.drivers.WebDriverFactory.Browser.values;
import com.cognizant.cognizantits.engine.drivers.customWebDriver.EmptyDriver;
import com.cognizant.cognizantits.engine.drivers.customWebDriver.ExtendedHtmlUnitDriver;
import com.cognizant.cognizantits.engine.drivers.findObjectBy.support.ByObjectProp;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galenframework.config.GalenConfig;
import com.galenframework.config.GalenProperty;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

public class WebDriverFactory {

    public enum Browser {

        FireFox("Firefox"),
        Chrome("Chrome"),
        IE("IE"),
        Edge("Edge"),
        Opera("Opera"),
        Safari("Safari"),
        ChromeHeadless("Chrome Headless"),
        HtmlUnit("HtmlUnit"),
        PhantomJS("PhantomJS"),
        Empty("No Browser"),
        Emulator("Emulator");

        private final String browserValue;

        Browser(String value) {
            browserValue = value;
        }

        public String getBrowserValue() {
            return browserValue;
        }

        public boolean isHeadLess() {
            return this == HtmlUnit || this == PhantomJS || this == Empty || this == ChromeHeadless;
        }

        @Override
        public String toString() {
            return getBrowserValue();
        }

        public static Browser fromString(String browserName) {
            for (Browser browser : values()) {
                if (browser.browserValue.equalsIgnoreCase(browserName)) {
                    return browser;
                }
            }
            return Emulator;
        }

        public static ArrayList<String> getValuesAsList() {
            ArrayList<String> browserList = new ArrayList<>();
            for (Browser browser : values()) {
                if (!browser.equals(Emulator)) {
                    browserList.add(browser.getBrowserValue());
                }
            }
            return browserList;
        }

    }

    public static ArrayList<String> getPlatFormList() {
        ArrayList<String> platFormList = new ArrayList<>();
        for (Platform platForm : Platform.values()) {
            platFormList.add(platForm.name());
        }
        return platFormList;
    }

    public static void initDriverLocation(ProjectSettings settings) {
        ByObjectProp.load();
        System.setProperty("firefox.bin.path", resolve(settings.getDriverSettings().getFirefoxBinaryPath()));
        System.setProperty("webdriver.chrome.driver", resolve(settings.getDriverSettings().getChromeDriverPath()));
        System.setProperty("webdriver.ie.driver", resolve(settings.getDriverSettings().getIEDriverPath()));
        System.setProperty("webdriver.edge.driver", resolve(settings.getDriverSettings().getEdgeDriverPath()));
        System.setProperty("webdriver.gecko.driver", resolve(settings.getDriverSettings().getGeckcoDriverPath()));
        GalenConfig.getConfig().setProperty(GalenProperty.SCREENSHOT_FULLPAGE,
                String.valueOf(Control.exe.getExecSettings().getRunSettings().getTakeFullPageScreenShot()));
        GalenConfig.getConfig().setProperty(GalenProperty.SCREENSHOT_AUTORESIZE, "false");

        GalenConfig.getConfig().setProperty(GalenProperty.SCREENSHOT_FULLPAGE_SCROLLWAIT, "200");
    }

    private static String resolve(String location) {
        if (location.startsWith(".")) {
            return new File(FilePath.getAppRoot() + File.separator + location.substring(1)).getAbsolutePath();
        }
        return location;
    }

    public static WebDriver createRemote(RunContext context, ProjectSettings settings) {
        String url = Control.exe.getExecSettings().getRunSettings().getRemoteGridURL();
        return create(context, settings, true, url);
    }

    public static WebDriver create(RunContext context, ProjectSettings settings) {
        return create(context, settings, false, null);
    }

    private static WebDriver create(RunContext context, ProjectSettings settings, Boolean isGrid, String remoteUrl) {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setPlatform(context.Platform);
        if (!context.BrowserVersion.equalsIgnoreCase("default")) {
            caps.setVersion(context.BrowserVersion);
        }
        caps.merge(getCapability(context.BrowserName, settings));
        return create(context.BrowserName, caps, settings, isGrid, remoteUrl);
    }

    private static WebDriver create(String browserName, DesiredCapabilities caps, ProjectSettings settings,
            Boolean isGrid, String remoteUrl) {
        Browser browser = Browser.fromString(browserName);
        Boolean maximize = true;
        WebDriver driver = null;
        switch (browser) {
            case FireFox:
                if (!isGrid) {
                    driver = new FirefoxDriver(withFirefoxProfile(caps));
                    addGeckoDriverAddon((FirefoxDriver) driver);
                } else {
                    caps = DesiredCapabilities.firefox().merge(withFirefoxProfile(caps));
                }
                break;
            case Chrome:
                maximize = false;
                if (!isGrid) {
                    driver = new ChromeDriver(withChromeOptions(caps));
                } else {
                    caps = DesiredCapabilities.chrome().merge(withChromeOptions(caps));
                }
                break;
            case ChromeHeadless:
                maximize = false;
                if (!isGrid) {
                    driver = new ChromeDriver(withHeadlessChrome(withChromeOptions(caps)));
                } else {
                    caps = DesiredCapabilities.chrome().merge(withHeadlessChrome(withChromeOptions(caps)));
                }
                break;
            case IE:
                if (!isGrid) {
                    driver = new InternetExplorerDriver(new InternetExplorerOptions(caps));
                } else {
                    caps = DesiredCapabilities.internetExplorer().merge(caps);
                }
                break;
            case Edge:
                if (!isGrid) {
                    driver = new EdgeDriver(caps);
                } else {
                    caps = DesiredCapabilities.edge().merge(caps);
                }
                break;
            case HtmlUnit:
                return new ExtendedHtmlUnitDriver(BrowserVersion.BEST_SUPPORTED);
            case Opera:
                if (!isGrid) {
                    driver = new OperaDriver(caps);
                } else {
                    caps = DesiredCapabilities.operaBlink().merge(caps);
                }
                break;
            case Safari:
                if (!isGrid) {
                    driver = new SafariDriver(caps);
                } else {
                    caps = DesiredCapabilities.safari().merge(caps);
                }
                break;
            case PhantomJS:
                if (!isGrid) {
                    driver = new PhantomJSDriver(caps);
                } else {
                    caps = DesiredCapabilities.phantomjs().merge(caps);
                }
                break;
            case Empty:
                return new EmptyDriver();
            case Emulator:
                return checkEmulators(browserName, caps, settings, isGrid, remoteUrl);
            default:
                throw new AssertionError(browser.name());
        }

        if (isGrid) {
            Boolean checkForProxy = settings.getDriverSettings().useProxy();
            driver = createRemoteDriver(remoteUrl, caps, checkForProxy, settings.getDriverSettings());
        }
        if (driver != null && maximize) {
            driver.manage().window().maximize();
        }
        return driver;
    }

    private static String toLString(Object o) {
        return Objects.toString(o, "").toLowerCase();
    }

    private static boolean isNullOrEmpty(Object o) {
        return Objects.isNull(o) || o.toString().isEmpty();
    }

    public static boolean isChromeEmulator(Emulator emulator) {
        return emulator != null && "Chrome Emulator".equalsIgnoreCase(emulator.getType());
    }

    private static WebDriver checkEmulators(String browserName, DesiredCapabilities caps, ProjectSettings settings,
            Boolean isGrid, String remoteUrl) {
        Emulator emulator = settings.getEmulators().getEmulator(browserName);
        if (emulator != null) {
            switch (emulator.getType()) {
                case "Duplicate":
                    return create(emulator.getDriver(), caps, settings, isGrid, remoteUrl);
                case "Emulator":
                    if (emulator.getDriver().equals("Chrome")) {
                        return checkAndSetSize(
                                create(emulator.getDriver(), getChromeUAECaps(caps, emulator), settings, isGrid, remoteUrl),
                                emulator.getSize());
                    } else {
                        return checkAndSetSize(
                                create(emulator.getDriver(), getFFUAECaps(caps, emulator), settings, isGrid, remoteUrl),
                                emulator.getSize());
                    }
                case "Chrome Emulator":
                    return create("Chrome", getChromeEmulatorCaps(caps, emulator.getDriver()), settings, isGrid, remoteUrl);
                case "Remote URL": {
                    return createRemoteDriver(emulator.getRemoteUrl(), caps, settings.getDriverSettings().useProxy(), settings.getDriverSettings());
                }
            }
        }
        return null;
    }

    private static boolean isAppiumNative(String remoteUrl, Map props) {
        return toLString(remoteUrl).matches(".*/wd/hub.*") && props != null
                && props.containsKey("platformName") && toLString(props.get("platformName")).matches("android|ios")
                && (!props.containsKey("browserName") || isNullOrEmpty(props.get("browserName")));
    }

    private static boolean isAndroidNative(Map props) {
        return toLString(props.get("platformName")).matches("android");
    }

    private static boolean isIOSNative(Map props) {
        return toLString(props.get("platformName")).matches("ios");
    }

    private static final Logger LOGGER = Logger.getLogger(WebDriverFactory.class.getName());

    private static WebDriver createRemoteDriver(String url, DesiredCapabilities caps, Boolean checkForProxy,
            Properties props) {
        try {
            if (isAppiumNative(url, caps.asMap())) {
                if (isAndroidNative(caps.asMap())) {
                    return new io.appium.java_client.android.AndroidDriver(new URL(url), caps);
                } else if (isIOSNative(caps.asMap())) {
                    return new io.appium.java_client.ios.IOSDriver(new URL(url), caps);
                }
            }
            if (url == null) {
                return new RemoteWebDriver(caps);
            }
            if (checkForProxy) {
                return new RemoteWebDriver(RemoteProxy.getProxyExecutor(new URL(url), props), caps);
            }
            return new RemoteWebDriver(new URL(url), caps);
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    private static DesiredCapabilities getChromeEmulatorCaps(DesiredCapabilities caps, String deviceName) {
        Map<String, String> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceName", deviceName);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);
        caps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        return caps;
    }

    private static DesiredCapabilities getChromeUAECaps(DesiredCapabilities caps, Emulator emulator) {
        ChromeOptions chromeOptions = new ChromeOptions();
        if (!emulator.getUserAgent().trim().isEmpty()) {
            chromeOptions.addArguments("--user-agent=" + emulator.getUserAgent());
        }
        caps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
        return caps;
    }

    private static DesiredCapabilities getFFUAECaps(DesiredCapabilities caps, Emulator emulator) {
        FirefoxProfile profile = new FirefoxProfile();
        if (!emulator.getUserAgent().trim().isEmpty()) {
            profile.setPreference("general.useragent.override", emulator.getUserAgent());
        }
        caps.setCapability(FirefoxDriver.PROFILE, profile);
        return caps;
    }

    private static WebDriver checkAndSetSize(WebDriver driver, String size) {
        if (driver != null) {
            if (size.matches("[0-9]+ x [0-9]+")) {
                int w = Integer.valueOf(size.split("x")[0].trim());
                int h = Integer.valueOf(size.split("x")[1].trim());
                driver.manage().window().setSize(new Dimension(w, h));
            }
        }
        return driver;
    }

    private static Object getPropertyValueAsDesiredType(String key, String value) {
        if (value != null && !value.isEmpty()) {
            if (value.toLowerCase().matches("(true|false)")) {
                return Boolean.valueOf(value);
            }
            if (value.matches("\\d+")) {
                return Integer.valueOf(value);
            }
            if (key.contains("loggingPrefs")) {
                return getLogPrefs(value);
            }
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private static LoggingPreferences getLogPrefs(String value) {
        LoggingPreferences logs = new LoggingPreferences();
        try {
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, String> prefs = mapper.readValue(value, HashMap.class);
            for (String logType : prefs.keySet()) {
                logs.enable(logType, Level.parse(prefs.get(logType)));
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return logs;
    }

    private static DesiredCapabilities getCapability(String browserName, ProjectSettings settings) {
        DesiredCapabilities caps = new DesiredCapabilities();
        Properties prop = settings.getCapabilities().getCapabiltiesFor(browserName);
        if (prop != null) {
            prop.keySet().stream().forEach((key) -> {
                caps.setCapability(key.toString(),
                        getPropertyValueAsDesiredType(key.toString(), prop.getProperty(key.toString())));
            });
        }
        return caps;
    }

    private static FirefoxOptions withFirefoxProfile(DesiredCapabilities caps) {
        FirefoxOptions fOptions = new FirefoxOptions();
        FirefoxProfile fProfile;
        Object obj = caps.getCapability(FirefoxDriver.PROFILE);
        if (obj != null && obj instanceof FirefoxProfile) {
            fProfile = (FirefoxProfile) obj;
        } else {
            fProfile = new FirefoxProfile();
        }

//        Patch provided in addGeckoDriverAddon
//        if (SystemDefaults.getClassesFromJar.get() && SystemDefaults.debugMode.get()) {
//            if (FilePath.getFireFoxAddOnPath().exists()) {
//                fProfile.addExtension(FilePath.getFireFoxAddOnPath());
//            }
//        }
        fProfile = addFFProfile(fProfile);
        caps.setCapability(FirefoxDriver.PROFILE, fProfile);
        String binPath = System.getProperty("firefox.bin.path");

        if (binPath != null && !binPath.isEmpty()) {
            fOptions.setBinary(binPath);
        }
        fOptions.merge(caps);
        return fOptions;
    }

    /**
     * https://developers.google.com/web/updates/2017/04/headless-chrome
     *
     * @param caps
     * @return
     */
    private static ChromeOptions withHeadlessChrome(ChromeOptions options) {
        Object obj = options.getCapability(ChromeOptions.CAPABILITY);
        if (obj != null && obj instanceof ChromeOptions) {
            options = (ChromeOptions) obj;
        } else {
            options = new ChromeOptions();
        }
        options.addArguments("--headless", "--disable-gpu", "--window-size=1366,768");
        options.setCapability(ChromeOptions.CAPABILITY, options);
        return options;
    }

    private static ChromeOptions withChromeOptions(DesiredCapabilities caps) {
        ChromeOptions options;
        Object obj = caps.getCapability(ChromeOptions.CAPABILITY);
        if (obj != null && obj instanceof ChromeOptions) {
            options = (ChromeOptions) obj;
        } else {
            options = new ChromeOptions();
        }
        if (!SystemDefaults.debugMode.get()) {
            options.addArguments("--disable-notifications");
        }
        if (SystemDefaults.getClassesFromJar.get()
                && SystemDefaults.debugMode.get()) {
            if (FilePath.getChromeAddOnPath().exists()) {
                options.addExtensions(FilePath.getChromeAddOnPath());
            }
        }
        options.addArguments("--start-maximized");
        options = addChromeOptions(options);
        caps.setCapability(ChromeOptions.CAPABILITY, options);
        return options;
    }

    private static FirefoxProfile addFFProfile(FirefoxProfile fProfile) {
        //Do your FirefoxProfile Settings over here

        return fProfile;
    }

    private static ChromeOptions addChromeOptions(ChromeOptions chromeOptions) {
        //Do your ChromeOptions Settings over here

        return chromeOptions;
    }

    /**
     * Patch for
     * https://github.com/CognizantQAHub/Cognizant-Intelligent-Test-Scripter/issues/7
     * Based on
     * https://github.com/mozilla/geckodriver/issues/759#issuecomment-308522851
     *
     * @param fDriver FirefoxDriver
     */
    private static void addGeckoDriverAddon(FirefoxDriver fDriver) {
        if (SystemDefaults.getClassesFromJar.get() && SystemDefaults.debugMode.get()) {
            if (FilePath.getFireFoxAddOnPath().exists()) {
                HttpCommandExecutor ce = (HttpCommandExecutor) fDriver.getCommandExecutor();
                String url = ce.getAddressOfRemoteServer() + "/session/" + fDriver.getSessionId() + "/moz/addon/install";
                addGeckoDriverAddon(FilePath.getFireFoxAddOnPath(), url);
            }
        }
    }

    private static Boolean addGeckoDriverAddon(File addonLoc, String url) {
        try {
            HttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(url);
            Map<String, Object> addonInfo = new HashMap<>();
            addonInfo.put("temporary", true);
            addonInfo.put("path", addonLoc.getAbsolutePath());
            String json = new Gson().toJson(addonInfo);
            StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            post.setEntity(requestEntity);
            return client.execute(post).getStatusLine().getStatusCode() == 200;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
