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

package org.activiti.engine.impl.repository;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.repository.ProcessDefinitionQuery;

/**
 * Contains the possible properties that can be used in a {@link ProcessDefinitionQuery}.
 * 
 * @author Joram Barrez
 */
public class ProcessDefinitionQueryProperty {
  
  private static final Map<String, ProcessDefinitionQueryProperty> properties = new HashMap<String, ProcessDefinitionQueryProperty>();

  String PROPERTY_ID = "ID_";
  String PROPERTY_KEY = "KEY_";
  String PROPERTY_VERSION = "VERSION_";
  
  public static final ProcessDefinitionQueryProperty ID = new ProcessDefinitionQueryProperty("PD.ID_");
  public static final ProcessDefinitionQueryProperty DEPLOYMENT_ID = new ProcessDefinitionQueryProperty("PD.DEPLOYMENT_ID_");
  public static final ProcessDefinitionQueryProperty KEY = new ProcessDefinitionQueryProperty("PD.KEY_");
  public static final ProcessDefinitionQueryProperty VERSION = new ProcessDefinitionQueryProperty("PD.VERSION_");

  private String name;

  public ProcessDefinitionQueryProperty(String name) {
    this.name = name;
    properties.put(name, this);
  }

  public String getName() {
    return name;
  }
  
  public static ProcessDefinitionQueryProperty findByName(String propertyName) {
    return properties.get(propertyName);
  }

}
