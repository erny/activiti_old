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

package org.activiti.rest.api.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.TaskQueryProperty;
import org.activiti.engine.query.QueryProperty;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.rest.api.ActivitiUtil;
import org.activiti.rest.api.DataResponse;
import org.activiti.rest.api.RequestUtil;
import org.activiti.rest.api.SecuredResource;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

/**
 * @author Tijs Rademakers
 */
public class TasksResource extends SecuredResource {
  
  Map<String, QueryProperty> properties = new HashMap<String, QueryProperty>();
  
  public TasksResource() {
    properties.put("id", TaskQueryProperty.TASK_ID);
    properties.put("name", TaskQueryProperty.NAME);
    properties.put("description", TaskQueryProperty.DESCRIPTION);
    properties.put("priority", TaskQueryProperty.PRIORITY);
    properties.put("assignee", TaskQueryProperty.ASSIGNEE);
    properties.put("executionId", TaskQueryProperty.EXECUTION_ID);
    properties.put("processInstanceId", TaskQueryProperty.PROCESS_INSTANCE_ID);
  }
  
  @Get
  public DataResponse getTasks() {
    if(authenticate() == false) return null;
    
    String personalTaskUserId = getQuery().getValues("assignee");
    String ownerTaskUserId = getQuery().getValues("owner");
    String involvedTaskUserId = getQuery().getValues("involved");
    String candidateTaskUserId = getQuery().getValues("candidate");
    String candidateGroupId = getQuery().getValues("candidate-group");
    
    String strPriority = getQuery().getValues("priority");
    String strMinPriority = getQuery().getValues("minPriority");
    String strMaxPriority = getQuery().getValues("maxPriority");
    
    String strDueDate = getQuery().getValues("dueDate");
    String strMinDueDate = getQuery().getValues("minDueDate");
    String strMaxDueDate = getQuery().getValues("maxDueDate");
    
    TaskQuery taskQuery = ActivitiUtil.getTaskService().createTaskQuery();
    if (personalTaskUserId != null) {
      taskQuery.taskAssignee(personalTaskUserId);
    } else if (ownerTaskUserId != null) {
      taskQuery.taskOwner(ownerTaskUserId);
    } else if (involvedTaskUserId != null) {
      taskQuery.taskInvolvedUser(involvedTaskUserId);
    } else if (candidateTaskUserId != null) {
      taskQuery.taskCandidateUser(candidateTaskUserId);
    } else if (candidateGroupId != null) {
      taskQuery.taskCandidateGroup(candidateGroupId);
    } else {
      throw new ActivitiException("Tasks must be filtered with 'assignee', 'owner', 'involved', 'candidate' or 'candidate-group'");
    }
    
    if (strPriority != null) {
      taskQuery.taskPriority(RequestUtil.parseToInteger(strPriority));
    } else if (strMinPriority != null) {
      taskQuery.taskMinPriority(RequestUtil.parseToInteger(strMinPriority));
    } else if (strMaxPriority != null) {
      taskQuery.taskMaxPriority(RequestUtil.parseToInteger(strMaxPriority));
    }
    
    if (strDueDate != null) {
      taskQuery.dueDate(RequestUtil.parseToDate(strDueDate));
    } else if (strMinDueDate != null) {
      taskQuery.dueAfter(RequestUtil.parseToDate(strMinDueDate));
    } else if (strMaxDueDate != null) {
      taskQuery.dueBefore(RequestUtil.parseToDate(strMaxDueDate));
    }
    
    DataResponse dataResponse = new TasksPaginateList().paginateList(getQuery(), taskQuery, "id", properties);
    // add processDefinition name to every task
    @SuppressWarnings("unchecked")
    List<TaskResponse> tasks = (List<TaskResponse>) dataResponse.getData();
    RepositoryService repositoryService = ActivitiUtil.getRepositoryService();
    Map <String , ProcessDefinition> processDefinitions = new HashMap<String, ProcessDefinition>();
    for (TaskResponse taskResponse : tasks) {
       String processDefinitionId = taskResponse.getProcessDefinitionId();
       ProcessDefinition processDefinition = processDefinitions.get(processDefinitionId);
       if (processDefinition == null){
           processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).list().get(0);
           processDefinitions.put(processDefinitionId, processDefinition);
       }
       String processDefinitionName = processDefinition.getName();
       taskResponse.setProcessDefinitionName(processDefinitionName);
    }
    return dataResponse;
  }

  @Put
  public ObjectNode createTask(Representation entity) {
    if (authenticate() == false)
      return null;
    try {
      String startParams = entity.getText();
      JsonNode startJSON = new ObjectMapper().readTree(startParams);

      String name = startJSON.path("name").getTextValue();
      String description = startJSON.path("description").getTextValue();
      String dueDateStr = startJSON.path("dueDate").getTextValue();
      Form tmpForm = new Form();
      tmpForm.set("dueDate", dueDateStr);
      Date dueDate = RequestUtil.getDate(tmpForm, "dueDate");
      int priority = startJSON.path("priority").getIntValue();
      String owner = startJSON.path("owner").getTextValue();

      TaskService taskService = ActivitiUtil.getTaskService();

      // Create task
      Task task = taskService.newTask();
      task.setName(name);
      task.setDescription(description);
      task.setDueDate(dueDate);
      task.setPriority(priority);
      task.setOwner(owner);
      taskService.saveTask(task);
    } catch (Exception e) {
      throw new ActivitiException("Failed to retrieve the process definition parameters", e);
    }
    ObjectNode successNode = new ObjectMapper().createObjectNode();
    successNode.put("success", true);
    return successNode;
  }
}
