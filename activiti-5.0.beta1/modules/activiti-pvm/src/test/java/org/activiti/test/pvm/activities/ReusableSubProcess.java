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

package org.activiti.test.pvm.activities;

import java.util.List;

import org.activiti.pvm.activity.ActivityExecution;
import org.activiti.pvm.activity.SubProcessActivityBehavior;
import org.activiti.pvm.delegate.DelegateExecution;
import org.activiti.pvm.process.PvmProcessDefinition;
import org.activiti.pvm.process.PvmTransition;
import org.activiti.pvm.runtime.PvmProcessInstance;


/**
 * @author Tom Baeyens
 */
public class ReusableSubProcess implements SubProcessActivityBehavior {

  PvmProcessDefinition processDefinition;
  
  public ReusableSubProcess(PvmProcessDefinition processDefinition) {
    this.processDefinition = processDefinition;
  }

  public void execute(ActivityExecution execution) throws Exception {
    PvmProcessInstance subProcessInstance = execution.createSubProcessInstance(processDefinition);
    
    // TODO set variables
    
    subProcessInstance.start();
  }

  public void completing(DelegateExecution execution, DelegateExecution subProcessInstance) throws Exception {
    // TODO extract information from the subprocess and inject it into the superprocess
  }

  public void completed(ActivityExecution execution) throws Exception {
    List<PvmTransition> outgoingTransitions = execution.getActivity().getOutgoingTransitions();
    execution.takeAll(outgoingTransitions, null);
  }
}
