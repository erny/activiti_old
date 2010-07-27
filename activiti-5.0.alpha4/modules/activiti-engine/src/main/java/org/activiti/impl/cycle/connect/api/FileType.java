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
package org.activiti.impl.cycle.connect.api;

import java.util.List;

import org.activiti.impl.cycle.connect.api.actions.FileAction;

/**
 * The file type specifies the type of an artifact, e.g. Signavio model, jpdl process model, text file, word document, ...
 * 
 * TODO: Think about FileType hierarchy
 * 
 * @author bernd.ruecker@camunda.com
 */
public class FileType {

  private String name;

  /**
   * the unique identifier for this file (e.g. file extension for files, namespace for Signavio, ...)
   */
  private String typeIdentifier;

  // TODO: think about artifacts containing different files (e.g. jBPM)

  public FileType() {
  }

  public FileType(String name, String typeIdentifier) {
    this.name = name;
    this.typeIdentifier = typeIdentifier;
  }

  public List<Class< ? extends FileAction>> getRegisteredActions() {
    return FileTypeRegistry.getRegisteredActionsForFileType(getTypeIdentifier());
  }

  public Class< ? extends FileAction> getDefaultAction() {
    return FileTypeRegistry.getDefaultActionForFileType(getTypeIdentifier());
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTypeIdentifier() {
    return typeIdentifier;
  }

}
