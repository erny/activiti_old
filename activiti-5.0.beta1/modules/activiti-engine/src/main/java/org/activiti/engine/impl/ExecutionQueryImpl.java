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

import org.activiti.engine.Page;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ExecutionQuery;


/**
 * @author Joram Barrez
 */
public class ExecutionQueryImpl extends AbstractQuery<Execution> implements ExecutionQuery {

  protected boolean onlyProcessInstances;
  protected String processDefinitionId;
  protected String processDefinitionKey;
  protected String activityId;
  protected String executionId;
  
  protected CommandExecutor commandExecutor;
  
  public ExecutionQueryImpl() {
  }
  
  public ExecutionQueryImpl(CommandExecutor commandExecutor) {
    super(commandExecutor);
  }

  public boolean isProcessInstancesOnly() {
    return false;
  }

  public ExecutionQueryImpl processDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
    return this;
  }

  public ExecutionQueryImpl processDefinitionKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
    return this;
  }
  
  public ExecutionQueryImpl processInstanceId(String processInstanceId) {
    this.executionId = processInstanceId;
    this.onlyProcessInstances = true;
    return this;
  }
  
  public ExecutionQueryImpl executionId(String executionId) {
    this.executionId = executionId;
    return this;
  }
  
  public ExecutionQueryImpl activityId(String activityId) {
    this.activityId = activityId;
    return this;
  }
  
  public ExecutionQueryImpl orderAsc(String column) {
    super.addOrder(column, SORTORDER_ASC);
    return this;
  }
  
  public ExecutionQueryImpl orderDesc(String column) {
    super.addOrder(column, SORTORDER_DESC);
    return this;
  }
  
  public long executeCount(CommandContext commandContext) {
    return commandContext
      .getRuntimeSession()
      .findExecutionCountByQueryCriteria(this);
  }

  @SuppressWarnings("unchecked")
  public List<Execution> executeList(CommandContext commandContext, Page page) {
    return (List) commandContext
      .getRuntimeSession()
      .findExecutionsByQueryCriteria(this, page);
  }

  public boolean getOnlyProcessInstances() {
    return onlyProcessInstances;
  }
  
  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }
  
  public String getProcessDefinitionId() {
    return processDefinitionId;
  }
  
  public String getExecutionId() {
    return executionId;
  }
  
}
