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

import java.util.Iterator;
import java.util.Map;

import org.activiti.rest.api.ActivitiUtil;
import org.activiti.rest.api.SecuredResource;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.resource.Get;

/**
 * @author Tijs Rademakers
 */
public class ProcessInstanceVariablesResource extends SecuredResource {
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
