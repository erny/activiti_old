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
import org.activiti.engine.impl.cfg.TaskSession;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.task.TaskEntity;
import org.activiti.engine.impl.task.IdentityLinkEntity;
import org.activiti.engine.task.IdentityLinkType;


/**
 * @author Joram Barrez
 */
public class AddIdentityLinkCmd implements Command<Void> {
  
  protected String taskId;
  
  protected String userId;
  
  protected String groupId;
  
  protected String type;
  
  public AddIdentityLinkCmd(String taskId, String userId, String groupId, String type) {
    validateParams(userId, groupId, type, taskId);
    this.taskId = taskId;
    this.userId = userId;
    this.groupId = groupId;
    this.type = type;
  }
  
  protected void validateParams(String userId, String groupId, String type, String taskId) {
    if(taskId == null) {
      throw new ActivitiException("taskId is null");
    }
    
    if (userId != null && groupId != null) {
      throw new ActivitiException("userId and groupId cannot both be given.");      
    }
    
    if (userId == null && groupId == null) {
      throw new ActivitiException("userId and groupId cannot both be null");      
    }
    
    if (type == null) {
      throw new ActivitiException("type is required when adding a new task identity link");
    }
    
    // Special treatment for assignee
    if (IdentityLinkType.ASSIGNEE.equals(type)) {
      if (userId == null) {
        throw new ActivitiException("When involving an assignee, the userId should always" 
                + " be provided (but null was given)");
      }
      if (groupId != null) {
        throw new ActivitiException("Incompatible usage: cannot use ASSIGNEE" 
                + " together with a groupId");
      }
    }
  }
  
  public Void execute(CommandContext commandContext) {
    TaskSession taskSession = commandContext.getTaskSession();
    TaskEntity task = taskSession.findTaskById(taskId);
    
    if (task == null) {
      throw new ActivitiException("Cannot find task with id " + taskId);
    }
    
    if(userId != null && commandContext.getIdentitySession().findUserById(userId) == null) {
       throw new ActivitiException("Cannot find user with id " + userId);
    }
    
    // Special treatment for assignee
    if (IdentityLinkType.ASSIGNEE.equals(type)) {
      task.setAssignee(userId);
    } else {
      if (userId != null) {
        addIdentityLink(task, userId, null);
      } else {
        if (commandContext.getIdentitySession().findGroupById(groupId) == null) {
          throw new ActivitiException("Cannot find group with id " + groupId);
        } 
        addIdentityLink(task, null, groupId);
      }
      
    }
    return null;  
  }
  
  protected void addIdentityLink(TaskEntity task, String userId, String groupId) {
    IdentityLinkEntity identityLinkEntity = task.createIdentityLink();
    identityLinkEntity.setType(type);
    if (userId != null) {
      identityLinkEntity.setUserId(userId);      
    } else {
      identityLinkEntity.setGroupId(groupId);
    }
  }

}
