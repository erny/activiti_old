/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.engine.bpmn;

import org.activiti.engine.impl.bpmn.BpmnActivityBehavior;
import org.activiti.pvm.activity.ActivityBehavior;
import org.activiti.pvm.activity.ActivityExecution;
import org.activiti.pvm.delegate.DelegateExecution;
import org.activiti.pvm.event.EventListener;
import org.activiti.pvm.event.EventListenerExecution;

/**
 * Convience class that should be used when a Java delegation in a BPMN 2.0
 * process is required (eg. to call custom business logic).
 * 
 * Since this class implements both the {@link ActivityBehavior} and
 * {@link EventListener} class, the logic can be used for both service tasks and
 * event listeners.
 * 
 * This class does not allow to influence the control flow. It follows the
 * default BPMN 2.0 behavior of taking every outgoing sequence flow (which has a
 * condition that evaluates to true if there is a condition defined) If you are
 * in need of influencing the flow in your process, use the
 * {@link ActivityBehavior} instead.
 * 
 * @author Joram Barrez
 */
public abstract class BpmnJavaDelegation extends BpmnActivityBehavior implements ActivityBehavior, EventListener {
  
  public void execute(ActivityExecution execution) throws Exception {
    execute((DelegateExecution) execution);
    performDefaultOutgoingBehavior(execution);
  }
  
  public void notify(EventListenerExecution execution) {
    execute((DelegateExecution) execution);
  }
  
  public abstract void execute(DelegateExecution execution);

}
