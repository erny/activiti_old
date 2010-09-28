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

package org.activiti.engine.impl.identity;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.identity.GroupQuery;


/**
 * Contains the possible properties that can be used by the {@link GroupQuery}.
 * 
 * @author Joram Barrez
 */
public class GroupQueryProperty {
  
  private static final Map<String, GroupQueryProperty> properties = new HashMap<String, GroupQueryProperty>();

  public static final GroupQueryProperty ID = new GroupQueryProperty("G.ID_");
  public static final GroupQueryProperty NAME = new GroupQueryProperty("G.NAME_");
  public static final GroupQueryProperty TYPE = new GroupQueryProperty("G.TYPE_");
  
  private String name;

  public GroupQueryProperty(String name) {
    this.name = name;
    properties.put(name, this);
  }

  public String getName() {
    return name;
  }
  
  public static GroupQueryProperty findByName(String propertyName) {
    return properties.get(propertyName);
  }

}
