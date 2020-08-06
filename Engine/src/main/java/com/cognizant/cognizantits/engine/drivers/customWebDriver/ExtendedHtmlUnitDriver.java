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
package com.cognizant.cognizantits.engine.drivers.customWebDriver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 *
 * 
 */
public class ExtendedHtmlUnitDriver extends HtmlUnitDriver implements TakesScreenshot {

    public ExtendedHtmlUnitDriver(BrowserVersion version) {
        super(version);
        setJavascriptEnabled(true);
    }

    public ExtendedHtmlUnitDriver() {
    }

    public ExtendedHtmlUnitDriver(boolean enableJavascript) {
        super(enableJavascript);
    }

    public ExtendedHtmlUnitDriver(Capabilities capabilities) {
        super(capabilities);
        setJavascriptEnabled(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        Rectangle rectangle = new Rectangle(0, 0, screenSize.width, screenSize.height);
        try {
            File ss = new File("image");
            ImageIO.write(new Robot().createScreenCapture(rectangle), "png", ss);
            return ((X) ss);
        } catch (AWTException ex) {
            Logger.getLogger(ExtendedHtmlUnitDriver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExtendedHtmlUnitDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
