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
package com.cognizant.cognizantits.engine.galenWrapper.SpecValidation;

import com.galenframework.specs.SpecText;
import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationErrorException;
import com.galenframework.validation.ValidationResult;
import com.galenframework.validation.specs.SpecValidationText;
import com.galenframework.validation.specs.TextOperation;
import java.util.List;

/**
 *
 * 
 * @param <T>
 */
public abstract class SpecValidationTextWrapper<T extends SpecText> extends SpecValidationText<T> {

    @Override
    abstract public ValidationResult check(PageValidation pageValidation, String objectName, T spec) throws ValidationErrorException;

    public String applyOperationsTo(String text, List<String> operations) {
        if (operations != null) {
            for (String operation : operations) {
                text = TextOperation.find(operation).apply(text);
            }
        }
        return text;
    }
}
