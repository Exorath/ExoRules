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

/**
 * Created by Toon on 8/7/2016.
 */
public interface Rule {
    /**
     * Rule conditions abstraction : this method encapsulates the rule's conditions.
     * @return true if the rule passed
     */
    boolean evaluate();

    /**
     * Rules may change state out of their own. They may emit their new state to the subscribers of this observable.
     * @return an observable that emits a
     */
    Observable<Boolean> getObservableEvaluation();

}
