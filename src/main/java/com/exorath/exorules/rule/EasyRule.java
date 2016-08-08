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
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

import java.util.concurrent.Callable;

/**
 * A simple implementation of rule.
 * Created by Toon on 8/7/2016.
 */
public abstract class EasyRule implements Rule {
    private Subject<Boolean, Boolean> observableEvaluation = new SerializedSubject<>(PublishSubject.create());
    private Boolean previous = null;

    @Override
    public boolean evaluate() {
        boolean doEvaluate = doEvaluate();
        //If there is no previous value or the previous value is not equal to the current evaluation, emmit the new evaluation and update previous
        if (previous == null || previous.booleanValue() != doEvaluate) {
            observableEvaluation.onNext(doEvaluate);
            previous = doEvaluate;
        }
        return doEvaluate;
    }

    public abstract boolean doEvaluate();

    @Override
    public Observable<Boolean> getObservableEvaluation() {
        return observableEvaluation;
    }

    public static EasyRule create(Callable<Boolean> evaluation){
        return new EasyRule() {
            @Override
            public boolean doEvaluate() {
                try {
                    return evaluation.call();
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };
    }
}
