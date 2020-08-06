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
package com.cognizant.cognizantits.engine.execution.exception;

public class ForcedException extends RuntimeException {

    public String ErrorName;
    public String ErrorDescription;

    public ForcedException(String errorName, String errorDescription) {
        this.ErrorName = errorName;
        this.ErrorDescription = errorDescription;

    }

    public ForcedException(String errorName, Throwable ex) {
        super(ex);
        this.ErrorName = errorName;
        this.ErrorDescription = ex.getMessage();
    }

    public ForcedException(String errorDescription) {
        this.ErrorName = "ForcedException";
        this.ErrorDescription = errorDescription;
    }

    @Override
    public String getMessage() {
        return ErrorDescription;
    }

}
