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
package org.activiti.explorer.ui.flow;

import org.activiti.engine.FormService;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.explorer.ExplorerApp;
import org.activiti.explorer.I18nManager;
import org.activiti.explorer.Messages;
import org.activiti.explorer.ui.ExplorerLayout;
import org.activiti.explorer.ui.Images;
import org.activiti.explorer.ui.custom.DetailPanel;
import org.activiti.explorer.ui.custom.PrettyTimeLabel;
import org.activiti.explorer.ui.flow.listener.StartFlowClickListener;
import org.activiti.explorer.ui.form.FormPropertiesEventListener;
import org.activiti.explorer.ui.form.FormPropertiesForm;
import org.activiti.explorer.ui.form.FormPropertiesForm.FormPropertiesEvent;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;


/**
 * Panel showing process definition detail.
 * 
 * @author Frederik Heremans
 */
public class ProcessDefinitionDetailPanel extends DetailPanel {
  
  private static final long serialVersionUID = -2018798598805436750L;
  
  // Members
  protected ProcessDefinition processDefinition;
  protected Deployment deployment;
  protected FlowPage flowPage;
  
  // Services
  protected RepositoryService repositoryService;
  protected FormService formService; 
  protected I18nManager i18nManager;
  
  // UI
  protected VerticalLayout verticalLayout;
  protected HorizontalLayout detailContainer;
  protected HorizontalLayout actionsContainer;
  protected Label nameLabel;
  protected Button startFlowButton;
  
  protected FormPropertiesForm processDefinitionStartForm;
  protected ProcessDefinitionInfoComponent definitionInfoComponent;
  
  public ProcessDefinitionDetailPanel(String processDefinitionId, FlowPage flowPage) {
    this.repositoryService = ProcessEngines.getDefaultProcessEngine().getRepositoryService();
    this.formService = ProcessEngines.getDefaultProcessEngine().getFormService();
    this.i18nManager = ExplorerApp.get().getI18nManager();
    
    this.flowPage = flowPage;
    this.processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();

    if(processDefinition != null) {
      deployment = repositoryService.createDeploymentQuery().deploymentId(processDefinition.getDeploymentId()).singleResult();
    }
    
    initUi();
  }
  
  protected void initUi() {
    setSizeFull();
    addStyleName(Reindeer.LAYOUT_WHITE);
    
    verticalLayout = new VerticalLayout();
    verticalLayout.setWidth(100, UNITS_PERCENTAGE);
    verticalLayout.setMargin(true);
    setDetailContainer(verticalLayout);
    
    // All details about the process definition
    initHeader();
    
    detailContainer = new HorizontalLayout();
    detailContainer.addStyleName(Reindeer.PANEL_LIGHT);
    verticalLayout.addComponent(detailContainer);
    detailContainer.setSizeFull();
    
    initActions();
    
    // Show details
    showProcessDefinitionInfo();
  }
  
  protected void initActions() {
    startFlowButton = new Button(i18nManager.getMessage(Messages.FLOW_START));
    startFlowButton.addListener(new StartFlowClickListener(processDefinition, flowPage));
    
    // Clear toolbar and add "start flow" button
    flowPage.getToolBar().removeAllButtons();
    flowPage.getToolBar().addButton(startFlowButton);
  }
  

  public void showProcessDefinitionInfo() {
    if(definitionInfoComponent == null) {
      definitionInfoComponent = new ProcessDefinitionInfoComponent(processDefinition, deployment);
    }
    
    startFlowButton.setEnabled(true);
    detailContainer.removeAllComponents();
    detailContainer.addComponent(definitionInfoComponent);
  }
  
  public void showProcessStartForm(StartFormData startFormData) {
    if(processDefinitionStartForm == null) {
      processDefinitionStartForm = new FormPropertiesForm();
      processDefinitionStartForm.setSubmitButtonCaption("Start process");
      processDefinitionStartForm.setCancelButtonCaption("Cancel");
      
      // When form is submitted/cancelled, show the info again
      processDefinitionStartForm.addListener(new FormPropertiesEventListener() {
        
        private static final long serialVersionUID = -1747717959106153970L;

        @Override
        protected void handleFormSubmit(FormPropertiesEvent event) {
          formService.submitStartFormData(processDefinition.getId(), event.getFormProperties());
          
          // Show notification
          ExplorerApp.get().getMainWindow().showNotification("Process '" + 
            processDefinition.getName() + "' started successfully");
          showProcessDefinitionInfo();
        }
        
        @Override
        protected void handleFormCancel(FormPropertiesEvent event) {
          showProcessDefinitionInfo();
        }
      });
    }
    processDefinitionStartForm.setFormProperties(startFormData.getFormProperties());
    
    startFlowButton.setEnabled(false);
    detailContainer.removeAllComponents();
    detailContainer.addComponent(processDefinitionStartForm);
  }

  protected void initHeader() {
    GridLayout taskDetails = new GridLayout(4, 2);
    taskDetails.setWidth(100, UNITS_PERCENTAGE);
    taskDetails.addStyleName(ExplorerLayout.STYLE_TITLE_BLOCK);
    taskDetails.setSpacing(true);
    taskDetails.setMargin(false, false, true, false);
    
    // Add image
    Embedded image = new Embedded(null, Images.FLOW_50);
    taskDetails.addComponent(image, 0, 0, 0, 1);
    
    // Add task name
    Label nameLabel = new Label(processDefinition.getName());
    nameLabel.addStyleName(Reindeer.LABEL_H2);
    taskDetails.addComponent(nameLabel, 1, 0, 3,0);

    // Add version
    String versionString = i18nManager.getMessage(Messages.FLOW_VERSION, processDefinition.getVersion());
    Label versionLabel = new Label(versionString);
    versionLabel.addStyleName(ExplorerLayout.STYLE_FLOW_HEADER_VERSION);
    taskDetails.addComponent(versionLabel, 1, 1);
    
    // Add deploy time
    PrettyTimeLabel deployTimeLabel = new PrettyTimeLabel(i18nManager.getMessage(Messages.FLOW_DEPLOY_TIME),
      deployment.getDeploymentTime(), null);
    deployTimeLabel.addStyleName(ExplorerLayout.STYLE_FLOW_HEADER_DEPLOY_TIME);
    taskDetails.addComponent(deployTimeLabel, 2, 1);
    
    taskDetails.setColumnExpandRatio(1, 1.0f);
    taskDetails.setColumnExpandRatio(2, 1.0f);
    taskDetails.setColumnExpandRatio(3, 1.0f);
    
    verticalLayout.addComponent(taskDetails);
  }
  
  protected void addEmptySpace(ComponentContainer container) {
    Label emptySpace = new Label("&nbsp;", Label.CONTENT_XHTML);
    emptySpace.setSizeUndefined();
    container.addComponent(emptySpace);
  }
}
