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

package org.activiti.engine.impl.persistence.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.persistence.AbstractManager;

/**
 * @author Tom Baeyens
 */
public class VariableInstanceManager extends AbstractManager {

  public void deleteVariableInstance(VariableInstanceEntity variableInstance) {
    getDbSqlSession().delete(VariableInstanceEntity.class, variableInstance.getId());

    String byteArrayValueId = variableInstance.getByteArrayValueId();
    if (byteArrayValueId != null) {
      // the next apparently useless line is probably to ensure consistency in
      // the DbSqlSession
      // cache, but should be checked and docced here (or removed if it turns
      // out to be unnecessary)
      // @see also HistoricVariableUpdateEntity
      variableInstance.getByteArrayValue();
      getDbSqlSession().delete(ByteArrayEntity.class, byteArrayValueId);
    }
  }

  @SuppressWarnings("unchecked")
  public List<VariableInstanceEntity> findVariableInstancesByTaskId(String taskId) {
    return getDbSqlSession().selectList("selectVariablesByTaskId", taskId);
  }

  @SuppressWarnings("unchecked")
  public List<VariableInstanceEntity> findVariableInstancesByExecutionId(String executionId) {
    return getDbSqlSession().selectList("selectVariablesByExecutionId", executionId);
  }

  public void deleteVariableInstanceByTask(TaskEntity task) {
    Map<String, VariableInstanceEntity> variableInstances = task.getVariableInstances();
    if (variableInstances != null) {
      for (VariableInstanceEntity variableInstance : variableInstances.values()) {
        deleteVariableInstance(variableInstance);
      }
    }
  }

  @SuppressWarnings("unchecked")
  public Map<String, List<VariableInstanceEntity>> findVariableInstancesByTaskIds(Collection<String> taskIds, Collection<String> variableNames) {
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("taskIds", taskIds);
    params.put("variableNames", variableNames);
    List<VariableInstanceEntity> variableInstanceEntities = getDbSqlSession().selectList("selectVariablesByTaskIds", params);
    return createVariablesMapByTaskId(variableInstanceEntities);
  }

  @SuppressWarnings("unchecked")
  public Map<String, List<VariableInstanceEntity>> findVariableInstancesLocalByTaskIds(Collection<String> taskIds, Collection<String> variableNames) {
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("taskIds", taskIds);
    params.put("variableNames", variableNames);
    List<VariableInstanceEntity> variableInstanceEntities = getDbSqlSession().selectList("selectVariablesLocalByTaskIds", params);
    return createVariablesMapByTaskId(variableInstanceEntities);
  }

  private Map<String, List<VariableInstanceEntity>> createVariablesMapByTaskId(List<VariableInstanceEntity> variableInstanceEntities) {
    Map<String, List<VariableInstanceEntity>> variablesByTaskMap = new HashMap<String, List<VariableInstanceEntity>>();
    for (VariableInstanceEntity variableInstanceEntity : variableInstanceEntities) {
      String taskId = variableInstanceEntity.getTaskId();
      if (variablesByTaskMap.get(taskId) == null) {
        variablesByTaskMap.put(taskId, new ArrayList<VariableInstanceEntity>());
      }
      variablesByTaskMap.get(taskId).add(variableInstanceEntity);
    }
    return variablesByTaskMap;
  }
}
