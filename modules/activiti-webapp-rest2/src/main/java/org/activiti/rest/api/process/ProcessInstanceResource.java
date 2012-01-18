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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.rest.api.ActivitiUtil;
import org.activiti.rest.api.SecuredResource;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * @author Tijs Rademakers
 */
public class ProcessInstanceResource extends SecuredResource {
  
  @Post
  public ProcessInstanceResponse startProcessInstance(Representation entity) {
    try {
      if(authenticate() == false) return null;
      
      String startParams = entity.getText();
      JsonNode startJSON = new ObjectMapper().readTree(startParams);
      String processDefinitionKey = startJSON.path("processDefinitionKey").getTextValue();
      String processDefinitionId = null;
      if (processDefinitionKey == null) {
        processDefinitionId = startJSON.path("processDefinitionId").getTextValue();
      }
      JsonNode businessKeyJson = startJSON.path("businessKey");
      String businessKey = null;
      if(businessKeyJson != null) {
        businessKey = businessKeyJson.getTextValue();
      }
      
      Map<String, Object> variables = new HashMap<String, Object>();
      Iterator<String> itName = startJSON.getFieldNames();
      while(itName.hasNext()) {
        String name = itName.next();
        variables.put(name, startJSON.path(name).getTextValue()); 
      }
      variables.remove("processDefinitionId");
      variables.remove("processDefinitionKey");
      variables.remove("businessKey");
      
      ProcessInstance processInstance = null;
      if (processDefinitionKey != null) {
        processInstance = ActivitiUtil.getRuntimeService().startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
      }
      else {
        processInstance = ActivitiUtil.getRuntimeService().startProcessInstanceById(processDefinitionId, businessKey, variables);
      }
      ProcessInstanceResponse response = new ProcessInstanceResponse(processInstance);
      return response;
      
    } catch (Exception e) {
      throw new ActivitiException("Failed to retrieve the process definition parameters", e);
    }
  }

	@Get
	public ObjectNode getVariables() {
		if (authenticate() == false)
			return null;
		String processInstanceId = (String) getRequest().getAttributes().get("processInstanceId");
		
		Map<String, Object> variables = ActivitiUtil.getRuntimeService().getVariables(processInstanceId);

		ObjectNode responseJSON = new ObjectMapper().createObjectNode();

		ArrayNode propertiesJSON = new ObjectMapper().createArrayNode();

		for (Iterator<String> it = variables.keySet().iterator(); it.hasNext();){
			String key = it.next();
			String value = String.valueOf(variables.get(key));
			
			ObjectNode propertyJSON = new ObjectMapper().createObjectNode();
			propertyJSON.put(key,value);
			propertiesJSON.add(propertyJSON);
		}
		responseJSON.put("data", propertiesJSON);

		return responseJSON;
	}
}
