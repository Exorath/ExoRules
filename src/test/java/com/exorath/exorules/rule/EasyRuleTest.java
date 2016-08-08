/*
 *    Copyright 2016 Exorath
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.exorath.exorules.rule;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Toon on 8/7/2016.
 */
public class EasyRuleTest {
    private EasyRule easyRuleTrue, easyRuleFalse;

    @Before
    public void setup() {
        easyRuleTrue = new EasyRule() {
            @Override
            public boolean doEvaluate() {
                return true;
            }
        };
        easyRuleFalse = new EasyRule() {
            @Override
            public boolean doEvaluate() {
                return false;
            }
        };
    }

    @Test
    public void evaluateCallsObservableSubscribersOnEvaluationChangeTest(){
        AtomicBoolean evaluation = new AtomicBoolean(false);
        AtomicBoolean called = new AtomicBoolean(false);
        EasyRule rule = new EasyRule() {
            @Override
            public boolean doEvaluate() {
                return evaluation.get();
            }
        };
        rule.getObservableEvaluation().subscribe(bool -> called.set(bool));

        evaluation.set(true);
        rule.evaluate();

        assertTrue(called.get());
    }

    @Test
    public void evaluateDoesNotCallsObservableSubscribersWhenEvaluationIsSameAsPreviousEvaluationTest(){
        AtomicBoolean called = new AtomicBoolean(false);

        easyRuleTrue.evaluate();
        easyRuleTrue.getObservableEvaluation().subscribe(bool -> called.set(bool));
        easyRuleTrue.evaluate();

        assertFalse(called.get());
    }

    @Test
    public void evaluateCallsObservableSubscribersWhenNoPreviousEvaluationExistsTest(){
        AtomicBoolean called = new AtomicBoolean(false);
        easyRuleTrue.getObservableEvaluation().subscribe(bool -> called.set(bool));
        easyRuleTrue.evaluate();

        assertTrue(called.get());
    }

}
