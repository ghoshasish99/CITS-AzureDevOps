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

import com.cognizant.cognizantits.datalib.testdata.view.TestDataView;
import com.cognizant.cognizantits.engine.constants.ObjectProperty;
import com.cognizant.cognizantits.engine.constants.SystemDefaults;
import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.execution.exception.UnCaughtException;
import com.cognizant.cognizantits.engine.support.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
public class SampleScript extends General {
    // create your own function

    public SampleScript(CommandControl cc) {
        super(cc);
    }

    public void prinThis(String argumentShouldNotBeGiven
    /**
     * No argument should be specifed Then only will be executed[show in
     * action column]
     */
    ) {

        //To do any operations before and/or after execution of eachStep add your code to
        //functions beforeStepExecution / afterStepExecution in
        //Annotation.java inside com.cognizant.core package
        //To do any operation after execution [execution is finished] add your code to
        //afterReportComplete function in
        //SummaryReport.java inside com.cognizant.reporting package
        // do ur action
        try {
            Element.click();//Object in ObjectName is resolved as WebElement and assigned to this variable[Element]

            System.out.println(ObjectName + "ObjectName used in ObjectColumn in the currentTestStep");
            System.out.println(Description + "Description used in DescriptionColumn in the currentTestStep");
            System.out.println(Action + "Action/Command used in ActionColumn in the currentTestStep");
            System.out.println(Input + "Input used in InputColumn in the currentTestStep");
            System.out.println(Data + "Resolved Input used in InputColumn in the currentTestStep");
            System.out.println(Reference + "Reference/PageName used in ReferenceColumn in the currentTestStep");

            System.out.println(getCurrentBrowserName() + "To get the current browserName");

            //If you stored some dynamic value in a varaible[%newVar%] you can get the value from the varaible using
            String value = getVar("%newVar%");
            System.out.println(value);

            //If you want to access the userdefined data created from options pane you can use it in two ways
            //One
            value = getVar("%userdefinedVar%");
            value = getVar("userdefinedVar");//This also will work
            //Two
            value = getUserDefinedData("userdefinedVar");

            System.out.println(value);

            //If you want to store some value in a varaible[%dyanmicVar%] you can store the value into a varaible using
            //Scope is for Current Testcase
            addVar("%dyanmicVar%", "Value to be Stored");

            //Scope is for All
            addGlobalVar("%dyanmicVar%", "Value to be Stored");

            // Using Inbuilt findMethod
            AObject.findElement(ObjectName, Reference);//To find the current step's object
            AObject.findElements(ObjectName, Reference);//To find the current step's object

            // to access the object value pass ObjectNameand PageName as inputs
            // ObjectName=p
            // PageName=Yahoo
            WebElement element = AObject.findElement("p", "Yahoo");
            List<WebElement> elementList = AObject.findElements("p", "Yahoo");

            // -----Using Conditioned FindMethod
            element = AObject.findElement("p", "Yahoo", ObjectProperty.Id);
            elementList = AObject.findElements("p", "Yahoo", ObjectProperty.ClassName);

            //-----Using Own findMethod
            element = Driver.findElement(By.id(Data));

            // using this element u can perform selenium operations
            element.sendKeys("Normal");

            element.sendKeys(Data);

            //To get a property of an object from ObjectRepository
            String prop = AObject.getWebObject("pageName", "objectName").getId();

            prop = AObject.getWebObject(Reference, ObjectName).getAttributeByName(ObjectProperty.Id);//to get current step object's id property

            System.out.println(prop);

            System.out.println("No of elements" + elementList.size());

            //To get a TestData
            TestDataView testDataView = userData.getTestData("sheetName");

            //To get all the columns
            testDataView.columns();

            //To get all the subiteration values for current Scenario/TestCase/Iteration
            testDataView.records();
            
            // to access the data from DataSheets pass TestDataname and
            // Column name as inputs
            // Don't pass GlobalData as inputsheet
            //                              SheetName,Columnname  
            String input = userData.getData("Sample", "Data1");

            //To get values from specified Iteration and subiteration
            input = userData.getData("Sample", "Data1", "1", "1");

            //To get values from specified Scenario, Testcase, Iteration and subiteration
            input = userData.getData("Sample", "Data1", "scenario", "testcase", "1", "1");

            element.sendKeys(input);

            //To write values into DataSheet  Don't pass GlobalData as inputsheet
            //                SheetName,Columnname,value
            userData.putData("Sample", "Data1", "kk");

            //to write values for specified Iteration and subiteration
            userData.putData("Sample", "Data1", "kk", "1", "1");

            //to write values for specified Scenario, Testcase, Iteration and subiteration
            userData.putData("Sample", "Data1", "kk", "scenario", "testcase", "1", "1");

            //To display in Report
            Report.updateTestLog("Userdefined Action ", "Operation Done successfully", Status.PASS);

            //To display in Report with custom html tags
            Report.updateTestLog("Userdefined Action ", "#CTAG <b>Operation Done successfully</b>", Status.PASS);

            //To get the current Iteration
            userData.getIteration();

            //To get the current SubIteration
            userData.getSubIteration();

            //To get the current Scenario
            userData.getScenario();

            //To get the current Testcase
            userData.getTestCase();

            //To get the current BrowserName
            System.out.println(getCurrentBrowserName());

            // to stop the current iteration if u want to... based on condition
            Boolean something = false;
            if (something) {
                SystemDefaults.stopCurrentIteration.set(true);
                SystemDefaults.stopExecution.set(true);//Stop the execution
            }

            // To use inbuilt functions 
            //simple way
            //make sure you set the Data,Element and other varaibles
            new Basic(getCommander()).Click();

            //Old school
            executeMethod(element, "Click");
            executeMethod("open", "@http://something");
            executeMethod("open", input);
            executeMethod(element, "Set", input);

            //To execute Other Testcases//testWeb:search
            //               scearioname,testcasename,subiteration 
            executeTestCase("OnlineShopping", "BuyProduct", 2);
            //               scearioname,testcasename
            executeTestCase("OnlineShopping", "BuyProduct");

            // -----------------------//
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.OFF, null, ex);
        }

    }

    public void handleCondition(String argumentShouldNotBeGiven
    /**
     * No argument should be specifed Then only will be executed[show in
     * action column]
     */
    ) throws UnCaughtException {
        //Getting object from the object repository
        WebElement element = AObject.findElement("ObjectName", "PageName");
        //Putting condition on object
        if (element.isDisplayed()) {
            //Calling another test case if the condition is matched
            //Pass the Scenario name,Test case name and sub-iteration index
            executeTestCase("testscenario1", "cancelTicket", 1);
            Report.updateTestLog("Userdefined Action ", "inside reusable", Status.PASS);
            //If needed you can break the test case also by calling existing functions
            executeMethod("StopBrowser");
            //
        } else {
            Report.updateTestLog("Userdefined Action ", "switch to origional", Status.DONE);
        }
    }

    private void checkAndSwitchToWindow() {
        String currentWindowHandle = Driver.getWindowHandle();
        Boolean found = false;
        List<String> handles = new ArrayList<>(Driver.getWindowHandles());
        AObject.setWaitTime(4);//in secs
        for (String handle : handles) {
            Driver.switchTo().window(handle);
            if (AObject.findElement("ObjName", "PageName") != null) {
                found = true;
                break;
            }
        }
        AObject.resetWaitTime();
        if (found) {
            Report.updateTestLog(Action, "Switched to Window by finding the element", Status.PASS);
        } else {
            Report.updateTestLog(Action, "Element not found in any window", Status.FAIL);
            Driver.switchTo().window(currentWindowHandle);
        }
    }
}
