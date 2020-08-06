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

/**
 *
 * 
 */
public class SpecAttribute extends SpecText {

    private String attributeName;

    public SpecAttribute(String attributeName, Type type, String text) {
        super(type, text);
        this.attributeName = attributeName;
    }

    public String getAtributeName() {
        return attributeName;
    }

    public void setAtributeName(String attributeName) {
        this.attributeName = attributeName;
    }

}
