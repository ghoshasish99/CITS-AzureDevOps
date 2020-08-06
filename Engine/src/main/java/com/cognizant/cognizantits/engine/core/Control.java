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

import com.cognizant.cognizantits.datalib.component.Project;
import com.cognizant.cognizantits.datalib.testdata.TestDataFactory;
import com.cognizant.cognizantits.engine.cli.LookUp;
import com.cognizant.cognizantits.engine.commands.database.General;
import com.cognizant.cognizantits.engine.constants.FilePath;
import com.cognizant.cognizantits.engine.constants.SystemDefaults;
import com.cognizant.cognizantits.engine.drivers.SeleniumDriver;
import com.cognizant.cognizantits.engine.drivers.WebDriverFactory;
import com.cognizant.cognizantits.engine.execution.exception.UnCaughtException;
import com.cognizant.cognizantits.engine.execution.run.ProjectRunner;
import com.cognizant.cognizantits.engine.mail.Mailer;
import com.cognizant.cognizantits.engine.reporting.SummaryReport;
import com.cognizant.cognizantits.engine.reporting.impl.ConsoleReport;
import com.cognizant.cognizantits.engine.reporting.util.DateTimeUtils;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.methodInf.MethodInfoManager;
import com.cognizant.cognizantits.engine.support.reflect.MethodExecutor;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Control {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS:%1$tmS %1$tz [%4$-4s] %2$s:%5$s%6$s%n");
    }
    private static final Logger LOG = Logger.getLogger(Control.class.getName());

    public static SummaryReport ReportManager;
    public Boolean executionFinished = false;
    public static ProjectRunner exe;

    private static SeleniumDriver seleniumDriver;

    private static void start() {
        do {
            Control control = new Control();
            control.startRun();
            control.resetAll();
        } while (exe.retryExecution());
        ConsoleReport.reset();
    }

    public static void call(Project project) throws UnCaughtException {
        RunManager.init();
        exe = ProjectRunner.load(project);
        start();
    }

    public static void call() throws UnCaughtException {
        RunManager.init();
        if (exe == null) {
            exe = ProjectRunner.load(RunManager.getGlobalSettings().getProjectPath());
        }
        start();
    }

    public static Project getCurrentProject() {
        if (exe != null) {
            return exe.getProject();
        }
        return null;
    }

    void resetAll() {
        exe.afterExecution(ReportManager.isPassed());
        SystemDefaults.resetAll();
        SummaryReport.reset();
        ReportManager = null;
        RunManager.clear();
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (!executionFinished) {
                    endExecution();
                    if (General.dbconnection != null) {
                        try {
                            General.dbconnection.close();
                        } catch (SQLException ex) {
                            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    ConsoleReport.reset();
                }
            }
        });
    }

    private void initRun() throws Exception {
        executionFinished = false;
        addShutDownHook();
        FilePath.initDateTime();
        MethodExecutor.init();
        ConsoleReport.init();
        SystemDefaults.printSystemInfo();
        System.out.println("Run Started on " + new Date().toString());
        System.out.println("Loading Browser Profile");
        WebDriverFactory.initDriverLocation(exe.getProject().getProjectSettings());
        System.out.println("Loading RunManager");
        RunManager.loadRunManager();
        System.out.println("Initializing Report");
        ReportManager = new SummaryReport();
    }

    private void startRun() {
        try {
            initRun();
            TMIntegration.init(ReportManager);
            ReportManager.createReport(DateTimeUtils.DateTimeNow(), RunManager.queue().size());
            ThreadPool threadPool = new ThreadPool(
                    exe.getExecSettings().getRunSettings().getThreadCount(),
                    exe.getExecSettings().getRunSettings().getExecutionTimeOut(),
                    exe.getExecSettings().getRunSettings().isGridExecution());
            while (!RunManager.queue().isEmpty() && !SystemDefaults.stopExecution.get()) {
                Task t = null;
                try {
                    RunContext currentContext = RunManager.queue().remove();
                    t = new Task(currentContext);
                    threadPool.execute(t, currentContext.Browser);
                } catch (Exception ex) {
                    Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
                    if (t != null) {
                        t.seleniumDriver.closeBrowser();
                    }
                }
            }
            threadPool.shutdownExecution();

            if (threadPool.awaitTermination(exe.getExecSettings()
                    .getRunSettings().getExecutionTimeOut(), TimeUnit.MINUTES)) {
            } else {
                Logger.getLogger(Control.class.getName()).log(Level.SEVERE, "Execution stopped due to Timeout [{0}]",
                        exe.getExecSettings().getRunSettings().getExecutionTimeOut());
                threadPool.shutdownNow();
                SystemDefaults.stopExecution.set(true);
            }

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            if (ReportManager != null) {
                SystemDefaults.reportComplete.set(false);
                ReportManager.updateTestCaseResults("[Unknown Error]", "---", ex.getMessage(), "", "Unknown", "Unknown",
                        Status.FAIL, "");
            }
        } finally {
            while (SystemDefaults.reportComplete.get()) {
                SystemDefaults.pollWait();
            }
            endExecution();
        }
    }

    static SeleniumDriver getSeDriver() {
        return seleniumDriver;
    }

    static void setSeDriver(SeleniumDriver seDriver) {
        seleniumDriver = seDriver;
    }

    private void endExecution() {
        executionFinished = true;
        System.out.println("Run Finished on " + new Date().toString());
        try {
            if (ReportManager != null) {
                ReportManager.finalizeReport();
                if (ReportManager.sync != null) {
                    ReportManager.sync.disConnect();
                }
                Mailer.send();
            }
            if (seleniumDriver != null) {
                seleniumDriver.closeBrowser();
            }

        } catch (Exception ex) {
           // Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void initDeps() {
        TestDataFactory.load();
        MethodInfoManager.load();
    }

    public static void main(String[] args) throws UnCaughtException {
        initDeps();
        if (args != null && args.length > 0) {
            LookUp.exe(args);
        } else {
            call();
        }
    }

}
