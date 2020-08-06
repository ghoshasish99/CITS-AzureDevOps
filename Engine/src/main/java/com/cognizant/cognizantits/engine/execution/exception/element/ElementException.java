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
package com.cognizant.cognizantits.engine.execution.exception.element;

/**
 *
 * 
 */
public class ElementException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public enum ExceptionType {

        Element_Not_Found,
        Element_Not_Visible,
        Element_Not_Enabled,
        Element_Not_Selected,
        Not_Found_on_Screen,
        Empty_Group;

        @Override
        public String toString() {
            switch (this) {
                case Element_Not_Found:
                    return "Seems Like the Element [{{Name}}] is Not Present/Found in the page Try Adding wait or heal it";
                case Element_Not_Visible:
                    return "Seems Like the Element [{{Name}}] is Not Visible or hidden at the moment";
                case Element_Not_Enabled:
                    return "Seems Like the Element [{{Name}}] is Not Enabled";
                case Element_Not_Selected:
                    return "Seems Like the Element [{{Name}}] is Not Selected";
                case Not_Found_on_Screen:
                    return " not Found on the Screen. ";
                case Empty_Group:
                    return " -- Object Group is Empty. ";
            }
            return "";
        }
    };

    public ElementException(ExceptionType type,
            String objectName) {
        super(type.toString().replace("{{Name}}", objectName));
    }
}
