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

/**
 * Represents a structure encapsulated in a class
 * 
 * @author Esteban Robles Luna
 */
public class ClassStructure implements Structure {

  protected Class<?> classStructure;

  public ClassStructure(Class<?> classStructure) {
    this.classStructure = classStructure;
  }

  public String getId() {
    return this.classStructure.getName();
  }

  public int getFieldSize() {
    //TODO
    return 0;
  }

  public String getFieldNameAt(int index) {
    return null;
  }

  public Class< ? > getFieldTypeAt(int index) {
    return null;
  }
}
