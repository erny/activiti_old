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
package org.activiti.engine.impl.jobexecutor;

import org.activiti.engine.impl.cfg.MessageSession;
import org.activiti.engine.impl.cfg.MessageSessionFactory;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.Session;


/**
 * @author Tom Baeyens
 */
public class JobExecutorMessageSessionFactory implements MessageSessionFactory {

  private JobExecutor jobExecutor;

  public JobExecutorMessageSessionFactory() {
  }

  public JobExecutorMessageSessionFactory(JobExecutor jobExecutor) {
    this.jobExecutor = jobExecutor;
  }
  
  public MessageSession openMessageSession(CommandContext commandContext) {
    return new JobExecutorMessageSession(commandContext, jobExecutor);
  }

  public Session openSession() {
    return new JobExecutorMessageSession();
  }

}
