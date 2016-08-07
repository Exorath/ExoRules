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

import org.junit.Test;
import rx.Observable;


import static org.junit.Assert.assertTrue;

/**
 * Created by Toon on 8/7/2016.
 */
public class RuleTest {
    @Test
    public void isInterfaceTest() {
        assertTrue(Rule.class.isInterface());
    }

    @Test
    public void hasEvaluateMethodTest() throws NoSuchMethodException {
        assertTrue(Rule.class.getMethod("evaluate").getReturnType().equals(boolean.class));
    }
    @Test
    public void hasGetObservableEvaluationMethodTest() throws NoSuchMethodException {
        assertTrue(Rule.class.getMethod("getObservableEvaluation").getReturnType().equals(Observable.class));
    }
}
