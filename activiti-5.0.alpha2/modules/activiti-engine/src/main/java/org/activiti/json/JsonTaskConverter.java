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
package org.activiti.json;

import java.io.Reader;

import org.activiti.ActivitiException;
import org.activiti.Task;
import org.activiti.impl.json.JSONObject;
import org.activiti.impl.task.TaskImpl;


/**
 * @author Tom Baeyens
 */
public class JsonTaskConverter extends JsonObjectConverter<Task> {

  public Task toObject(Reader reader) {
    throw new ActivitiException("not yet implemented");
  }

  public JSONObject toJsonObject(Task task) {
    TaskImpl taskImpl = (TaskImpl) task;
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("id", taskImpl.getId());
    jsonObject.put("dbversion", taskImpl.getRevision());
    jsonObject.put("assignee", taskImpl.getAssignee());
    jsonObject.put("name", taskImpl.getName());
    jsonObject.put("priority", taskImpl.getPriority());
    jsonObject.put("createTime", taskImpl.getCreateTime());
    jsonObject.put("skippable", taskImpl.isSkippable());
    if (taskImpl.getStartDeadline()!=null) {
      jsonObject.put("startDeadline", taskImpl.getStartDeadline());
    }
    if (taskImpl.getCompletionDeadline()!=null) {
      jsonObject.put("completionDeadline", taskImpl.getCompletionDeadline());
    }
    if (taskImpl.getExecutionId()!=null) {
      jsonObject.put("execution", taskImpl.getExecutionId());
    }
    if (taskImpl.getProcessDefinitionId()!=null) {
      jsonObject.put("processDefinition", taskImpl.getProcessDefinitionId());
    }
    return jsonObject;
  }
}
