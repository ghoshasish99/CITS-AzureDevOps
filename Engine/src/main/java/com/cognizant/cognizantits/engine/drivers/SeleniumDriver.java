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

import com.cognizant.cognizantits.datalib.settings.emulators.Emulator;
import com.cognizant.cognizantits.engine.core.Control;
import com.cognizant.cognizantits.engine.core.RunContext;
import com.cognizant.cognizantits.engine.drivers.WebDriverFactory.Browser;
import com.cognizant.cognizantits.engine.drivers.customWebDriver.EmptyDriver;
import com.cognizant.cognizantits.engine.drivers.customWebDriver.ExtendedHtmlUnitDriver;
import com.cognizant.cognizantits.engine.execution.exception.DriverClosedException;
import com.cognizant.cognizantits.engine.execution.exception.UnCaughtException;
import com.galenframework.config.GalenConfig;
import com.galenframework.config.GalenProperty;
import com.galenframework.utils.GalenUtils;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.android.AndroidDriver;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import ru.yandex.qatools.ashot.shooting.ShootingStrategy;

/**
 * Class to handle driver related operation
 *
 */
public class SeleniumDriver {

    public WebDriver driver;
    protected RunContext runContext;

    public void launchDriver(RunContext context) throws UnCaughtException {
        runContext = context;
        System.out.println("Launching " + runContext.BrowserName);
        try {
            if (Control.exe.getExecSettings().getRunSettings().isGridExecution()) {
                System.out.println("Launching Remote Driver");
                driver = WebDriverFactory.createRemote(context, Control.getCurrentProject().getProjectSettings());
            } else {
                System.out.println("Launching Local Driver");
                driver = WebDriverFactory.create(context, Control.getCurrentProject().getProjectSettings());
            }
            if (driver == null) {
                throw new UnCaughtException("Unknown Browser Requested -[ " + runContext.BrowserName + " ]");
            } else {
                System.out.println(runContext.BrowserName + " Launched");
            }
        } catch (UnCaughtException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
            throw new UnCaughtException("[Selenium Driver Exception] Cannot Initiate Browser " + ex.getMessage());
        }
    }

    public void launchDriver(String browser) throws UnCaughtException {
        RunContext context = new RunContext();
        context.BrowserName = browser;
        context.Browser = Browser.fromString(browser);
        context.Platform = Platform.getCurrent();
        context.BrowserVersion = "default";
        launchDriver(context);
    }

    public boolean isHeadless() {
        return driver == null || runContext.Browser.isHeadLess();
    }

    public void RestartBrowser() throws UnCaughtException {
        StopBrowser();
        StartBrowser(runContext.BrowserName);
    }

    public void StopBrowser() {
        try {
            driver.quit();
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
        }
        driver = null;
    }

    public void StartBrowser(String b) throws UnCaughtException {
        StopBrowser();
        launchDriver(b);
    }

    public void closeBrowser() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.OFF, "Couldn't Kill the Driver", ex);
            }
            driver = null;
        }
    }

    public String getCurrentBrowser() {
        return runContext.BrowserName;
    }

    public String getDriverName(String browserName) {
        try {
            Emulator emulator = Control.getCurrentProject().getProjectSettings().getEmulators()
                    .getEmulator(browserName);
            if (emulator != null) {
                return emulator.getDriver();
            }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
        }
        return browserName;
    }

    public Boolean isAlive() {
        try {
            if(driver instanceof MobileDriver){
                driver.manage();
            } else {
                driver.getCurrentUrl();
            }
            return true;
        } catch (Exception ex) {
            throw new DriverClosedException(runContext.BrowserName);
        }
    }

    public File createScreenShot() {
        try {
            if (driver == null) {
                System.err.println("Report Driver[" + runContext.BrowserName + "]  is not initialized");
            } else if (isAlive()) {
                if (alertPresent()) {
                    System.err.println("Couldn't take ScreenShot Alert Present in the page");
                    return ((TakesScreenshot) (new EmptyDriver())).getScreenshotAs(OutputType.FILE);
                } else if (driver instanceof MobileDriver || driver instanceof ExtendedHtmlUnitDriver
                        || driver instanceof EmptyDriver) {
                    return ((TakesScreenshot) (driver)).getScreenshotAs(OutputType.FILE);
                } else {
                    return createNewScreenshot();
                }
            }
        } catch (DriverClosedException ex) {
            System.err.println("Couldn't take ScreenShot Driver is closed or connection is lost with driver");
        }
        return null;
    }

    private File createNewScreenshot() {
        try {
            boolean chromeEmulator = WebDriverFactory.isChromeEmulator(
                    Control.getCurrentProject().getProjectSettings()
                    .getEmulators().getEmulator(runContext.BrowserName));
            boolean fullPage = GalenConfig.getConfig()
                    .getBooleanProperty(GalenProperty.SCREENSHOT_FULLPAGE);
            if (chromeEmulator) {
                return getScreenShotFromAShot(driver, fullPage);
            } else {
                return getScreenShotFromGalen(driver, fullPage);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error making screenshot", ex);
        }
    }

    private File getScreenShotFromAShot(WebDriver driver, boolean fullPage) throws Exception {
        ShootingStrategy strategy = fullPage ? ShootingStrategies.viewportPasting(400)
                : ShootingStrategies.simple();
        return getScreenShotFromAShot(driver, strategy);
    }

    private File getScreenShotFromGalen(WebDriver driver, boolean fullPage) throws Exception {
        return fullPage ? GalenUtils.makeFullScreenshot(driver)
                : GalenUtils.takeScreenshot(driver);
    }

    private File getScreenShotFromAShot(WebDriver driver, ShootingStrategy strategy) throws IOException {
        File file = File.createTempFile("screenshot", ".png");
        Screenshot screenshot = new AShot().shootingStrategy(strategy)
                .takeScreenshot(driver);
        ImageIO.write(screenshot.getImage(), "png", file);
        return file;
    }

    private boolean alertPresent() {
        try {
            driver.switchTo().alert();
            driver.switchTo().defaultContent();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getBrowserVersion() {
        if (runContext.BrowserVersion.equalsIgnoreCase("default")) {
            if (!runContext.Browser.equals((Browser.HtmlUnit))) {
                String browser_version;
                Capabilities cap;
                if (driver instanceof ExtendedHtmlUnitDriver) {
                    cap = ((ExtendedHtmlUnitDriver) driver).getCapabilities();
                } else if (driver instanceof MobileDriver) {
                    cap = ((RemoteWebDriver) driver).getCapabilities();
                    Object pV = cap.getCapability("platformVersion");
                    return pV == null ? "" : pV.toString();
                } else if (driver instanceof RemoteWebDriver) {
                    cap = ((RemoteWebDriver) driver).getCapabilities();
                } else {
                    return runContext.BrowserVersion;
                }
                String browsername = cap.getBrowserName();
                // This block to find out IE Version number
                if ("internet explorer".equalsIgnoreCase(browsername)) {
                    String uAgent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
                    // uAgent return as "MSIE 8.0 Windows" for IE8
                    if (uAgent.contains("MSIE") && uAgent.contains("Windows")) {
                        browser_version = uAgent.substring(uAgent.indexOf("MSIE") + 5, uAgent.indexOf("Windows") - 2);
                    } else if (uAgent.contains("Trident/7.0")) {
                        browser_version = "11.0";
                    } else {
                        browser_version = "0.0";
                    }
                } else {
                    // Browser version for Firefox and Chrome
                    browser_version = cap.getVersion();
                }
                if (browser_version.contains(".") && browser_version.length() > 5) {
                    return browser_version.substring(0, browser_version.indexOf("."));
                } else {
                    return browser_version;
                }
            }
        }
        return runContext.BrowserVersion;
    }

    public String getNodeIP() {
        // String[] details = GridInfoExtractor.getHostNameAndPort(hub_ip, hub_port,
        // ((RemoteWebDriver) driver).getSessionId());
        return null;
    }

    public String getPlatformName() {

        if (runContext.Platform.equals(Platform.ANY) || runContext.Platform.equals(Platform.getCurrent())) {
            Capabilities cap;
            if (driver instanceof ExtendedHtmlUnitDriver) {
                cap = ((ExtendedHtmlUnitDriver) driver).getCapabilities();
            } else if (driver instanceof MobileDriver) {
                cap = ((RemoteWebDriver) driver).getCapabilities();
                Object platf = cap.getCapability("platformName");
                if (platf != null && !platf.toString().isEmpty()) {
                    return platf.toString();
                } else {
                    return (driver instanceof AndroidDriver) ? "Android" : "IOS";
                }
            } else if (driver instanceof EmptyDriver) {
                return "Any";
            } else {
                cap = ((RemoteWebDriver) driver).getCapabilities();
            }
            Platform p = cap.getPlatform();
            if (p.name().equals(Platform.VISTA.name()) || p.name().equals(Platform.XP.name())
                    || p.name().equals(Platform.WINDOWS.name()) || p.name().equals(Platform.WIN8.name())) {
                switch (p.getMajorVersion() + "." + p.getMinorVersion()) {
                    case "5.1":
                        return "XP";
                    case "6.0":
                        return "VISTA";
                    case "6.1":
                        return "WIN7";
                    case "6.2":
                        return "WIN8";
                    case "6.3":
                        return "WIN8.1";
                    default:
                        return p.name();
                }
            } else {
                return p.toString();
            }
        } else if (runContext.PlatformValue.equals("WINDOWS")) {
            return "WIN";
        } else {
            return runContext.PlatformValue;
        }
    }

}
