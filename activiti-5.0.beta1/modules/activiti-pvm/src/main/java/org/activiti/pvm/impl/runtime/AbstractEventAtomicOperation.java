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

package org.activiti.pvm.impl.runtime;

import java.util.List;

import org.activiti.pvm.event.EventListener;
import org.activiti.pvm.impl.process.ScopeImpl;


/**
 * @author Tom Baeyens
 */
public abstract class AbstractEventAtomicOperation implements AtomicOperation {

  public void execute(ExecutionImpl execution) {
    ScopeImpl scope = getScope(execution);
    List<EventListener> eventListeners = scope.getEventListeners(getEventName());
    int eventListenerIndex = execution.getEventListenerIndex();
    
    if (eventListeners.size()>eventListenerIndex) {
      execution.setEventName(getEventName());
      execution.setEventSource(scope);
      EventListener listener = eventListeners.get(eventListenerIndex);
      listener.notify(execution);
      execution.setEventListenerIndex(eventListenerIndex+1);
      execution.performOperation(this);

    } else {
      execution.setEventListenerIndex(0);
      execution.setEventName(null);
      execution.setEventSource(null);
      
      eventNotificationsCompleted(execution);
    }
  }

  protected abstract ScopeImpl getScope(ExecutionImpl execution);
  protected abstract String getEventName();
  protected abstract void eventNotificationsCompleted(ExecutionImpl execution);
}
