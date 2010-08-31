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

import org.activiti.engine.impl.cfg.RuntimeSession;
import org.activiti.engine.impl.cfg.TransactionState;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.runtime.JobEntity;

/**
 * @author Tom Baeyens
 */
public class DecrementJobRetriesCmd implements Command<Object> {

  protected String jobId;
  protected Throwable exception;
  protected JobExecutor jobExecutor;

  public DecrementJobRetriesCmd(JobExecutor jobExecutor, String jobId, Throwable exception) {
    this.jobExecutor = jobExecutor;
    this.jobId = jobId;
    this.exception = exception;
  }

  public Object execute(CommandContext commandContext) {
    RuntimeSession runtimeSession = commandContext.getRuntimeSession();
    JobEntity job = runtimeSession.findJobById(jobId);
    job.setRetries(job.getRetries() - 1);
    job.setLockOwner(null);
    job.setLockExpirationTime(null);

    commandContext.getTransactionContext().addTransactionListener(TransactionState.COMMITTED, new MessageAddedNotification(jobExecutor));

    // TODO http://jira.codehaus.org/browse/ACT-87 store the exception in a byte array 
    // StringWriter stringWriter = new StringWriter();
    // exception.printStackTrace(new PrintWriter(stringWriter));
    // byte[] exceptionBytes = stringWriter.toString().getBytes();

    return null;
  }
}
