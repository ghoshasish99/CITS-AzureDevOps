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
package com.cognizant.cognizantits.engine.commands;

import com.cognizant.cognizantits.engine.commands.galenCommands.Text;
import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 *
 * 
 */
public class ByLabel extends Command {

    CommandControl cc;//Commander

    public ByLabel(CommandControl cc) {
        super(cc);
        this.cc = cc;//Commander
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Set the data [<Data>] to an input element that is adjacent to the provided label element [<Object>]", 
    		input =InputType.YES)
    public void setInputByLabel() {
        cc.Element = findInputElementByLabelTextByXpath();
        new Basic(cc).Set();
    }
    
    @Action(object = ObjectType.SELENIUM, 
    		desc ="Click on an element whose label is provided in the [<Object>]"
    		)
    public void clickInputByLabel() {
        cc.Element = findInputElementByLabelTextByXpath();
        new Basic(cc).Click();
    }
    @Action(object = ObjectType.BROWSER, 
    		desc ="click on the element whose label is provided in the [<Input>]", 
    		input =InputType.YES)
    public void clickInputByText() {
        cc.Element = findInputElementByLabelTextByXpath(Data);//Another variant
        new Basic(cc).Click();
    }

    @Action(object = ObjectType.SELENIUM, desc ="Submit input element adjacent to the provided label element [<Object>]")
    public void submitInputByLabel() {
        cc.Element = findInputElementByLabelTextByXpath();
        new Basic(cc).Submit();
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>]'s Text adjacent to provided label element Equals [<Data>]", 
    		input =InputType.YES)
    public void assertElementTextByLabel() {
        cc.Element = findInputElementByLabelTextByXpath();
        new Text(cc).assertElementTextEquals();//Create object for the necessary Class[Text as it has the assertElementTextEquals etc and call you desired method[assertElementTextEquals]
    }

    @Action(object = ObjectType.SELENIUM, 
    		desc ="Assert if [<Object>]'s Text adjacent to provided label element Contains [<Data>]", 
    		input =InputType.YES)
    public void assertElementTextContainsByLabel() {
        cc.Element = findInputElementByLabelTextByXpath();
        new Text(cc).assertElementTextContains();
    }

    private WebElement findInputElementByLabelTextByXpath() {
        return findInputElementByLabelTextByXpath(Element.getText());
    }

    private WebElement findInputElementByLabelTextByXpath(String text) {
        return Driver.findElement(By.xpath("//*[text()='" + text + "']/following::input[1]"));
    }
}
