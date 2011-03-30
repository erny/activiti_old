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

import org.activiti.explorer.data.LazyLoadingQuery;
import org.activiti.explorer.navigation.TaskNavigationHandler;
import org.activiti.explorer.navigation.UriFragment;




/**
 * @author Joram Barrez
 */
public class TaskInboxPage extends TaskPage {
  
  private static final long serialVersionUID = 652000311912640606L;
  
  protected String taskId;
  
  public TaskInboxPage() {
  }
  
  /**
   * Constructor called when page is accessed straight through the url, eg. /task/id=123
   */
  public TaskInboxPage(String taskId) {
    this.taskId = taskId;
  }
  
  @Override
  protected LazyLoadingQuery createLazyLoadingQuery() {
    return new TaskInboxListQuery();
  }
  
  @Override
  protected void initUi() {
    super.initUi();
    if (taskId == null) {
      selectListElement(0);
    } else {
      selectListElement(taskListContainer.getIndexForObjectId(taskId));
    }
  }

  @Override
  protected UriFragment getUriFragment(String taskId) {
    UriFragment taskFragment = new UriFragment(TaskNavigationHandler.TASK_URI_PART, taskId);

    if(taskId != null) {
      taskFragment.addUriPart(taskId);
    }

    taskFragment.addParameter(TaskNavigationHandler.PARAMETER_CATEGORY, TaskNavigationHandler.CATEGORY_INBOX);
    return taskFragment;
  }
  
}
