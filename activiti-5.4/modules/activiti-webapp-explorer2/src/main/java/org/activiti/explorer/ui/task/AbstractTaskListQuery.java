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
package org.activiti.explorer.ui.task;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.explorer.ExplorerApplication;
import org.activiti.explorer.data.AbstractLazyLoadingQuery;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;


/**
 * @author Joram Barrez
 */
public abstract class AbstractTaskListQuery extends AbstractLazyLoadingQuery {
  
  protected String userId;
  protected TaskService taskService;
  
  public AbstractTaskListQuery() {
    this.userId = ExplorerApplication.getCurrent().getLoggedInUser().getId();
    this.taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
  }

  public int size() {
    return (int) getQuery().count();
  }

  public List<Item> loadItems(int start, int count) {
    List<Task> tasks = getQuery().listPage(start, count);
    List<Item> items = new ArrayList<Item>();
    for (Task task : tasks) {
      items.add(new TaskListItem(task));
    }
    return items;
  }

  public Item loadSingleResult(String id) {
    return new TaskListItem(getQuery().taskId(id).singleResult());
  }

  public void setSorting(Object[] propertyId, boolean[] ascending) {
    throw new UnsupportedOperationException();
  }
  
  protected abstract TaskQuery getQuery();
  
  class TaskListItem extends PropertysetItem implements Comparable<TaskListItem>{

    private static final long serialVersionUID = 1L;
    
    public TaskListItem(Task task) {
      addItemProperty("id", new ObjectProperty<String>(task.getId()));
      addItemProperty("name", new ObjectProperty<String>(task.getName()));
    }

    public int compareTo(TaskListItem other) {
      String taskId = (String) getItemProperty("id").getValue();
      String otherTaskId = (String) other.getItemProperty("id").getValue();
      return taskId.compareTo(otherTaskId);
    }
    
  }

}
