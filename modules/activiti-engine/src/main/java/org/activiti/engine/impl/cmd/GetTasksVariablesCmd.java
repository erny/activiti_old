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

package org.activiti.engine.impl.cmd;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.VariableInstanceEntity;
import org.activiti.engine.impl.persistence.entity.VariableInstanceManager;

/**
 * @author Andrzej Dengusiak
 */
public class GetTasksVariablesCmd implements Command<Map<String, Map<String, Object>>>, Serializable {

  private static final long serialVersionUID = 1L;
  protected Collection<String> taskIds;
  protected Collection<String> variableNames;
  protected boolean isLocal;

  public GetTasksVariablesCmd(Collection<String> taskIds, Collection<String> variableNames, boolean isLocal) {
    this.taskIds = taskIds;
    this.variableNames = variableNames;
    this.isLocal = isLocal;
  }

  public Map<String, Map<String, Object>> execute(CommandContext commandContext) {
    if (taskIds == null) {
      throw new ActivitiException("taskIds is null");
    }
    Map<String, Map<String, Object>> taskVariablesByTaskIdMap = new HashMap<String, Map<String, Object>>();
    if (taskIds.isEmpty()) {
      return taskVariablesByTaskIdMap;
    }
    VariableInstanceManager variableInstanceManager = Context.getCommandContext().getVariableInstanceManager();
    Map<String, List<VariableInstanceEntity>> tasksVariableInstancesMap = new HashMap<String, List<VariableInstanceEntity>>();
    if (isLocal) {
      tasksVariableInstancesMap = variableInstanceManager.findVariableInstancesLocalByTaskIds(taskIds, variableNames);
    } else {
      tasksVariableInstancesMap = variableInstanceManager.findVariableInstancesByTaskIds(taskIds, variableNames);
    }
    for (String taskId : tasksVariableInstancesMap.keySet()) {
      Map<String, Object> taskVariables = new HashMap<String, Object>();
      for (VariableInstanceEntity variableInstance : tasksVariableInstancesMap.get(taskId)) {
        taskVariables.put(variableInstance.getName(), variableInstance.getValue());
      }
      taskVariablesByTaskIdMap.put(taskId, taskVariables);
    }
    return taskVariablesByTaskIdMap;
  }
}
