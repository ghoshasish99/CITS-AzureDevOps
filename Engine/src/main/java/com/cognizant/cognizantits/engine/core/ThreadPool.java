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
package com.cognizant.cognizantits.engine.core;

import com.cognizant.cognizantits.engine.drivers.WebDriverFactory.Browser;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * 
 */
public class ThreadPool extends ThreadPoolExecutor {

    public Boolean doSelectiveThreading = false;

    public ThreadPool(int threadCount, long keepAliveTime, boolean isGridMode) {
        super(threadCount, threadCount, keepAliveTime, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        doSelectiveThreading = threadCount > 1 && !isGridMode;
    }

    Map<Runnable, Browser> browserPool = new HashMap<>();
    Queue<Runnable> IEList = new LinkedList<>();

    @Override
    protected synchronized void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (doSelectiveThreading) {
            if (browserPool.containsKey(r)) {
                browserPool.remove(r);
            }
            if (IEList.isEmpty()) {
                shutdown();
                System.out.println("Shutting Down Thread as there is no IE Browser to do SelectiveThreading");
            } else if (!browserPool.containsValue(Browser.IE)) {
                if (getActiveCount() < getCorePoolSize()) {
                    Runnable ieRun = IEList.remove();
                    execute(ieRun);
                    browserPool.put(ieRun, Browser.IE);
                }
            }
        }
    }

    public synchronized void execute(Runnable command, Browser browserName) {
        if (doSelectiveThreading) {
            if (browserPool.containsValue(browserName)
                    && browserName.equals(Browser.IE)) {
                IEList.add(command);
            } else {
                browserPool.put(command, browserName);
                execute(command);
            }
        } else {
            execute(command);
        }
    }

    public void shutdownExecution() {
        if (!doSelectiveThreading) {
            System.out.println("Shutting Down Thread as there is no need for SelectiveThreading");
            shutdown();
        }
    }

}
