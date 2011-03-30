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
package org.activiti.explorer.ui.management.deployment;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.explorer.data.AbstractLazyLoadingQuery;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;


/**
 * @author Joram Barrez
 */
public class DeploymentListQuery extends AbstractLazyLoadingQuery {
  
  protected RepositoryService repositoryService;
  
  public DeploymentListQuery() {
    this.repositoryService = ProcessEngines.getDefaultProcessEngine().getRepositoryService();
  }

  public int size() {
    return (int) repositoryService.createDeploymentQuery().count();
  }

  public List<Item> loadItems(int start, int count) {
    List<Deployment> deployments = repositoryService.createDeploymentQuery()
      .orderByDeploymentName().asc()
      .orderByDeploymentId().asc()
      .list();
    
    List<Item> items = new ArrayList<Item>();
    for (Deployment deployment : deployments) {
      items.add(new DeploymentListitem(deployment));
    }
    return items;
  }

  public Item loadSingleResult(String id) {
    return new DeploymentListitem(repositoryService.createDeploymentQuery().deploymentId(id).singleResult());
  }
  
  public void setSorting(Object[] propertyIds, boolean[] ascending) {
    throw new UnsupportedOperationException();
  }
  
  class DeploymentListitem extends PropertysetItem implements Comparable<DeploymentListitem> {
    
    private static final long serialVersionUID = 1L;
    
    public DeploymentListitem(Deployment deployment) {
      addItemProperty("id", new ObjectProperty<String>(deployment.getId()));
      addItemProperty("name", new ObjectProperty<String>(deployment.getName()));
    }

    public int compareTo(DeploymentListitem other) {
      String name = (String) getItemProperty("name").getValue();
      String otherName = (String) other.getItemProperty("name").getValue();
      
      int comparison = name.compareTo(otherName);
      if (comparison != 0) {
        return comparison;
      } else {
        String id = (String) getItemProperty("id").getValue();
        String otherId = (String) other.getItemProperty("id").getValue();
        return id.compareTo(otherId);
      }
    }
    
  }
  
}
