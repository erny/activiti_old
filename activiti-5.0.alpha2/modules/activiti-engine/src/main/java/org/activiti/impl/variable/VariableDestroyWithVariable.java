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
package org.activiti.impl.variable;

import org.activiti.ActivitiException;
import org.activiti.impl.execution.ExecutionImpl;
import org.activiti.pvm.Listener;
import org.activiti.pvm.ListenerExecution;


/**
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class VariableDestroyWithVariable implements Listener {

  protected String sourceVariableName;
  protected String destinationVariableName;
  
  public VariableDestroyWithVariable(String sourceVariableName, String destinationVariableName) {
    this.sourceVariableName = sourceVariableName;
    this.destinationVariableName = destinationVariableName;
  }

  public void notify(ListenerExecution execution) {
    ExecutionImpl innerScope = (ExecutionImpl) execution;
    ExecutionImpl outerScope = innerScope.getParent();
    
    if (innerScope.hasVariable(sourceVariableName)) {
      Object value = innerScope.getVariable(sourceVariableName);
      outerScope.setVariable(destinationVariableName, value);      
    } else {
      throw new ActivitiException("Couldn't destroy variable " 
              + sourceVariableName + ", since it does not exist");
    }
  }

}
