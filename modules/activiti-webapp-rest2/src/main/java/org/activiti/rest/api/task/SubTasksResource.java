package org.activiti.rest.api.task;

import java.io.IOException;
import java.util.Date;

import javax.crypto.IllegalBlockSizeException;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.activiti.rest.api.ActivitiUtil;
import org.activiti.rest.api.RequestUtil;
import org.activiti.rest.api.SecuredResource;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Put;

public class SubTasksResource extends SecuredResource{
	@Put
	public ObjectNode createSubTask(Representation entity) {
		 Authentication.getAuthenticatedUserId();
		if (authenticate() == false)
			return null;
		try {
			String startParams = entity.getText();
			JsonNode startJSON = new ObjectMapper().readTree(startParams);

    	String parentTaskId = startJSON.path("parentTaskId").getTextValue();
			String name = startJSON.path("name").getTextValue();
			String description = startJSON.path("description").getTextValue();
      
    	TaskService taskService = ActivitiUtil.getTaskService();
    	Task parentTask = taskService.createTaskQuery().taskId(parentTaskId).singleResult();
      // save task
      Task newTask = taskService.newTask();
      newTask.setParentTaskId(parentTask.getId());
      if (parentTask.getAssignee() != null) {
        newTask.setAssignee(parentTask.getAssignee());
      } else {
        newTask.setAssignee(super.loggedInUser);
      }
      if (parentTask.getOwner() != null) {
        newTask.setOwner(parentTask.getOwner());
      } else {
        newTask.setOwner(super.loggedInUser);
      }
      newTask.setName(name);
      newTask.setDescription(description);
      taskService.saveTask(newTask);
		} catch (Exception e) {
			throw new ActivitiException("Failed to create subtask", e);
    }
		ObjectNode successNode = new ObjectMapper().createObjectNode();
		successNode.put("success", true);
		return successNode;
	}

}
