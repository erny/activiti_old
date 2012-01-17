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

package org.activiti.rest.api.process;

import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.rest.api.ActivitiUtil;
import org.activiti.rest.api.SecuredResource;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.resource.Get;

/**
 * @author Manuel Saelices
 */
public class ProcessDefinitionTasksResource extends SecuredResource {
  
  @Get
  public ObjectNode getTasks() {
    if(authenticate() == false) return null;
    
    String processDefinitionId = (String) getRequest().getAttributes().get("processDefinitionId");
    
    if(processDefinitionId == null) {
      throw new ActivitiException("No process definition id provided");
    }
    ObjectNode responseJSON = new ObjectMapper().createObjectNode();
    ArrayNode tasksJSON = new ObjectMapper().createArrayNode();
    RepositoryService repository = ActivitiUtil.getRepositoryService();
    ProcessDefinitionEntity pde = (ProcessDefinitionEntity) ((RepositoryServiceImpl) ActivitiUtil.getRepositoryService())
            .getDeployedProcessDefinition(processDefinitionId);
    Map<String, TaskDefinition> taskDefinitionMap = pde.getTaskDefinitions();
    for (TaskDefinition taskDef: taskDefinitionMap.values()) {
    	ObjectNode jsonNode = new ObjectMapper().createObjectNode();
    	jsonNode.put("key", taskDef.getKey());
    	Expression assigneeExpression = taskDef.getAssigneeExpression();
    	if (assigneeExpression != null){
    		jsonNode.put("assigneeExpression", assigneeExpression.toString());    		
    	} else {
    		jsonNode.put("assigneeExpression", "");
    	}
    	Expression nameExpression = taskDef.getNameExpression();
    	if (nameExpression != null){
    		jsonNode.put("nameExpression", nameExpression.toString());    		
    	} else {
    		jsonNode.put("nameExpression", "");
    	}
    	tasksJSON.add(jsonNode);
    }
    responseJSON.put("data", tasksJSON);
    return responseJSON;
  }
}
