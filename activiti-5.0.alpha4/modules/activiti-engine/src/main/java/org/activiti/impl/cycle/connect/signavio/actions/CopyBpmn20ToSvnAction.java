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
package org.activiti.impl.cycle.connect.signavio.actions;

import java.util.Map;

import org.activiti.impl.cycle.connect.api.FileInfo;
import org.activiti.impl.cycle.connect.api.FolderInfo;
import org.activiti.impl.cycle.connect.api.ItemInfo;
import org.activiti.impl.cycle.connect.api.RepositoryConnector;
import org.activiti.impl.cycle.connect.api.actions.FileAction;
import org.activiti.impl.cycle.connect.api.actions.FileActionGuiRepresentation;
import org.activiti.impl.cycle.connect.signavio.SignavioConnector;

/**
 * 
 * @author christian.lipphardt@camunda.com
 */
public class CopyBpmn20ToSvnAction extends FileAction {

  private String status;

  @Override
  public void execute() {
  }

  @Override
  public void execute(ItemInfo itemInfo) {
    if (itemInfo instanceof FolderInfo) {
      FileInfo srcFile = getFile();
      SignavioConnector connector = (SignavioConnector) srcFile.getConnector();
      String copyString = connector.getModelAsBpmn20Representation(srcFile);

      FolderInfo targetFolder = (FolderInfo) itemInfo;
      RepositoryConnector svnconnector = targetFolder.getConnector();

      FileInfo destFile = new FileInfo(svnconnector);
      destFile.setName(srcFile.getName() + ".xml");
      destFile.setTextContent(copyString);

      svnconnector.createNewFile(targetFolder, destFile);
    }

    // TODO: check if copied successfully or throw exception
    status = "successfully copied...?";
  }

  @Override
  public void execute(ItemInfo itemInfo, Map<String, Object> param) {
  }

  @Override
  public FileActionGuiRepresentation getGuiRepresentation() {
    return FileActionGuiRepresentation.MODAL_PANEL;
  }

  @Override
  public String getGuiRepresentationAsString() {
    return FileActionGuiRepresentation.MODAL_PANEL.toString();
  }

  @Override
  public String getGuiRepresentationUrl() {
    return "modalContent/copy.xhtml";
  }

  @Override
  public String getGuiRepresentationContent() {
    return status;
  }

  @Override
  public String getName() {
    return "Copy BPMN 2.0 to SVN";
  }

}
