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

import com.cognizant.cognizantits.engine.core.CommandControl;
import com.cognizant.cognizantits.engine.execution.exception.element.ElementException;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.Action;
import com.cognizant.cognizantits.engine.support.methodInf.InputType;
import com.cognizant.cognizantits.engine.support.methodInf.ObjectType;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class Dropdown extends General {

    String selector = "li a";

    enum SelectRange {

        Single, Multiple, All;

        public Boolean isMultiple() {
            return !this.equals(Single);
        }
    };

    enum SelectType {

        Select,
        DeSelect;

        @Override
        public String toString() {
            if (this.equals(Select)) {
                return "selected";
            }
            return "deselected";
        }
    }

    enum SelectBy {

        Index, Text, Value
    };

    public Dropdown(CommandControl cc) {
        super(cc);
    }

    @Action(object = ObjectType.SELENIUM, desc = "selecting value [<Data>] from unordered list .", input = InputType.YES)
    public void selectValueFromUnorderedList() {
        selectFromUnorderedList(SelectBy.Text);
    }

    @Action(object = ObjectType.SELENIUM, desc = "selecting value by index [<Data>] from unordered list .", input = InputType.YES)
    public void selectIndexFromUnorderedList() {
        selectFromUnorderedList(SelectBy.Index);
    }

    private void selectFromUnorderedList(SelectBy selectBy) {
        if (elementPresent()) {
            if (Condition != null && !Condition.trim().isEmpty()) {
                selector = Condition;
            }
            List<WebElement> options = Element.findElements(By.cssSelector(selector));
            if (options.isEmpty()) {
                options = Element.findElements(By.tagName("li"));
            }
            Boolean flag = false;
            if (selectBy.equals(SelectBy.Index)) {
                int index = Integer.parseInt(Data);
                if (index < options.size()) {
                    options.get(index).click();
                    flag = true;
                }
            } else {
                for (WebElement option : options) {
                    if (Data.equals(option.getText().trim())) {
                        option.click();
                        flag = true;
                        break;
                    }
                }
            }
            if (flag) {
                Report.updateTestLog(Action, Data + " is Selected", Status.PASS);
            } else {
                Report.updateTestLog(Action, "Element[" + ObjectName + "] Not Visible/Available", Status.FAIL);
            }
        } else {
            throw new ElementException(ElementException.ExceptionType.Element_Not_Found, ObjectName);
        }
    }

    @Action(object = ObjectType.SELENIUM, desc = "Select item in [<Object>] which has text: [<Data>]", input = InputType.YES)
    public void selectByVisibleText() {
        select(SelectType.Select, SelectRange.Single, SelectBy.Text);
    }

    /**
     * **************************************
     */
    @Action(object = ObjectType.SELENIUM, desc = "Select item in [<Object>] if Data exists", input = InputType.YES)
    public void SelectByVisibleTextIfDataPresent() {
        if (!Data.isEmpty()) {
            if (Element != null) {
                select(SelectType.Select, SelectRange.Single, SelectBy.Text);
            } else {
                Report.updateTestLog(Action, "Element [" + ObjectName + "] not Exists", Status.DONE);
            }
        } else {
            Report.updateTestLog(Action, "Data does not exist", Status.DONE);
        }
    }

    /**
     * **************************************
     */

    @Action(object = ObjectType.SELENIUM, desc = "Select item in [<Object>] which has the value: [<Data>]", input = InputType.YES)
    public void selectByValue() {
        select(SelectType.Select, SelectRange.Single, SelectBy.Value);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Select all options from a select Element [<Object>]", input = InputType.YES)
    public void selectByIndex() {
        select(SelectType.Select, SelectRange.Single, SelectBy.Index);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Select items [<Data>] of [<Object>] by visible Text", input = InputType.YES)
    public void selectMultipleByText() {
        select(SelectType.Select, SelectRange.Multiple, SelectBy.Text);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Select items [<Data>] of [<Object>] by value", input = InputType.YES)
    public void selectMultipleByValue() {
        select(SelectType.Select, SelectRange.Multiple, SelectBy.Value);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Select items [<Data>] of [<Object>] by index", input = InputType.YES)
    public void selectMultipleByIndex() {
        select(SelectType.Select, SelectRange.Multiple, SelectBy.Index);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Select all options from a select Element [<Object>]")
    public void selectAll() {
        select(SelectType.Select, SelectRange.All, null);
    }

    @Action(object = ObjectType.SELENIUM,
            desc = "Deselect item in [<Object>] which has text: [<Data>]", input = InputType.YES)
    public void deSelectByVisibleText() {
        select(SelectType.DeSelect, SelectRange.Single, SelectBy.Text);
    }

    @Action(object = ObjectType.SELENIUM,
            desc = "Deselect item in [<Object>] which has value: [<Data>]",
            input = InputType.YES)
    public void deSelectByValue() {
        select(SelectType.DeSelect, SelectRange.Single, SelectBy.Value);
    }

    @Action(object = ObjectType.SELENIUM,
            desc = "Deselect  item in [<Object>] which has index: [<Data>]", input = InputType.YES)
    public void deSelectByIndex() {
        select(SelectType.DeSelect, SelectRange.Single, SelectBy.Index);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Deselect items [<Data>] of [<Object>] by visible Text", input = InputType.YES)
    public void deSelectMultipleByText() {
        select(SelectType.DeSelect, SelectRange.Multiple, SelectBy.Text);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Deselect items [<Data>] of [<Object>] by value", input = InputType.YES)
    public void deSelectMultipleByValue() {
        select(SelectType.DeSelect, SelectRange.Multiple, SelectBy.Value);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Deselect items [<Data>] of [<Object>] by index", input = InputType.YES)
    public void deSelectMultipleByIndex() {
        select(SelectType.DeSelect, SelectRange.Multiple, SelectBy.Index);
    }

    @Action(object = ObjectType.SELENIUM, desc = "Deselect all items in  [<Object>]")
    public void deSelectAll() {
        select(SelectType.DeSelect, SelectRange.All, null);
    }

    @Action(object = ObjectType.SELENIUM,
            desc = "Assert if the  select list [<Object>] contains [<Data>]",
            input = InputType.YES)
    public void assertSelectContains() {
        if (elementPresent()) {
            Boolean isPresent = false;
            Select select = new Select(Element);
            for (WebElement option : select.getOptions()) {
                if (option.getText().trim().equals(Data)) {
                    isPresent = true;
                    break;
                }
            }
            if (isPresent) {
                Report.updateTestLog(Action, ObjectName + " Contains the Option " + Data, Status.DONE);
            } else {
                Report.updateTestLog(Action, ObjectName + " doesn't Contains the Option " + Data, Status.DEBUG);
            }
        } else {
            throw new ElementException(ElementException.ExceptionType.Element_Not_Found, ObjectName);
        }
    }

    private void select(SelectType selectType, SelectRange selectRange, SelectBy selectBy) {
        if (elementPresent()) {
            Boolean flag = true;
            if (selectRange.isMultiple()) {
                flag = new Select(Element).isMultiple();
            }
            if (flag) {
                switch (selectRange) {
                    case Single:
                        selectSingle(selectType, selectBy);
                        break;
                    case Multiple:
                        selectMultiple(selectType, selectBy);
                        break;
                    case All:
                        selectAll(selectType);
                        break;
                }
                Report.updateTestLog(Action, "Item/s '" + Data
                        + "' is/are " + selectType.toString() + " from list " + ObjectName, Status.DONE);
            } else {
                Report.updateTestLog(Action, ObjectName + " is not a multiple Select Element ", Status.FAIL);
            }
        } else {
            throw new ElementException(ElementException.ExceptionType.Element_Not_Found, ObjectName);
        }
    }

    private void selectSingle(SelectType selectType, SelectBy selectBy) {
        switch (selectType) {
            case Select:
                select(selectBy);
                break;
            case DeSelect:
                deSelect(selectBy);
                break;
        }

    }

    private void selectMultiple(SelectType selectType, SelectBy selectBy) {
        Select select = new Select(Element);
        String[] values = Data.split(",");
        for (String value : values) {
            switch (selectType) {
                case Select:
                    select(select, value, selectBy);
                    break;
                case DeSelect:
                    deSelect(select, value, selectBy);
                    break;
            }
        }

    }

    private void select(SelectBy selectBy) {
        select(new Select(Element), Data, selectBy);
    }

    private void select(Select select, String Data, SelectBy selectBy) {
        switch (selectBy) {
            case Index:
                select.selectByIndex(Integer.parseInt(Data));
                break;
            case Text:
                select.selectByVisibleText(Data);
                break;
            case Value:
                select.selectByValue(Data);
                break;
        }

    }

    private void deSelect(SelectBy selectBy) {
        deSelect(new Select(Element), Data, selectBy);
    }

    private void deSelect(Select select, String Data, SelectBy selectBy) {
        switch (selectBy) {
            case Index:
                select.deselectByIndex(Integer.parseInt(Data));
                break;
            case Text:
                select.deselectByVisibleText(Data);
                break;
            case Value:
                select.deselectByValue(Data);
                break;
        }

    }

    private void selectAll(SelectType selectType) {
        switch (selectType) {
            case Select:
                Select select = new Select(Element);
                for (int i = 0; i < select.getOptions().size(); i++) {
                    select.selectByIndex(i);
                }
                break;
            case DeSelect:
                new Select(Element).deselectAll();
                break;
        }

    }

}
