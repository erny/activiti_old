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

package org.activiti.engine.test.db;

import junit.framework.TestCase;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.repository.Deployment;



/**
 * @author Daniel Meyer
 */
public class DatabaseTablePrefixTest extends TestCase {
  
  public void testPerformDatabaseSchemaOperationCreate() {
    
    ProcessEngineConfiguration config1 = ProcessEngineConfigurationImpl.createProcessEngineConfigurationFromResource("org/activiti/engine/test/db/prefix1.activiti.cfg.xml");
    ProcessEngineConfiguration config2 = ProcessEngineConfigurationImpl.createProcessEngineConfigurationFromResource("org/activiti/engine/test/db/prefix2.activiti.cfg.xml");
        
    ProcessEngine engine1 = config1.buildProcessEngine();
    ProcessEngine engine2 = config2.buildProcessEngine();
    
    // if I deploy a process to one engine, it is not visible to the other engine:
    try {
      Deployment deployment = engine1.getRepositoryService()
        .createDeployment()
        .addClasspathResource("org/activiti/engine/test/db/oneJobProcess.bpmn20.xml")
        .deploy();
      
      assertEquals(1, engine1.getRepositoryService().createDeploymentQuery().count());
      assertEquals(0, engine2.getRepositoryService().createDeploymentQuery().count());
      
    } finally {
      
      engine1.close();
      engine2.close();
      
    }   
  }
  
 public void testPerformDatabaseSchemaOperationDrop() {
    
    ProcessEngineConfigurationImpl config1 = (ProcessEngineConfigurationImpl) ProcessEngineConfigurationImpl.createProcessEngineConfigurationFromResource("org/activiti/engine/test/db/prefix1.activiti.cfg.xml");
    ProcessEngineConfigurationImpl config2 = (ProcessEngineConfigurationImpl) ProcessEngineConfigurationImpl.createProcessEngineConfigurationFromResource("org/activiti/engine/test/db/prefix2.activiti.cfg.xml");
        
    config1.buildProcessEngine();
    config2.buildProcessEngine();
    
    Command<Void> dropCommand = new Command<Void>() {
      public Void execute(CommandContext commandContext) {
        commandContext.getDbSqlSession().dbSchemaDrop();
        return null;
      }
    };
    
    config1.getCommandExecutorTxRequired().execute(dropCommand);
    config2.getCommandExecutorTxRequired().execute(dropCommand);    
    
  }

}
