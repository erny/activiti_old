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
package org.activiti.engine.impl.bpmn;

import org.activiti.engine.impl.el.ActivitiValueExpression;
import org.activiti.engine.impl.el.ExpressionManager;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.impl.task.TaskEntity;
import org.activiti.pvm.activity.ActivityExecution;

/**
 * activity implementation for the user task.
 * 
 * @author Joram Barrez
 */
public class UserTaskActivity extends TaskActivity {

  protected TaskDefinition taskDefinition;
  protected ExpressionManager expressionManager;

  public UserTaskActivity(ExpressionManager expressionManager, TaskDefinition taskDefinition) {
    this.expressionManager = expressionManager;
    this.taskDefinition = taskDefinition;
  }

  public void execute(ActivityExecution execution) throws Exception {
    TaskEntity task = TaskEntity.createAndInsert();
    task.setExecution(execution);
    task.setFormResourceKey(taskDefinition.getFormResourceKey());

    if (taskDefinition.getNameValueExpression() != null) {
      String name = (String) taskDefinition.getNameValueExpression().getValue(execution);
      task.setName(name);
    }

    if (taskDefinition.getDescriptionValueExpression() != null) {
      String description = (String) taskDefinition.getDescriptionValueExpression().getValue(execution);
      task.setDescription(description);
    }

    handleAssignments(task, execution);
  }

  public void signal(ActivityExecution execution, String signalName, Object signalData) throws Exception {
    leave(execution);
  }

  protected void handleAssignments(TaskEntity task, ActivityExecution execution) {
    if (taskDefinition.getAssigneeValueExpression() != null) {
      task.setAssignee((String) taskDefinition.getAssigneeValueExpression().getValue(execution));
    }

    if (!taskDefinition.getCandidateGroupIdValueExpressions().isEmpty()) {
      for (ActivitiValueExpression groupIdExpr : taskDefinition.getCandidateGroupIdValueExpressions()) {
        task.addCandidateGroup((String) groupIdExpr.getValue(execution));
      }
    }

    if (!taskDefinition.getCandidateUserIdValueExpressions().isEmpty()) {
      for (ActivitiValueExpression userIdExpr : taskDefinition.getCandidateUserIdValueExpressions()) {
        task.addCandidateUser((String) userIdExpr.getValue(execution));
      }
    }
  }


  
  // getters and setters //////////////////////////////////////////////////////
  
  public TaskDefinition getTaskDefinition() {
    return taskDefinition;
  }
  
  public ExpressionManager getExpressionManager() {
    return expressionManager;
  }

}
