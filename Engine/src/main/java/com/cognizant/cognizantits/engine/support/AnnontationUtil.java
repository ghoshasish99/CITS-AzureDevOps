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

import com.cognizant.cognizantits.engine.constants.FilePath;
import com.cognizant.cognizantits.engine.constants.SystemDefaults;
import eu.infomas.annotation.AnnotationDetector;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 */
public class AnnontationUtil {

    public static void detect(AnnotationDetector ANNOTATION_DETECTOR, String... packageNames) {
        try {
            String libLocation = "lib" + File.separator;
            File[] externalCommands = new File(libLocation + "commands").listFiles();
            if (externalCommands != null) {
                ANNOTATION_DETECTOR.detect(externalCommands);
            }
            if (SystemDefaults.getClassesFromJar.get()) {
                ANNOTATION_DETECTOR.detect(new File(FilePath.getEngineJarPath()));
            } else {
                ANNOTATION_DETECTOR.detect(packageNames);
            }
            ANNOTATION_DETECTOR.detect(new File(FilePath.getAppRoot(), "userdefined"));
        } catch (IOException ex) {
            Logger.getLogger(AnnontationUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
