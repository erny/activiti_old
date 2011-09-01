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
package org.activiti.engine.test.bpmn.async;

import java.util.Date;

import org.activiti.engine.impl.persistence.entity.MessageEntity;
import org.activiti.engine.impl.persistence.entity.TimerEntity;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.impl.util.ClockUtil;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.test.Deployment;

/**
 * 
 * @author Daniel Meyer
 */
public class AsyncServiceTaskTest extends PluggableActivitiTestCase {
  
  public static boolean INVOCATION;
  
  @Deployment
  public void testAsycServiceNoListeners() {  
    INVOCATION = false;
    // start process 
    runtimeService.startProcessInstanceByKey("asyncService").getProcessInstanceId();
    // now there should be one job in the database:
    assertEquals(1, managementService.createJobQuery().count());
    // the service was not invoked:
    assertFalse(INVOCATION);
    
    waitForJobExecutorToProcessAllJobs(5000L, 25L);
    
    // the service was invoked
    assertTrue(INVOCATION);    
    // and the job is done
    assertEquals(0, managementService.createJobQuery().count());       
  }
  
  @Deployment
  public void testAsycServiceListeners() {  
    String pid = runtimeService.startProcessInstanceByKey("asyncService").getProcessInstanceId();
    assertEquals(1, managementService.createJobQuery().count());
    // the listener was not yet invoked:
    assertNull(runtimeService.getVariable(pid, "listener"));
    
    waitForJobExecutorToProcessAllJobs(5000L, 25L);
    
    assertEquals(0, managementService.createJobQuery().count());
  }
  
  @Deployment
  public void testAsycServiceConcurrent() {  
    INVOCATION = false;
    // start process 
    runtimeService.startProcessInstanceByKey("asyncService").getProcessInstanceId();
    // now there should be one job in the database:
    assertEquals(1, managementService.createJobQuery().count());
    // the service was not invoked:
    assertFalse(INVOCATION);
    
    waitForJobExecutorToProcessAllJobs(5000L, 25L);
    
    // the service was invoked
    assertTrue(INVOCATION);    
    // and the job is done
    assertEquals(0, managementService.createJobQuery().count());   
  }
  
  @Deployment
  public void testAsyncServiceMultiInstance() {  
    INVOCATION = false;
    // start process 
    runtimeService.startProcessInstanceByKey("asyncService").getProcessInstanceId();
    // now there should be one job in the database:
    assertEquals(1, managementService.createJobQuery().count());
    // the service was not invoked:
    assertFalse(INVOCATION);
    
    waitForJobExecutorToProcessAllJobs(5000L, 25L);
    
    // the service was invoked
    assertTrue(INVOCATION);    
    // and the job is done
    assertEquals(0, managementService.createJobQuery().count());   
  }
  

  // This test passes but I am not sure it is the expected behavior from a user-perspective:
  @Deployment
  public void testFailingAsycServiceTimer() { 
    // start process 
    runtimeService.startProcessInstanceByKey("asyncService").getProcessInstanceId();
    // now there should be one job in the database, and it is a message
    assertEquals(1, managementService.createJobQuery().count());
    Job job = managementService.createJobQuery().singleResult();
    if(!(job instanceof MessageEntity)) {
      fail("the job must be a message");
    }      
    
    waitForJobExecutorToProcessAllJobs(5000L, 25L);
    
    // the service failed: the execution is still sitting in the service task:
    Execution execution = runtimeService.createExecutionQuery().singleResult();
    assertNotNull(execution);
    assertEquals("service", runtimeService.getActiveActivityIds(execution.getId()).get(0));
    
    // there still a single job because the timer was created in the same transaction as the 
    // service was executed (which rolled back)
    assertEquals(1, managementService.createJobQuery().count());    
    
    runtimeService.deleteProcessInstance(execution.getId(), "dead");        
  }
  
  // TODO:Maybe we want to see this:
  @Deployment
  public void FAILING_testFailingAsycServiceTimer() { 
    // start process 
    runtimeService.startProcessInstanceByKey("asyncService").getProcessInstanceId();
    // now there are two jobs the message and a timer:
    assertEquals(2, managementService.createJobQuery().count());          
    
    // let 'max-retires' on the message be reached
    waitForJobExecutorToProcessAllJobs(5000L, 25L);
    
    // the service failed: the execution is still sitting in the service task:
    Execution execution = runtimeService.createExecutionQuery().singleResult();
    assertNotNull(execution);
    assertEquals("service", runtimeService.getActiveActivityIds(execution.getId()).get(0));
    
    // there are tow jobs, the message and the timer (the message will no be retried anymore, max retires is reached.)
    assertEquals(2, managementService.createJobQuery().count());    
      
    // now the timer triggers:
    ClockUtil.setCurrentTime(new Date(System.currentTimeMillis()+10000));
    waitForJobExecutorToProcessAllJobs(5000L, 25L);
    
    // and we are done:
    assertNull(runtimeService.createExecutionQuery().singleResult());    
    // and there are no more jobs left:
    assertEquals(0, managementService.createJobQuery().count());   
        
  }
  
  @Deployment
  public void testAsycServiceSubProcessTimer() { 
    INVOCATION = false;
    // start process 
    runtimeService.startProcessInstanceByKey("asyncService").getProcessInstanceId();
    // now there should be two jobs in the database:
    assertEquals(2, managementService.createJobQuery().count());
    // the service was not invoked:
    assertFalse(INVOCATION);
    
    waitForJobExecutorToProcessAllJobs(5000L, 25L);
    
    // the service was invoked
    assertTrue(INVOCATION);    
    // both the timer and the message are cancelled
    assertEquals(0, managementService.createJobQuery().count());   
        
  }
  


}
