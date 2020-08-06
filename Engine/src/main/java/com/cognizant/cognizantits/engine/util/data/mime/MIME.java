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
package com.cognizant.cognizantits.engine.util.data.mime;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * 
 */
public class MIME {

    private final Properties map = new Properties();

    private static MIME mime;

    private MIME() throws IOException {
        map.load(MIME.class.
                getResourceAsStream(
                        "/util/mime/mime.types.properties"));

    }

    public static String getType(File f) {
        try {
            return getType(f.getName());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public static String getType(String fileName) {
        try {
            return getTypeFor(FilenameUtils.getExtension(fileName));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    private static String getTypeFor(String ext) {
        try {
            if (mime == null) {
                mime = new MIME();
            }
            return mime.map.getProperty(ext);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }
    private static final Logger LOG = Logger.getLogger(MIME.class.getName());

}
