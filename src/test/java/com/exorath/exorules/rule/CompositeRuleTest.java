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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by Toon on 8/7/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class CompositeRuleTest {
    @Mock
    private Rule trueRule1, trueRule2, falseRule1;


    private CompositeRule<Rule> compositeRule;

    @Before
    public void setup() {
        Rule[] rules = new Rule[]{trueRule1, trueRule2, falseRule1};

        when(trueRule1.evaluate()).thenReturn(true);
        when(trueRule2.evaluate()).thenReturn(true);
        when(falseRule1.evaluate()).thenReturn(false);

        for(Rule rule : rules)
            when(rule.getObservableEvaluation()).thenReturn(PublishSubject.create());
        compositeRule = new CompositeRule<>();
    }

    @Test
    public void implementsRuleTest() {
        assertTrue(Rule.class.isAssignableFrom(CompositeRule.class));
    }

    @Test
    public void evaluatesToTrueByDefaultTest() {
        assertTrue(compositeRule.evaluate());
    }

    @Test
    public void addRuleContainsTest() {
        compositeRule.addRule(trueRule1);
        assertTrue(compositeRule.containsRule(trueRule1));
    }

    @Test
    public void ruleNoContainsTest() {
        assertFalse(compositeRule.containsRule(trueRule1));
    }
    @Test
    public void addRulesContainsTest() {
        compositeRule.addRule(trueRule1);
        compositeRule.addRule(trueRule2);
        assertTrue(compositeRule.containsRule(trueRule1) && compositeRule.containsRule(trueRule2));
    }

    @Test
    public void removeRuleNoContainsTest() {
        compositeRule.addRule(trueRule1);
        compositeRule.removeRule(trueRule1);
        assertFalse(compositeRule.containsRule(trueRule1));
    }

    @Test
    public void removeRuleTrueOnSuccessTest() {
        compositeRule.addRule(trueRule1);
        assertTrue(compositeRule.removeRule(trueRule1));
    }

    @Test
    public void removeRuleFalseOnFailureTest() {
        assertFalse(compositeRule.removeRule(trueRule1));
    }

    @Test
    public void evaluatesToFalseWithRuleTest() {
        compositeRule.addRule(falseRule1);
        assertFalse(compositeRule.evaluate());
    }

    @Test
    public void evaluatesToFalseWithRulesTest() {
        compositeRule.addRule(trueRule1);
        compositeRule.addRule(falseRule1);
        assertFalse(compositeRule.evaluate());
    }

    @Test
    public void evaluatesToTrueWithRuleTest() {
        compositeRule.addRule(trueRule1);
        assertTrue(compositeRule.evaluate());
    }

    @Test
    public void evaluatesToTrueWithRulesTest() {
        compositeRule.addRule(trueRule1);
        compositeRule.addRule(trueRule2);
        assertTrue(compositeRule.evaluate());
    }
    @Test
    public void callsRulesTest() {
        compositeRule.addRule(trueRule1);
        compositeRule.addRule(trueRule2);

        compositeRule.evaluate();

        verify(trueRule1).evaluate();
        verify(trueRule2).evaluate();
    }

    @Test
    public void doesNotCallRemovedRuleTest() {
        compositeRule.addRule(trueRule1);
        compositeRule.addRule(trueRule2);
        compositeRule.removeRule(trueRule1);


        compositeRule.evaluate();

        verify(trueRule1, never()).evaluate();
        verify(trueRule2).evaluate();
    }

    @Test
    public void observableEmitsTrueWhenRulesChangeToTrueTest(){
        Rule rule = mock(Rule.class);

        Subject<Boolean, Boolean> observable = PublishSubject.create();

        when(rule.evaluate()).thenReturn(false);
        when(rule.getObservableEvaluation()).thenReturn(observable);

        compositeRule.addRule(rule);
        AtomicBoolean evaluation = new AtomicBoolean(false);
        compositeRule.getObservableEvaluation().subscribe(bool -> evaluation.set(bool));
        when(rule.evaluate()).thenReturn(true);
        observable.onNext(true);

        assertTrue(evaluation.get());
    }

    @Test
    public void observableEmitsFalseWhenRulesChangeToTrueAndFalseTest(){
        Rule rule = mock(Rule.class);

        Subject<Boolean, Boolean> observable = PublishSubject.create();

        when(rule.evaluate()).thenReturn(false);
        when(rule.getObservableEvaluation()).thenReturn(observable);

        compositeRule.addRule(rule);
        compositeRule.addRule(falseRule1);

        AtomicBoolean evaluation = new AtomicBoolean(false);
        compositeRule.getObservableEvaluation().subscribe(bool -> evaluation.set(bool));
        when(rule.evaluate()).thenReturn(true);
        observable.onNext(true);

        assertFalse(evaluation.get());
    }

    @Test
    public void observableEmitsFalseWhenRuleChangesToFalse(){
        Rule rule = mock(Rule.class);

        Subject<Boolean, Boolean> observable = PublishSubject.create();

        when(rule.evaluate()).thenReturn(true);
        when(rule.getObservableEvaluation()).thenReturn(observable);

        compositeRule.addRule(rule);

        AtomicBoolean evaluation = new AtomicBoolean(false);
        compositeRule.getObservableEvaluation().subscribe(bool -> evaluation.set(bool));
        when(rule.evaluate()).thenReturn(false);
        observable.onNext(false);

        assertFalse(evaluation.get());
    }

    @Test
    public void getRulesNotNullTest(){
        assertTrue(compositeRule.getRules() != null);
    }

    @Test
    public void getRulesEmptyByDefaultTest(){
        assertTrue(compositeRule.getRules().isEmpty());
    }

    @Test
    public void getRulesContainsAddedRuleTest(){
        compositeRule.addRule(trueRule1);
        assertTrue(compositeRule.getRules().contains(trueRule1));
    }

    @Test
    public void getRulesDoesNotContainRemovedRuleTest(){
        compositeRule.addRule(trueRule1);
        compositeRule.removeRule(trueRule1);
        assertFalse(compositeRule.getRules().contains(trueRule1));
    }
}
