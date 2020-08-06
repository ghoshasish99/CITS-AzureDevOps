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
import com.paulhammant.ngwebdriver.ByAngular;
import com.paulhammant.ngwebdriver.ByAngularRepeaterCell;
import com.paulhammant.ngwebdriver.ByAngularRepeaterRow;
import com.paulhammant.ngwebdriver.NgWebDriver;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

/**
 *
 *
 */
public class AngularFindBy {

    @SProperty(name = "ng-bind")
    public By getByBind(String binding) {
        return ByAngular.binding(binding);
    }

    @SProperty(name = "ng-buttonText")
    public By getByButtonText(String buttonText) {
        return ByAngular.buttonText(buttonText);
    }

    @SProperty(name = "ng-cssContainingText")
    public By getBycssContainingText(String cssContainingText) {
        String[] vals = cssContainingText.split("##");
        return ByAngular.cssContainingText(vals[0], vals[1]);
    }

    @SProperty(name = "ng-exactBind")
    public By getByWxactBinding(String exactBinding) {
        return ByAngular.exactBinding(exactBinding);
    }

    @SProperty(name = "ng-model")
    public By getByModel(String model) {
        return ByAngular.model(model);
    }

    @SProperty(name = "ng-options")
    public By getByOptions(String options) {
        return ByAngular.options(options);
    }

    @SProperty(name = "ng-partialButtonText")
    public By getByPartialButtonText(String buttonText) {
        return ByAngular.partialButtonText(buttonText);
    }

    @SProperty(name = "ng-repeat")
    public By getByRepeater(String repeater) {
        return ByAngular.repeater(repeater);
    }

    @SProperty(name = "ng-repeat-row")
    public By getByRepeaterRow(String repeaterRow) {
        String repeater = repeaterRow.split("###")[0];
        int row = Integer.valueOf(repeaterRow.split("###")[1]);
        return new ByAngularRepeaterRow(NgWebDriver.DEFAULT_ROOT_SELECTOR, repeater, false, row) {
            @Override
            public List<WebElement> findElements(SearchContext searchContext) {
                List<WebElement> elements = new ArrayList<>();
                try {
                    elements.add(this.findElement(searchContext));
                } catch (Exception ex) {

                }
                return elements;
            }
        };
    }

    @SProperty(name = "ng-repeat-column")
    public By getByRepeaterColumn(String repeaterCol) {
        String repeater = repeaterCol.split("###")[0];
        String column = repeaterCol.split("###")[1];
        return ByAngular.repeater(repeater).column(column);
    }

    @SProperty(name = "ng-repeat-cell")
    public By getByRepeaterCell(String repeaterVal) {
        String repeater = repeaterVal.split("###")[0];
        int row = Integer.valueOf(repeaterVal.split("###")[1]);
        String column = repeaterVal.split("###")[2];
        return new ByAngularRepeaterCell(NgWebDriver.DEFAULT_ROOT_SELECTOR, repeater, false, row, column) {
            @Override
            public List<WebElement> findElements(SearchContext searchContext) {
                List<WebElement> elements = new ArrayList<>();
                try {
                    elements.add(this.findElement(searchContext));
                } catch (Exception ex) {

                }
                return elements;
            }
        };
    }

    @SProperty(name = "ng-exactRepeat")
    public By getByExactRepeater(String exactRepeater) {
        return ByAngular.exactRepeater(exactRepeater);
    }

    @SProperty(name = "ng-exactRepeat-row")
    public By getByExactRepeaterRow(String repeaterRow) {
        String repeater = repeaterRow.split("###")[0];
        int row = Integer.valueOf(repeaterRow.split("###")[1]);
        return new ByAngularRepeaterRow(NgWebDriver.DEFAULT_ROOT_SELECTOR, repeater, true, row) {
            @Override
            public List<WebElement> findElements(SearchContext searchContext) {
                List<WebElement> elements = new ArrayList<>();
                try {
                    elements.add(this.findElement(searchContext));
                } catch (Exception ex) {

                }
                return elements;
            }
        };
    }

    @SProperty(name = "ng-exactRepeat-column")
    public By getByExactRepeaterColumn(String repeaterCol) {
        String repeater = repeaterCol.split("###")[0];
        String column = repeaterCol.split("###")[1];
        return ByAngular.exactRepeater(repeater).column(column);
    }

    @SProperty(name = "ng-exactRepeat-cell")
    public By getByExactRepeaterCell(String repeaterVal) {
        String repeater = repeaterVal.split("###")[0];
        int row = Integer.valueOf(repeaterVal.split("###")[1]);
        String column = repeaterVal.split("###")[2];
        return new ByAngularRepeaterCell(NgWebDriver.DEFAULT_ROOT_SELECTOR, repeater, true, row, column) {
            @Override
            public List<WebElement> findElements(SearchContext searchContext) {
                List<WebElement> elements = new ArrayList<>();
                try {
                    elements.add(this.findElement(searchContext));
                } catch (Exception ex) {

                }
                return elements;
            }
        };
    }

}
