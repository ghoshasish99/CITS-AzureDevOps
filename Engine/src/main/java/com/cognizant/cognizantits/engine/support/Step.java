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

import com.cognizant.cognizantits.datalib.component.TestStep;
import com.cognizant.cognizantits.engine.execution.run.TestCaseRunner;
import com.cognizant.cognizantits.engine.reporting.util.DateTimeUtils;

public class Step {

    public int StepNum;
    public String ObjectName;
    public String Action;
    public String Input;
    public String Data;
    public String Condition;
    public String Reference;
    public boolean BreakPoint = false;
    public String Description;

    private int subIter = 1;
    private TestCaseRunner runner;

    public Step(int n) {
        this.StepNum = n;
    }

    public Step(TestStep ts, TestCaseRunner runner) {
        this.runner = runner;
        BreakPoint = false;
        ObjectName = ts.getObject();
        Action = ts.getAction();
        Input = ts.getInput();
        Data = ts.getInput();
        Condition = ts.getCondition();
        Reference = ts.getReference();
        Description = ts.getDescription();
        if (ts.getTag().matches(".*[0-9]+")) {
            // strip break point and other special characters and parse step
            StepNum = Integer.parseInt(ts.getTag().replaceAll("[^0-9]", ""));
        }
        BreakPoint = ts.hasBreakPoint();
    }

    public Step printStep() {
    	if (Action.contains("post"))
    		System.out.println(
                    String.format("Step:%-4s| Object: %s | Action: %s | Input: %s | Conditon: %s | @%s",
                            new Object[]{StepNum, ObjectName, Action, "Payload Below", Condition, DateTimeUtils.DateTimeNow()}));
    	else
	        System.out.println(
	                String.format("Step:%-4s| Object: %s | Action: %s | Input: %s | Conditon: %s | @%s",
	                        new Object[]{StepNum, ObjectName, Action, Input, Condition, DateTimeUtils.DateTimeNow()}));
        return this;
    }

    public static Step create(int num) {
        Step s = new Step(num);
        s.ObjectName = "Browser";
        s.Condition = s.Input = s.Data = "";
        return s;
    }

    public static Step create(int num, int subIter, TestCaseRunner runner) {
        Step s = create(num);
        s.subIter = subIter;
        s.runner = runner;
        s.Input = "@" + subIter;
        return s;
    }

    public static Step create() {
        return create(-1);
    }

    public Step object(String name, String page) {
        this.ObjectName = name;
        this.Reference = page;
        return this;
    }

    public Step object(String name) {
        return object(name, "");
    }

    public Step execute(String com) {
        return execute("Execute", com);
    }

    public Step execute(Class<?> c) {
        return execute(c.getName().split("\\."));
    }

    private Step execute(String[] re) {
        return execute("ExecuteClass", re[1] + ":" + re[2]);
    }

    private Step execute(String obj, String com) {
        this.ObjectName = obj;
        this.Reference = "";
        this.Action = com;
        return this;
    }

    public Step executeClass(String com) {
        return execute("ExecuteClass", com);
    }

    public Step action(String name) {
        this.Action = name;
        return this;
    }

    public Step condition(String condition) {
        this.Condition = condition;
        return this;
    }

    public Step input(String inp) {
        this.Input = inp;
        this.Data = inp;
        return this;
    }

    public Step run(TestCaseRunner runner) {
        return run(runner, subIter);
    }

    public Step run() {
        return run(runner, subIter);
    }

    public Step run(TestCaseRunner runner, int sunIter) {
        runner.runStep(this, sunIter);
        return this;
    }

    public TestStep toTestStep() {
        TestStep s = new TestStep(runner.getTestCase());
        s.setAction(Action);
        s.setCondition(Condition);
        s.setReference(Reference);
        s.setTag(StepNum + "");
        s.setInput(Input);
        s.setDescription(Description);
        s.setObject(ObjectName);
        return s;
    }

}
