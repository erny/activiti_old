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
package org.activiti.impl.jobexecutor;

import java.util.List;

import org.activiti.impl.interceptor.CommandExecutor;
import org.activiti.impl.job.JobHandlers;


/**
 * @author Tom Baeyens
 */
public class ExecuteJobsRunnable implements Runnable {

  private final CommandExecutor commandExecutor;
  private final List<String> jobIds;
  private final JobHandlers jobHandlers;
  private final JobExecutor jobExecutor;
  
  public ExecuteJobsRunnable(CommandExecutor commandExecutor, List<String> jobIds, JobHandlers jobHandlers, JobExecutor jobExecutor) {
    this.commandExecutor = commandExecutor;
    this.jobIds = jobIds;
    this.jobHandlers = jobHandlers;
    this.jobExecutor = jobExecutor;
  }

  public void run() {
    for (String jobId: jobIds) {
      try {
        commandExecutor.execute(new ExecuteJobsCmd(jobHandlers, jobId));
      } catch (Throwable exception) {
        commandExecutor.execute(new DecrementJobRetriesCmd(jobExecutor, jobId, exception));
      }
    }
  }
}
