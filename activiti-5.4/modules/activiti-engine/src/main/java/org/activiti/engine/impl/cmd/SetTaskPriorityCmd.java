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
package org.activiti.engine.impl.cmd;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.task.TaskEntity;


/**
 * @author Joram Barrez
 */
public class SetTaskPriorityCmd implements Command<Void> {
  
  protected String taskId;
  
  protected int priority;
  
  public SetTaskPriorityCmd(String taskId, int priority) {
    this.taskId = taskId;
    this.priority = priority;
  }
  
  public Void execute(CommandContext commandContext) {
    if(taskId == null) {
      throw new ActivitiException("taskId is null");
    }
    
    TaskEntity task = commandContext
      .getTaskSession()
      .findTaskById(taskId);
    
    if (task == null) {
      throw new ActivitiException("Cannot find task with id " + taskId);
    }
    
    task.setPriority(priority);
    
    return null;
  }

}
