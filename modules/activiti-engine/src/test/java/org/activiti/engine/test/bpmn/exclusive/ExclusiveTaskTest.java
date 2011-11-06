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
package org.activiti.engine.test.bpmn.exclusive;

import java.util.List;

import org.activiti.engine.impl.persistence.entity.JobEntity;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.test.Deployment;

/**
 * 
 * @author Daniel Meyer
 */
public class ExclusiveTaskTest extends PluggableActivitiTestCase {
  
  
  @Deployment
  public void testExclusiveServiceConcurrent() {   
    // start process 
    runtimeService.startProcessInstanceByKey("exclusive");
    // now there should be 2 exclusive jobs in the database:
    List<Job> jobList = managementService.createJobQuery().list();
    assertEquals(2, jobList.size());
    for (Job job : jobList) {
      if(!((JobEntity)job).isExclusive()) {
        fail("This must be an exclusive job!");
      }
    }           
    waitForJobExecutorToProcessAllJobs(5000L, 25L);
    
    // all the jobs are done
    assertEquals(0, managementService.createJobQuery().count());      
  }
  

}
