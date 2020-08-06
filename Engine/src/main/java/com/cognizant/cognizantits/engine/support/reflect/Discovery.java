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
package com.cognizant.cognizantits.engine.support.reflect;

import com.cognizant.cognizantits.engine.constants.FilePath;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Discovery {

    private static final Logger LOG = Logger.getLogger(Discovery.class.getName());

    private static List<Class<?>> classList;

    public static String[] packages;

    public static List<Class<?>> getClassesForPackage() {
        ArrayList<Class<?>> clazz = new ArrayList<>();
        clazz.addAll(getClassesFromPackageList());
        clazz.addAll(getClassesFromUserDefinedPackage());
        return clazz;
    }

    public static ArrayList<Class<?>> getClassesFromPackageList() {
        ArrayList<Class<?>> clazz = new ArrayList<>();
        try {
            clazz.addAll(ClassFinder.getClasses(packages));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return clazz;
    }

    public static List<Class<?>> getClassesFromUserDefinedPackage() {
        List<Class<?>> classes = new ArrayList<>();
        try {
            File directory = new File(FilePath.getAppRoot(), "userdefined");
            if (directory.exists()) {
                URL[] urls = new URL[]{directory.toURI().toURL()};
                String[] files = directory.list();
                for (String file : files) {
                    if (file.endsWith(".class")) {
                        String className = file.substring(0, file.length() - 6);
                        try {
                            try (URLClassLoader uCl = new URLClassLoader(urls);) {
                                classes.add(uCl.loadClass(className));
                            } catch (IOException e) {
                                LOG.log(Level.SEVERE, null, e);
                            }
                        } catch (ClassNotFoundException e) {
                            LOG.log(Level.SEVERE, null, e);
                            throw new RuntimeException("ClassNotFoundException loading " + className);
                        }
                    }
                }
            }
        } catch (MalformedURLException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return classes;
    }

    public static void discoverCommands() {
        loadPackageFromProperties();
        classList = getClassesForPackage();
    }

    private static void loadPackageFromProperties() {
        try {
            packages = null;
            Properties prop = new Properties();
            File file = new File("Configuration", "package.properties");
            if (file.exists()) {
                prop.load(new FileInputStream(file));
                if (prop.containsKey("actions")) {
                    packages = prop.getProperty("actions").split(",");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Discovery.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (packages == null) {
            packages = new String[]{"com.cognizant.cognizantits.engine.commands"};
        }
    }

    public static List<Class<?>> getClassList() {
        return classList;
    }

    public static Class<?> getClassByName(String name) {
        for (Class<?> class1 : classList) {
            if (class1.getName().equals(name)) {
                return class1;
            }
        }
        return null;
    }

    public static List<String> getUserMethods() {
        List<String> userMethods = new ArrayList<>();
        List<Class<?>> clazzes = getClassesFromUserDefinedPackage();
        for (Class<?> classs : clazzes) {
            Method[] method = classs.getMethods();
            for (Method m : method) {
                if (m.getParameterTypes().length == 0
                        && m.getReturnType() == void.class && !Modifier.isFinal(m.getModifiers())) {
                    userMethods.add(m.getName());
                }
            }

        }
        return userMethods;
    }

}
