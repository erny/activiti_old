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
package org.activiti.test.bpmn.property;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.Execution;
import org.activiti.ProcessInstance;
import org.activiti.test.ActivitiTestCase;


/**
 * @author Joram Barrez
 */
public class PropertyTest extends ActivitiTestCase {
  
  public void testUserTaskSrcProperty() {
    deployProcessForThisTestMethod();
    
    // Start the process -> waits in usertask
    Map<String, Object> vars = new HashMap<String, Object>();
    vars.put("inputVar", "test");
    ProcessInstance pi = processService.startProcessInstanceByKey("testUserTaskSrcProperty", vars);
    
    // 1 task should be active, and since the task is scoped 1 child execution should exist
    assertNotNull(taskService.createTaskQuery().singleResult());
    List<Execution> childExecutions = processService.findChildExecutions(pi.getId());
    assertEquals(1, childExecutions.size());
    
    // The scope at the task should be able to see the 'myVar' variable, 
    // but the process instance shouldn't be able to see it
    Execution childExecution = childExecutions.get(0);
    assertEquals("test", processService.getVariable(childExecution.getId(), "myVar"));
    assertNull(processService.getVariable(pi.getId(), "myVar"));
    
    // The variable 'inputVar' should be visible for both
    assertEquals("test", processService.getVariable(childExecution.getId(), "inputVar"));
    assertEquals("test", processService.getVariable(pi.getId(), "inputVar"));
    
    // Change the value of variable 'myVar' on the task scope
    processService.setVariable(childExecution.getId(), "myVar", "new_value");
    assertEquals("new_value", processService.getVariable(childExecution.getId(), "myVar"));
    assertEquals("test", processService.getVariable(childExecution.getId(), "inputVar"));
    assertNull(processService.getVariable(pi.getId(), "myVar"));
    
    // When the task completes, the variable 'myVar' is destroyed
    taskService.complete(taskService.createTaskQuery().singleResult().getId());
    for (Execution execution : processService.findChildExecutions(pi.getId())) {
      assertNull(processService.getVariable(execution.getId(), "myVar"));
    }
  }
  
  public void testUserTaskSrcExprProperty() {
    deployProcessForThisTestMethod();
    
    // Start the process -> waits in usertask
    final String address = "TestStreet 123 90210 Beverly-Hills";
    Order order = new Order(address);
    Map<String, Object> vars = new HashMap<String, Object>();
    vars.put("order", order);
    ProcessInstance pi = processService.startProcessInstanceByKey("testUserTaskSrcExprProperty", vars);    
    
    // The execution at the task should be able to see the 'orderAddress' variable,
    // but the process instance shouldn't be able to see it
    List<Execution> childExecutions = processService.findChildExecutions(pi.getId());
    String childExecutionId = childExecutions.get(0).getId();
    assertEquals(address, processService.getVariable(childExecutionId, "orderAddress"));
    assertNull(processService.getVariable(pi.getId(), "orderAddress"));

    // Completing the task removes the 'orderAddress' variable
    taskService.complete(taskService.createTaskQuery().singleResult().getId());
    assertNull(processService.getVariable(pi.getId(), "orderAddress"));
    assertNotNull(processService.getVariable(pi.getId(), "order"));
  }
  
  public void testUserTaskDstProperty() {
    deployProcessForThisTestMethod();
    
    ProcessInstance pi = processService.startProcessInstanceByKey("testUserTaskDstProperty");
    List<Execution> childExecutions = processService.findChildExecutions(pi.getId());
    String childExecutionId = childExecutions.get(0).getId();    
    
    // The execution at the task should be able to see the 'taskVar' variable,
    Map<String, Object> vars = processService.getVariables(childExecutionId);
    assertEquals(1, vars.size());
    assertTrue(vars.containsKey("taskVar"));
    
    // but the process instance shouldn't be able to see it
    assertTrue(processService.getVariables(pi.getId()).isEmpty());
    
    // Setting the 'taskVar' value and completing the task should push the value into 'processVar'
    processService.setVariable(childExecutionId, "taskVar", "myValue");
    taskService.complete(taskService.createTaskQuery().singleResult().getId());
    vars = processService.getVariables(pi.getId());
    assertEquals(1, vars.size());
    assertTrue(vars.containsKey("processVar"));
  }
  
  public void NOTYETIMPLEMENTEDtestUserTaskDstExprProperty() {
    deployProcessForThisTestMethod();
    
    Order order = new Order();
    Map<String, Object> vars = new HashMap<String, Object>();
    vars.put("order", order);
    ProcessInstance pi = processService.startProcessInstanceByKey("testUserTaskDstExprProperty", vars);
    
    List<Execution> childExecutions = processService.findChildExecutions(pi.getId());
    String childExecutionId = childExecutions.get(0).getId();    
    
    // The execution at the task should be able to see the 'orderAddress' variable,
    vars = processService.getVariables(childExecutionId);
    assertEquals(1, vars.size());
    assertTrue(vars.containsKey("orderAddress"));
    
    // but the process instance shouldn't be able to see it
    vars = processService.getVariables(pi.getId());
    assertEquals(1, vars.size());
    assertTrue(vars.containsKey("order"));
    
    // Setting the 'orderAddress' value and completing the task should push the value into order object
    processService.setVariable(childExecutionId, "orderAddress", "testAddress");
    taskService.complete(taskService.createTaskQuery().singleResult().getId());
    assertEquals(1, processService.getVariables(pi.getId()).size());
    
    Order orderAfterComplete = (Order) processService.getVariable(pi.getId(), "order"); 
    assertEquals("testAddress", orderAfterComplete.getAddress());
  }
  
  public void testUserTaskLinkProperty() {
    deployProcessForThisTestMethod();
    
    // Start the process -> waits in usertask
    Map<String, Object> vars = new HashMap<String, Object>();
    vars.put("inputVar", "test");
    ProcessInstance pi = processService.startProcessInstanceByKey("testUserTaskLinkProperty", vars);
    
    // Variable 'taskVar' should only be visible for the task scoped execution
    Execution childExecution = processService.findChildExecutions(pi.getId()).get(0);
    assertEquals("test", processService.getVariable(childExecution.getId(), "taskVar"));
    assertEquals("test", processService.getVariable(childExecution.getId(), "inputVar"));
    
    // Change the value of variable 'taskVar' on the task scope
    String taskScopedExecutionId = childExecution.getId();
    processService.setVariable(taskScopedExecutionId, "taskVar", "new_value");
    assertEquals("new_value", processService.getVariable(taskScopedExecutionId, "taskVar"));
    assertEquals("test", processService.getVariable(taskScopedExecutionId, "inputVar"));
    assertNull(processService.getVariable(pi.getId(), "taskVar"));
    
    // Completing the task copies the value of 'taskVar' into 'inputVar'
    taskService.complete(taskService.createTaskQuery().singleResult().getId());
    assertTrue(processService.findChildExecutions(pi.getId()).isEmpty()); // second task is not scoped
    assertNull(processService.findExecutionById(taskScopedExecutionId));
    assertNull(processService.getVariable(pi.getId(), "taskVar"));
    assertEquals("new_value", processService.getVariable(pi.getId(), "inputVar"));
  }

//  public void testUserTaskLinkExprProperty() {
//    deployProcessForThisTestMethod();
//    
//    // Start the process -> waits in usertask
//    Map<String, Object> address = new HashMap<String, Object>();
//    address.put("Street", "Broadway");
//    address.put("City", "New York");
//    
//    Map<String, Object> variables = new HashMap<String, Object>();
//    variables.put("address", address);
//    ProcessInstance pi = processService.startProcessInstanceByKey("testUserTaskLinkExprProperty", variables);
//    
//    // Variable 'taskVar' should only be visible for the task scoped execution
//    Execution childExecution = processService.findChildExecutions(pi.getId()).get(0);
//    assertEquals("test", processService.getVariable(childExecution.getId(), "taskVar"));
//    assertEquals("test", processService.getVariable(childExecution.getId(), "inputVar"));
//    
//    // Change the value of variable 'taskVar' on the task scope
//    String taskScopedExecutionId = childExecution.getId();
//    processService.setVariable(taskScopedExecutionId, "taskVar", "new_value");
//    assertEquals("new_value", processService.getVariable(taskScopedExecutionId, "taskVar"));
//    assertEquals("test", processService.getVariable(taskScopedExecutionId, "inputVar"));
//    assertNull(processService.getVariable(pi.getId(), "taskVar"));
//    
//    // Completing the task copies the value of 'taskVar' into 'inputVar'
//    taskService.complete(taskService.createTaskQuery().singleResult().getId());
//    assertTrue(processService.findChildExecutions(pi.getId()).isEmpty()); // second task is not scoped
//    assertNull(processService.findExecutionById(taskScopedExecutionId));
//    assertNull(processService.getVariable(pi.getId(), "taskVar"));
//    assertEquals("new_value", processService.getVariable(pi.getId(), "inputVar"));
//  }
  

}
