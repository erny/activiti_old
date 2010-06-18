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
package org.activiti.test.db;

import org.activiti.ActivitiException;
import org.activiti.Deployment;
import org.activiti.test.ActivitiTestCase;


/**
 * @author Joram Barrez
 */
public class DbNotCleanTest extends ActivitiTestCase {
  
  public void testDbNotCleanAfterTest() {
    Deployment deployment = 
      processEngine.getProcessService()
        .newDeployment()
        .addString("test.bpmn20.xml", "<definitions xmlns='http://schema.omg.org/spec/BPMN/2.0' " +
        		"targetNamespace='http://www.activiti.org/bpmn2.0' />")
        .deploy();
    assertNotNull(deployment);
    
    // Manually call the check on db cleaning check
    try {
      checkDbIsClean();
      fail("Exception expected");
    }  catch (ActivitiException e) {
      assertExceptionMessage("Database not clean", e);
    }
    
    // Registering the deployment will clean it up in the 'real' tearDown
    registerDeployment(deployment.getId());
  }

}
