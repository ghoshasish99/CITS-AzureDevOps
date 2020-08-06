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

public class UnCaughtException extends RuntimeException {

    public String errorName = "UnCaughtException";

    public UnCaughtException(String errorDescription) {
        super(errorDescription);
    }

    public UnCaughtException(Throwable ex) {
        super(ex);
    }

    public UnCaughtException(String errorName, String errorDescription) {
        super(errorDescription);
        this.errorName = errorName;
    }

    public String getName() {
        return errorName;
    }
}
