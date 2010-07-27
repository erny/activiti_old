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
package org.activiti.impl.msg;

import org.activiti.impl.interceptor.CommandContext;
import org.activiti.impl.job.MessageImpl;
import org.activiti.impl.jobexecutor.JobExecutor;
import org.activiti.impl.persistence.PersistenceSession;
import org.activiti.impl.tx.TransactionState;


/**
 * @author Tom Baeyens
 */
public class JobExecutorMessageSession implements MessageSession {

  private final CommandContext commandContext;
  private final JobExecutor jobExecutor;
  
  public JobExecutorMessageSession(CommandContext commandContext, JobExecutor jobExecutor) {
    this.commandContext = commandContext;
    this.jobExecutor = jobExecutor;
  }

  public JobExecutorMessageSession() {
    this.commandContext = CommandContext.getCurrentCommandContext();
    this.jobExecutor = commandContext.getProcessEngineConfiguration().getJobExecutor();
  }

  public void send(MessageImpl message) {
    PersistenceSession persistenceSession = commandContext.getPersistenceSession();
    persistenceSession.insert(message);
    
    commandContext
      .getTransactionContext()
      .addTransactionListener(TransactionState.COMMITTED, new MessageAddedNotification(jobExecutor));
  }

  public void close() {
  }

  public void flush() {
  }
}
