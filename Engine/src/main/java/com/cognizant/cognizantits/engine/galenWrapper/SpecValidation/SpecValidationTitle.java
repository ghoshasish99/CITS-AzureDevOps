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

import com.galenframework.validation.PageValidation;
import com.galenframework.validation.ValidationErrorException;
import com.galenframework.validation.ValidationObject;
import com.galenframework.validation.ValidationResult;
import static java.util.Arrays.asList;

/**
 *
 * 
 */
public class SpecValidationTitle extends SpecValidationTextWrapper<SpecTitle> {

    @Override
    public ValidationResult check(PageValidation pageValidation, String objectName, SpecTitle spec) throws ValidationErrorException {

        String realText = pageValidation.getPage().getTitle();
        if (realText == null) {
            realText = "";
        }
        realText = applyOperationsTo(realText, spec.getOperations());
        checkValue(spec, objectName, realText, "Title", null);

        return new ValidationResult(spec,asList(new ValidationObject(null, objectName)));
    }
}
