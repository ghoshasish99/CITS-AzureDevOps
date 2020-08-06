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
package com.cognizant.cognizantits.engine.execution.run;

import com.cognizant.cognizantits.datalib.component.Scenario;
import com.cognizant.cognizantits.datalib.component.TestCase;
import com.cognizant.cognizantits.datalib.component.TestStep;
import com.cognizant.cognizantits.engine.constants.SystemDefaults;
import com.cognizant.cognizantits.engine.execution.data.DataProcessor;
import com.cognizant.cognizantits.engine.execution.data.Parameter;
import com.cognizant.cognizantits.engine.execution.exception.DriverClosedException;
import com.cognizant.cognizantits.engine.execution.exception.ForcedException;
import com.cognizant.cognizantits.engine.execution.exception.UnKnownError;
import com.cognizant.cognizantits.engine.execution.exception.data.DataNotFoundException;
import com.cognizant.cognizantits.engine.execution.exception.element.ElementException;
import com.cognizant.cognizantits.engine.support.Status;
import com.cognizant.cognizantits.engine.support.Step;
import com.cognizant.cognizantits.engine.support.reflect.MethodExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.lang.String.format;

public class TestStepRunner {

    private static final Logger LOG = Logger.getLogger(TestStepRunner.class.getName());

    private final TestStep testStep;
    private final Parameter parameter;
    private Step step;

    public TestStepRunner(TestStep testStep, Parameter parameter) {
        this.parameter = parameter;
        this.testStep = testStep;
    }

    public TestStepRunner() {
        this.parameter = null;
        this.testStep = null;
    }

    public void run(TestCaseRunner context) throws DataNotFoundException, DriverClosedException {
        if (this.parameter != null && this.testStep != null) {
            if (context.executor().isDebugExe()) {
                checkForDebug();
            }
            step = new Step(testStep, context);
            context.getReport().updateStepDetails(step);
            switch (getStep().getObject()) {
                case "Execute":
                    execute(context);
                    break;
                default:
                    executeStep(context);
                    break;
            }
        } else {
            throw new RuntimeException("Not enough data to run a step");
        }
    }

    private void checkForDebug() {
        SystemDefaults.nextStepflag.set(true);
        SystemDefaults.pauseExecution.set(getStep().hasBreakPoint()
                || SystemDefaults.pauseExecution.get());
        while (SystemDefaults.pauseExecution.get() && SystemDefaults.nextStepflag.get()
                && !SystemDefaults.stopExecution.get()) {
            SystemDefaults.pollWait();
        }
    }

    private int getSubIterationFromInput(TestCaseRunner context) {
        if (!getStep().getInput().isEmpty()) {
            try {
                return Integer.valueOf(DataProcessor.resolve(getStep().getInput(), context,
                        String.valueOf(parameter.getSubIteration())));
            } catch (Exception ex) {
                System.err.println("Unable to resolve subIteration for reusable!!");
                LOG.log(Level.WARNING, ex.getMessage(), ex);
                return 1;
            }
        }
        return parameter.getSubIteration();
    }

    private TestStep getStep() {
        return testStep;
    }

    /**
     * parse the Execute action to reusable testcase and executes in the current
     * testcase context
     *
     * @param context - current testcase context to run the reusable
     * @throws DataNotFoundException, ForcedException
     */
    private void execute(TestCaseRunner context) throws DataNotFoundException, ForcedException {
        if (getStep().isReusableStep()) {
            String[] rData = getStep().getReusableData();
            String scenario = rData[0];
            String testcase = rData[1];
            Scenario scn = context.project().getScenarioByName(scenario);
            if (scn != null) {
                TestCase stc = scn.getTestCaseByName(testcase);
                if (stc != null) {
                    executeTestCase(context, stc);
                    return;
                } else {
                    throw new ForcedException(format("reusable testcase [//%s/%s] not found",
                            scenario, testcase));
                }
            } else {
                throw new ForcedException(format("scenario [%s] not found", scenario));
            }
        }
        throw new ForcedException(
                format("invalid reusable [%s], expected format [scenario:reusable]",
                        getStep().getAction()));
    }

    private void executeTestCase(TestCaseRunner context, TestCase stc) throws DataNotFoundException {
        try {
            parameter.setSubIteration(getSubIterationFromInput(context));
            context.getReport().startComponent(getStep().getAction(), getStep().getDescription());
            new TestCaseRunner(context, stc, parameter).run();
        } finally {
            context.getReport().endComponent(getStep().getAction());
        }
    }

    private void executeStep(TestCaseRunner context) throws DataNotFoundException, DriverClosedException {
        try {
            Annotation ann = new Annotation(context.getControl());
            ann.beforeStepExecution();
            executeStep(context, step, parameter);
            ann.afterStepExecution();
        } catch (DataNotFoundException | DriverClosedException
                | ForcedException | ElementException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new UnKnownError(ex);
        }
    }

    private void executeStep(TestCaseRunner context, Step step, Parameter parameter)
            throws Throwable {
        step.printStep();
        context.getControl().sync(step, String.valueOf(parameter.getSubIteration()));
        executeAction(context, step.Action);
    }

    public void executeAction(TestCaseRunner context, String action) throws Throwable {
        if (!MethodExecutor.executeMethod(action, context.getControl())) {
            System.out.println("[ERROR][Could not find Action:" + action + "]");
            context.getReport().updateTestLog(action, "[Could not find Action]",
                    Status.DEBUG);
        }
    }

}
