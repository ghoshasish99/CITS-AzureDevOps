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

import com.cognizant.cognizantits.engine.core.CommandControl;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;

public class MethodExecutor {
    
    private static final Map<String, MethodHandle> CACHE = new HashMap<>();
    private static final Map<MethodHandle, Class<?>> CACHE_CLASS = new HashMap<>();

    public static void init() {
        CACHE.clear();
        CACHE_CLASS.clear();
        Discovery.discoverCommands();
    }
    
    public static boolean executeMethod(String mName, CommandControl inst) throws Throwable {
        MethodHandle handle = getHandle(mName);
        if (handle != null) {
            handle.invoke(CACHE_CLASS.get(handle).getConstructor(
                    CommandControl.class).newInstance(inst));
            
            return true;
        }
        return false;
    }
    
    private static MethodHandle makeHandle(String mName) {
        for (Class<?> c : Discovery.getClassList()) {
            MethodHandle handle = getHandle(c, mName);
            if (handle != null) {
                CACHE.put(mName, handle);
                CACHE_CLASS.put(handle, c);
                return handle;
            }
        }
        return null;
    }
    
    private static MethodHandle getHandle(Class<?> c, String mName) {
        try {
            return MethodHandles.lookup().findVirtual(c, mName,
                    MethodType.methodType(void.class
                    ));
        } catch (Exception ex) {
            return null;
        }
    }
    
    private static boolean cached(String mName) {
        return CACHE.containsKey(mName) && CACHE_CLASS.containsKey(CACHE.get(mName));
    }
    
    private static MethodHandle getHandle(String mName) {
        if (cached(mName)) {
            return CACHE.get(mName);
        } else {
            return makeHandle(mName);
        }
    }
}
