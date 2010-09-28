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
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricActivityInstanceQueryProperty;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;


/**
 * @author Tom Baeyens
 */
public class HistoricActivityInstanceQueryImpl extends AbstractQuery<HistoricActivityInstance> implements HistoricActivityInstanceQuery {
  
  protected String processInstanceId;
  protected String executionId;
  protected String processDefinitionId;
  protected String activityId;
  protected String activityName;
  protected String activityType;
  protected String assignee;
  protected boolean onlyOpen;
  protected HistoricActivityInstanceQueryProperty orderProperty;
  protected String orderBy;

  public HistoricActivityInstanceQueryImpl() {
  }
  
  public HistoricActivityInstanceQueryImpl(CommandExecutor commandExecutor) {
    super(commandExecutor);
  }

  @Override
  public long executeCount(CommandContext commandContext) {
    return commandContext
      .getHistorySession()
      .findHistoricActivityInstanceCountByQueryCriteria(this);
  }

  @Override
  public List<HistoricActivityInstance> executeList(CommandContext commandContext, Page page) {
    return commandContext
      .getHistorySession()
      .findHistoricActivityInstancesByQueryCriteria(this, page);
  }
  

  public HistoricActivityInstanceQueryImpl processInstanceId(String processInstanceId) {
    this.processInstanceId = processInstanceId;
    return this;
  }

  public HistoricActivityInstanceQueryImpl executionId(String executionId) {
    this.executionId = executionId;
    return this;
  }

  public HistoricActivityInstanceQueryImpl processDefinitionId(String processDefinitionId) {
    this.processDefinitionId = processDefinitionId;
    return this;
  }

  public HistoricActivityInstanceQueryImpl activityId(String activityId) {
    this.activityId = activityId;
    return this;
  }

  public HistoricActivityInstanceQueryImpl activityName(String activityName) {
    this.activityName = activityName;
    return this;
  }

  public HistoricActivityInstanceQueryImpl activityType(String activityType) {
    this.activityType = activityType;
    return this;
  }
  
  public HistoricActivityInstanceQueryImpl assignee(String assignee) {
    this.assignee = assignee;
    return this;
  }
  
  public HistoricActivityInstanceQueryImpl onlyOpen() {
    this.onlyOpen = true;
    return this;
  }


  // ordering /////////////////////////////////////////////////////////////////

  public HistoricActivityInstanceQueryImpl asc() {
    return direction(Direction.ASCENDING);
  }

  public HistoricActivityInstanceQueryImpl desc() {
    return direction(Direction.DESCENDING);
  }

  public HistoricActivityInstanceQueryImpl direction(Direction direction) {
    if (orderProperty==null) {
      throw new ActivitiException("you should call any of the orderBy methods first before specifying a direction");
    }
    addOrder(direction.getName(), orderProperty.getName());
    orderProperty = null;
    return this;
  }

  public HistoricActivityInstanceQueryImpl orderBy(HistoricActivityInstanceQueryProperty property) {
    this.orderProperty = property;
    return this;
  }

  public HistoricActivityInstanceQueryImpl orderByDuration() {
    orderBy(HistoricActivityInstanceQueryProperty.DURATION);
    return this;
  }

  public HistoricActivityInstanceQueryImpl orderByEnd() {
    orderBy(HistoricActivityInstanceQueryProperty.END);
    return this;
  }

  public HistoricActivityInstanceQueryImpl orderByExecutionId() {
    orderBy(HistoricActivityInstanceQueryProperty.EXECUTION_ID);
    return this;
  }

  public HistoricActivityInstanceQueryImpl orderById() {
    orderBy(HistoricActivityInstanceQueryProperty.ID);
    return this;
  }

  public HistoricActivityInstanceQueryImpl orderByProcessDefinitionId() {
    orderBy(HistoricActivityInstanceQueryProperty.PROCESS_DEFINITION_ID);
    return this;
  }

  public HistoricActivityInstanceQueryImpl orderByProcessInstanceId() {
    orderBy(HistoricActivityInstanceQueryProperty.PROCESS_INSTANCE_ID);
    return this;
  }

  public HistoricActivityInstanceQueryImpl orderByStart() {
    orderBy(HistoricActivityInstanceQueryProperty.START);
    return this;
  }

  // getters and setters //////////////////////////////////////////////////////
  
  public String getProcessInstanceId() {
    return processInstanceId;
  }
  public String getExecutionId() {
    return executionId;
  }
  public String getProcessDefinitionId() {
    return processDefinitionId;
  }
  public String getActivityId() {
    return activityId;
  }
  public String getOrderBy() {
    return orderBy;
  }
  public String getActivityName() {
    return activityName;
  }
  public String getActivityType() {
    return activityType;
  }
  public boolean isOnlyOpen() {
    return onlyOpen;
  }
  public String getAssignee() {
    return assignee;
  }
}
