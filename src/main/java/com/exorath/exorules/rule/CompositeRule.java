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

import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class representing a composite rule composed of a set of rules.
 * <p>
 * A composite rule is triggered if <strong>ALL</strong> conditions of its composing rules are satisfied.
 * <p>
 * Created by Toon on 8/7/2016.
 */
public class CompositeRule implements Rule {

    protected Map<Rule, Subscription> subsByRule = new HashMap<>();

    private Subject<Boolean, Boolean> observableEvaluation = new SerializedSubject<>(PublishSubject.create());

    @Override
    public Observable<Boolean> getObservableEvaluation() {
        return observableEvaluation;
    }

    @Override
    public boolean evaluate() {
        boolean evaluation = getEvaluation();
        observableEvaluation.onNext(evaluation);
        return evaluation;
    }

    private boolean getEvaluation() {
        for (Rule rule : subsByRule.keySet())
            if (!rule.evaluate())
                return false;
        return true;
    }

    public void addRule(Rule rule) {
        Subscription subscription = rule.getObservableEvaluation().subscribe(evaluation -> evaluate());
        subsByRule.put(rule, subscription);
    }

    public boolean containsRule(Rule rule) {
        return subsByRule.containsKey(rule);
    }

    public boolean removeRule(Rule rule) {
        if (!subsByRule.containsKey(rule))
            return false;
        if (subsByRule.get(rule) != null)
            subsByRule.get(rule).unsubscribe();
        subsByRule.remove(rule);
        return true;
    }
}
