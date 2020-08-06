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
package com.cognizant.cognizantits.engine.support;

/**
 * 
 *
 */
public enum Flag {

    /*
    [offset]
    apply offset(from the image object properties) to the region found on screen
     */
    SET_OFFSET,
    /*
    [offset]
    use match only alternative to SET_OFFSET flag
     */
    REGION_ONLY,
    /*
    [coordinate]
    use static screen coordinates(from the image object properties)
     */
    SET_COORDINATES,
    /*
    [coordinate]
    use match only alternative/defaut to SET_COORDINATES flag
     */
    MATCH_ONLY,
    /*
    [searchmode]
    use image  search only (native image search)
     */
    IMAGE_ONLY,
    /*
    [searchmode]
    use image and text search
     */
    IMAGE_AND_TEXT,
    /*
    [searchmode]
    use text search only (ocr search on screen)
     */
    TEXT_ONLY
}
