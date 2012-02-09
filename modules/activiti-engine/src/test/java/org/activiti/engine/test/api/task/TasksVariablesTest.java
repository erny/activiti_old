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

package org.activiti.engine.test.api.task;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;

/**
 * @author Andrzej Dengusiak
 */
public class TasksVariablesTest extends PluggableActivitiTestCase {

  public void testStandaloneTasksVariables() {
    Task task = taskService.newTask();
    task.setName("gonzoTask");
    taskService.saveTask(task);
    String taskId = task.getId();
    taskService.setVariable(taskId, "instrument", "trumpet");
    taskService.setVariable(taskId, "player", "gonzo");

    Task task2 = taskService.newTask();
    task2.setName("kermitTask");
    taskService.saveTask(task2);
    String taskId2 = task2.getId();
    taskService.setVariable(taskId2, "instrument", "clarnet");
    taskService.setVariable(taskId2, "player", "piggy");

    Set<String> variableNames = new HashSet<String>(Arrays.asList("instrument", "player"));

    // retrieve only local task variables, because task is not associated with
    // execution (process) context
    Map<String, Map<String, Object>> variables = taskService.getVariablesLocal(Arrays.asList(taskId, taskId2), variableNames);
    assertNotNull(variables.get(taskId));
    assertNotNull(variables.get(taskId2));
    assertEquals(variableNames, variables.get(taskId).keySet());
    assertEquals(variableNames, variables.get(taskId2).keySet());
    assertEquals("trumpet", variables.get(taskId).get("instrument"));
    assertEquals("gonzo", variables.get(taskId).get("player"));
    assertEquals("clarnet", variables.get(taskId2).get("instrument"));
    assertEquals("piggy", variables.get(taskId2).get("player"));

    taskService.deleteTask(taskId, true);
    taskService.deleteTask(taskId2, true);
  }

  @Deployment
  public void testTasksExecutionVariables() {
    String processInstanceId = runtimeService.startProcessInstanceByKey("oneTaskProcess").getId();
    String taskId = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult().getId();

    String processInstanceId2 = runtimeService.startProcessInstanceByKey("oneTaskProcess").getId();
    String taskId2 = taskService.createTaskQuery().processInstanceId(processInstanceId2).singleResult().getId();

    Map<String, Map<String, Object>> variables = taskService.getVariables(Arrays.asList(taskId, taskId2), null);
    assertNotNull(variables);
    assertTrue(variables.isEmpty());

    
    //variable in task
    taskService.setVariable(taskId, "player", "gonzo");

    variables = taskService.getVariables(Arrays.asList(taskId, taskId2), Arrays.asList("player"));
    assertNotNull(variables.get(taskId));
    assertNull(variables.get(taskId2));
    assertEquals(new HashSet<String>(Arrays.asList("player")), variables.get(taskId).keySet());
    assertEquals("gonzo", variables.get(taskId).get("player"));

    //variable in task2
    taskService.setVariable(taskId2, "instrument", "clarnet");
    
    variables = taskService.getVariables(Arrays.asList(taskId, taskId2), Arrays.asList("player"));
    assertNotNull(variables.get(taskId));
    assertNull(variables.get(taskId2));
    assertEquals(new HashSet<String>(Arrays.asList("player")), variables.get(taskId).keySet());
    assertEquals("gonzo", variables.get(taskId).get("player"));
    
    variables = taskService.getVariables(Arrays.asList(taskId, taskId2), null);
    assertNotNull(variables.get(taskId));
    assertNotNull(variables.get(taskId2));
    assertEquals(new HashSet<String>(Arrays.asList("player")), variables.get(taskId).keySet());
    assertEquals(new HashSet<String>(Arrays.asList("instrument")), variables.get(taskId2).keySet());
    assertEquals("gonzo", variables.get(taskId).get("player"));
    assertEquals("clarnet", variables.get(taskId2).get("instrument"));
    
    variables = taskService.getVariables(Arrays.asList(taskId, taskId2), Arrays.asList("player","instrument"));
    assertNotNull(variables.get(taskId));
    assertNotNull(variables.get(taskId2));
    assertEquals(new HashSet<String>(Arrays.asList("player")), variables.get(taskId).keySet());
    assertEquals(new HashSet<String>(Arrays.asList("instrument")), variables.get(taskId2).keySet());
    assertEquals("gonzo", variables.get(taskId).get("player"));
    assertEquals("clarnet", variables.get(taskId2).get("instrument"));
    
    //local variable in task
    taskService.setVariableLocal(taskId, "instrument", "trumpet");
    //local variable in task2
    taskService.setVariableLocal(taskId2, "player", "piggy");
    
    //retrieve local and process variables
    variables = taskService.getVariables(Arrays.asList(taskId, taskId2), Arrays.asList("player","instrument"));
    assertEquals(new HashSet<String>(Arrays.asList("player","instrument")), variables.get(taskId).keySet());
    assertEquals(new HashSet<String>(Arrays.asList("player","instrument")), variables.get(taskId2).keySet());
    assertEquals("gonzo", variables.get(taskId).get("player"));
    assertEquals("trumpet", variables.get(taskId).get("instrument"));
    assertEquals("piggy", variables.get(taskId2).get("player"));
    assertEquals("clarnet", variables.get(taskId2).get("instrument"));
    
    //retrieve local variables only
    variables = taskService.getVariablesLocal(Arrays.asList(taskId, taskId2), Arrays.asList("player","instrument"));
    assertEquals(new HashSet<String>(Arrays.asList("instrument")), variables.get(taskId).keySet());
    assertEquals(new HashSet<String>(Arrays.asList("player")), variables.get(taskId2).keySet());
    assertEquals("trumpet", variables.get(taskId).get("instrument"));
    assertEquals("piggy", variables.get(taskId2).get("player"));
  }
}
