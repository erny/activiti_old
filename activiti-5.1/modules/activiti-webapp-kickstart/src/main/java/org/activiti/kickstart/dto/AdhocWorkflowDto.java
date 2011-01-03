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
package org.activiti.kickstart.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.activiti.kickstart.bpmn20.model.Definitions;
import org.activiti.kickstart.bpmn20.model.Documentation;
import org.activiti.kickstart.bpmn20.model.FlowElement;
import org.activiti.kickstart.bpmn20.model.FormalExpression;
import org.activiti.kickstart.bpmn20.model.Process;
import org.activiti.kickstart.bpmn20.model.activity.resource.HumanPerformer;
import org.activiti.kickstart.bpmn20.model.activity.resource.PotentialOwner;
import org.activiti.kickstart.bpmn20.model.activity.resource.ResourceAssignmentExpression;
import org.activiti.kickstart.bpmn20.model.activity.type.UserTask;
import org.activiti.kickstart.bpmn20.model.bpmndi.BPMNDiagram;
import org.activiti.kickstart.bpmn20.model.bpmndi.BPMNPlane;
import org.activiti.kickstart.bpmn20.model.connector.SequenceFlow;
import org.activiti.kickstart.bpmn20.model.event.EndEvent;
import org.activiti.kickstart.bpmn20.model.event.StartEvent;
import org.activiti.kickstart.bpmn20.model.gateway.ParallelGateway;
import org.activiti.kickstart.util.ExpressionUtil;

/**
 * @author Joram Barrez
 */
public class AdhocWorkflowDto {

  public static final String START_NAME = "theStart";
  public static final String END_NAME = "theEnd";

  protected String name;
  protected String description;
  protected List<TaskDto> tasks = new ArrayList<TaskDto>();
  protected List<TaskBlock> taskBlocks;

  // Cached version of the BPMN JAXB counterpart
  protected Definitions definitions;

  public AdhocWorkflowDto() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    this.definitions = null;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
    this.definitions = null;
  }

  public List<TaskDto> getTasks() {
    return Collections.unmodifiableList(tasks);
  }

  public void setTasks(List<TaskDto> tasks) {
    this.tasks = tasks;
    this.taskBlocks = null;
    this.definitions = null;
  }

  public void addTask(TaskDto task) {
    tasks.add(task);
    this.taskBlocks = null;
    this.definitions = null;
  }

  public List<TaskBlock> getTaskBlocks() {
    if (taskBlocks == null) {
      generateTaskBlocks();
    }
    return taskBlocks;
  }

  protected void generateTaskBlocks() {
    taskBlocks = new ArrayList<TaskBlock>();
    for (int i = 0; i < tasks.size(); i++) {
      TaskDto task = tasks.get(i);
      // Parallel tasks are grouped in the same task block
      if (task.getStartsWithPrevious() && (i != 0)) {
        taskBlocks.get(taskBlocks.size() - 1).addTask(task);
      } else {
        TaskBlock taskBlock = new TaskBlock();
        taskBlock.addTask(task);
        taskBlocks.add(taskBlock);
      }
    }
  }

  /**
   * Generate the JAXB version of this adhoc workflow.
   * 
   * Extremely important: the flowelements are added in topological order, from
   * left to right and top to bottom.
   */
  public Definitions convertToBpmn20() {

    if (definitions != null) {
      return definitions;
    }

    definitions = new Definitions();
    definitions.setTargetNamespace("adhoc");
    String processName = getName().replace(" ", "_");

    // Process
    org.activiti.kickstart.bpmn20.model.Process process = new org.activiti.kickstart.bpmn20.model.Process();
    process.setId("adhoc_" + processName);
    process.setName(getName());
    Documentation processDocumentation = new Documentation();
    processDocumentation.setId(process.getId() + "_documentation");
    processDocumentation.setText(getDescription());
    process.getDocumentation().add(processDocumentation);
    definitions.getRootElement().add(process);

    // BPMNDiagram
    BPMNDiagram diagram = new BPMNDiagram();
    diagram.setId(processName + "_diagram");
    definitions.getDiagram().add(diagram);
    BPMNPlane plane = new BPMNPlane();
    plane.setId(processName + "_plane");
    plane.setBpmnElement(process);
    diagram.setBPMNPlane(plane);

    // Start
    StartEvent startEvent = new StartEvent();
    startEvent.setId(START_NAME);
    process.getFlowElement().add(startEvent);

    // We'll group tasks by each 'task block' that is to be executed in parallel
    List<List<UserTask>> UserTaskBlocks = new ArrayList<List<UserTask>>();
    int index = 1;
    List<TaskBlock> taskBlocks = getTaskBlocks();
    for (TaskBlock taskBlock : taskBlocks) {

      List<UserTask> UserTaskBlock = new ArrayList<UserTask>();
      UserTaskBlocks.add(UserTaskBlock);

      for (TaskDto task : taskBlock.getTasks()) {

        UserTask userTask = new UserTask();
        userTask.setId("task_" + index++);
        userTask.setName(task.getName());
        if (task.getForm() != null) {
          userTask.setFormKey(task.generateDefaultFormName());
        }

        // assignee
        if (task.getAssignee() != null && !"".equals(task.getAssignee())) {
          HumanPerformer humanPerformer = new HumanPerformer();
          humanPerformer.setId(userTask.getId() + "_humanPerformer");
          ResourceAssignmentExpression assignmentExpression = new ResourceAssignmentExpression();
          assignmentExpression.setId(userTask.getId() + "_humanPerformer_assignmentExpression");
          FormalExpression formalExpression = new FormalExpression(task.getAssignee());
          formalExpression.setId(userTask.getId() + "_humanPerformer_formalExpressions");
          assignmentExpression.setExpression(formalExpression);
          humanPerformer.setResourceAssignmentExpression(assignmentExpression);
          userTask.getActivityResource().add(humanPerformer);
        }
        
        // groups
        if (task.getGroups() != null && !"".equals(task.getGroups()) ) {
          PotentialOwner potentialOwner = new PotentialOwner();
          potentialOwner.setId(userTask.getId() + "_potentialOwner");
          ResourceAssignmentExpression assignmentExpression = new ResourceAssignmentExpression();
          assignmentExpression.setId(userTask.getId() + "_potentialOwner_assignmentExpression");
          
          StringBuilder groups = new StringBuilder();
          for (String group : task.getGroups().split(",")) {
            groups.append(group + ",");
          }
          groups.deleteCharAt(groups.length() - 1);
          FormalExpression formalExpression = new FormalExpression(groups.toString());
          
          formalExpression.setId(userTask.getId() + "_potentialOwner_formalExpressions");
          assignmentExpression.setExpression(formalExpression);
          potentialOwner.setResourceAssignmentExpression(assignmentExpression);
          userTask.getActivityResource().add(potentialOwner);
        }

        // Description
        Documentation taskDocumentation = new Documentation(ExpressionUtil.replaceWhiteSpaces(task.getDescription()));
        taskDocumentation.setId(userTask.getId() + "_documentation");
        userTask.getDocumentation().add(taskDocumentation);

        // process.getFlowElement().add(userTask);
        UserTaskBlock.add(userTask);

      }
    }

    // Sequence flow generation
    AtomicInteger flowIndex = new AtomicInteger(1); // Hacky hacky, Integer doesnt have an increment() function ... lazy me
    AtomicInteger gatewayIndex = new AtomicInteger(1);
    List<FlowElement> lastFlowElementOfBlockStack = new ArrayList<FlowElement>();
    lastFlowElementOfBlockStack.add(startEvent);

    // All tasks blocks
    for (int i = 0; i < taskBlocks.size(); i++) {
      convertTaskBlockToBpmn20(process, flowIndex, gatewayIndex, UserTaskBlocks.get(i), lastFlowElementOfBlockStack);
    }

    // End
    EndEvent endEvent = new EndEvent();
    endEvent.setId(END_NAME);
    process.getFlowElement().add(endEvent);

    // Seq flow lastTask -> end
    createSequenceFlow(process, flowIndex, getLast(lastFlowElementOfBlockStack), endEvent);

    return definitions;
  }
  
  protected FlowElement getLast(List<FlowElement> elements) {
    if (elements.size() > 0) {
      return elements.get(elements.size() - 1);
    }
    return null;
  }

  protected SequenceFlow createSequenceFlow(Process process, AtomicInteger flowIndex, 
          FlowElement sourceRef, FlowElement targetRef) {
    SequenceFlow sequenceFlow = new SequenceFlow();
    sequenceFlow.setId("flow_" + flowIndex.getAndIncrement());
    sequenceFlow.setSourceRef(sourceRef);
    sequenceFlow.setTargetRef(targetRef);
    process.getFlowElement().add(sequenceFlow);
    return sequenceFlow;
  }

  protected void convertTaskBlockToBpmn20(Process process, AtomicInteger flowIndex, 
          AtomicInteger gatewayIndex, List<UserTask> taskBlock,
          List<FlowElement> lastFlowElementOfBlockStack) {

    SequenceFlow sequenceFlow = createSequenceFlow(process, flowIndex, 
            getLast(lastFlowElementOfBlockStack), null);
    if (taskBlock.size() == 1) {
      UserTask userTask = taskBlock.get(0);
      sequenceFlow.setTargetRef(userTask);
      lastFlowElementOfBlockStack.add(userTask);
      process.getFlowElement().add(userTask);
    } else {
      ParallelGateway fork = new ParallelGateway();
      fork.setId("parallel_gateway_fork_" + gatewayIndex.getAndIncrement());
      process.getFlowElement().add(fork);
      sequenceFlow.setTargetRef(fork);

      ParallelGateway join = new ParallelGateway();
      join.setId("parallel_gateway_join" + gatewayIndex.getAndIncrement());

      // sequence flow to each task of the task block from the parallel gateway
      // and back to the join
      for (UserTask taskInBlock : taskBlock) {
        createSequenceFlow(process, flowIndex, fork, taskInBlock);
        createSequenceFlow(process, flowIndex, taskInBlock, join);
        process.getFlowElement().add(taskInBlock);
      }

      process.getFlowElement().add(join);
      lastFlowElementOfBlockStack.add(join);
    }
  }

}
