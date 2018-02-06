/*
 * Copyright 2018 Rundeck, Inc. (http://rundeck.com)
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

package rundeck.quartzjobs

import org.quartz.Job
import org.quartz.JobDataMap
import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.rundeck.core.triggers.Action
import org.rundeck.core.triggers.Condition
import org.rundeck.core.triggers.TriggerActionInvoker
import rundeck.services.RDTriggerContext

class RDTriggerConditionJob implements Job {
    @Override
    void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.error("RDTriggerConditionJob fired")
        def data = jobExecutionContext.getMergedJobDataMap()

        String triggerId = data['triggerId']
        RDTriggerContext context = data['context']
        Condition condition = data['condition']
        Action action = data['action']
        TriggerActionInvoker invoker = data['invoker']

        def invocationData =  condition.conditionData + [fireTime: jobExecutionContext.fireTime]
        log.error("RDTriggerConditionJob: invoking with: $invocationData")
        invoker.triggerConditionMet(triggerId, context,invocationData)
    }
}