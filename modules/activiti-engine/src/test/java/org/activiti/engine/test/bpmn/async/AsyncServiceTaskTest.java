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

import org.activiti.engine.impl.test.PluggableActivitiTestCase;
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
  
  // TODO: do we want this behavior?  
  @Deployment
  public void FAILING_testAsycServiceTimer() { 
    INVOCATION = false;
    // start process 
    runtimeService.startProcessInstanceByKey("asyncService").getProcessInstanceId();
    // now there should be two jobs in the database, one for the message and one for the timer.
    assertEquals(2, managementService.createJobQuery().count());
    // the service was not invoked:
    assertFalse(INVOCATION);
    
    waitForJobExecutorToProcessAllJobs(5000L, 25L);
    
    // the service was invoked
    assertTrue(INVOCATION);    
    // both the timer and the message are cancelled
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
