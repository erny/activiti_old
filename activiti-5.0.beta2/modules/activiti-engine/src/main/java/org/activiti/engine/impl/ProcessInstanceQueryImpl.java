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

package org.activiti.engine.impl;

import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.runtime.ProcessInstanceQueryProperty;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;


/**
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class ProcessInstanceQueryImpl extends AbstractQuery<ProcessInstance> implements ProcessInstanceQuery {

  protected String executionId;
  protected String processDefinitionId;
  protected String processDefinitionKey;
  protected ProcessInstanceQueryProperty orderProperty;
  protected String superProcessInstanceId;
  protected String subProcessInstanceId;
  
  // Unused, see dynamic query
  protected String activityId; 
  
  protected CommandExecutor commandExecutor;
  
  public ProcessInstanceQueryImpl() {
  }
  
  public ProcessInstanceQueryImpl(CommandExecutor commandExecutor) {
    super(commandExecutor);
  }

  public ProcessInstanceQueryImpl processInstanceId(String processInstanceId) {
    if (processInstanceId == null) {
      throw new ActivitiException("Process instance id is null");
    }
    this.executionId = processInstanceId;
    return this;
  }
  
  public ProcessInstanceQueryImpl processDefinitionId(String processDefinitionId) {
    if (processDefinitionId == null) {
      throw new ActivitiException("Process definition id is null");
    }
    this.processDefinitionId = processDefinitionId;
    return this;
  }

  public ProcessInstanceQueryImpl processDefinitionKey(String processDefinitionKey) {
    if (processDefinitionKey == null) {
      throw new ActivitiException("Process definition key is null");
    }
    this.processDefinitionKey = processDefinitionKey;
    return this;
  }
  
  public ProcessInstanceQuery superProcessInstance(String superProcessInstanceId) {
    if (superProcessInstanceId == null) {
      throw new ActivitiException("Super process instance id is null");
    }
    this.superProcessInstanceId = superProcessInstanceId;
    return this;
  }
  
  public ProcessInstanceQuery subProcessInstance(String subProcessInstanceId) {
    if (subProcessInstanceId == null) {
      throw new ActivitiException("Sub process instance id is null");
    }
    this.subProcessInstanceId = subProcessInstanceId;
    return this;
  }

  //ordering //////////////////////////////////////////////

  public ProcessInstanceQuery orderByProcessInstanceId() {
    this.orderProperty = ProcessInstanceQueryProperty.PROCESS_INSTANCE_ID;
    return this;
  }
  
  public ProcessInstanceQuery orderByProcessDefinitionId() {
    this.orderProperty = ProcessInstanceQueryProperty.PROCESS_DEFINITION_ID;
    return this;
  }
  
  public ProcessInstanceQuery orderByProcessDefinitionKey() {
    this.orderProperty = ProcessInstanceQueryProperty.PROCESS_DEFINITION_KEY;
    return this;
  }
  
  public ProcessInstanceQuery orderBy(ProcessInstanceQueryProperty property) {
    this.orderProperty = property;
    return this;
  }
  
  public ProcessInstanceQuery asc() {
    return direction(Direction.ASCENDING);
  }
  
  public ProcessInstanceQuery desc() {
    return direction(Direction.DESCENDING);
  }
  
  public ProcessInstanceQuery direction(Direction direction) {
    if (orderProperty==null) {
      throw new ActivitiException("You should call any of the orderBy methods first before specifying a direction");
    }
    addOrder(orderProperty.getName(), direction.getName());
    orderProperty = null;
    return this;
  }
  
  //results /////////////////////////////////////////////////////////////////
  
  public long executeCount(CommandContext commandContext) {
    checkQueryOk();
    return commandContext
      .getRuntimeSession()
      .findProcessInstanceCountByQueryCriteria(this);
  }

  public List<ProcessInstance> executeList(CommandContext commandContext, Page page) {
    checkQueryOk();
    return commandContext
      .getRuntimeSession()
      .findProcessInstanceByQueryCriteria(this, page);
  }
  
  protected void checkQueryOk() {
    if (orderProperty != null) {
      throw new ActivitiException("Invalid query: call asc() or desc() after using orderByXX()");
    }
  }

  
  //getters /////////////////////////////////////////////////////////////////
  
  public boolean getOnlyProcessInstances() {
    return true; // See dynamic query in runtime.mapping.xml
  }
  public String getProcessInstanceId() {
    return executionId;
  }
  public String getProcessDefinitionId() {
    return processDefinitionId;
  }
  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }
  public String getActivityId() {
    return null; // Unused, see dynamic query
  }
  public String getSuperProcessInstanceId() {
    return superProcessInstanceId;
  }
  public String getSubProcessInstanceId() {
    return subProcessInstanceId;
  }
  
}
