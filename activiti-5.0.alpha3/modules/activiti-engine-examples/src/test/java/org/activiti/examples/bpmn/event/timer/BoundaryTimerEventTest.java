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
package org.activiti.examples.bpmn.event.timer;

import static org.junit.Assert.*;

import java.util.Date;

import org.activiti.ProcessInstance;
import org.activiti.Task;
import org.activiti.impl.time.Clock;
import org.activiti.test.JobExecutorPoller;
import org.activiti.test.LogInitializer;
import org.activiti.test.ProcessDeclared;
import org.activiti.test.ProcessDeployer;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Joram Barrez
 */
public class BoundaryTimerEventTest {

  @Rule
  public LogInitializer logSetup = new LogInitializer();
  @Rule
  public ProcessDeployer deployer = new ProcessDeployer();

  @Test
  @ProcessDeclared
  public void testInterruptingTimerDuration() {

    // Start process instance
    ProcessInstance pi = deployer.getProcessService().startProcessInstanceByKey("interruptingBoundaryTimer");

    // There should be one task, with a timer : first line support
    Task task = deployer.getTaskService().createTaskQuery().processInstance(pi.getId()).singleResult();
    assertEquals("First line support", task.getName());

    // Set clock to the future such that the timer can fire
    Clock.setCurrentTime(new Date(System.currentTimeMillis() + (5 * 60 * 60 * 1000)));
    new JobExecutorPoller(deployer.getProcessEngine()).waitForJobExecutorToProcessAllJobs(10000L, 250);

    // The timer has fired, and the second task (secondlinesupport) now exists
    task = deployer.getTaskService().createTaskQuery().processInstance(pi.getId()).singleResult();
    assertEquals("Second line support", task.getName());
  }

}
