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

package org.activiti.impl.cmd;

import java.util.ArrayList;
import java.util.List;

import org.activiti.impl.interceptor.Command;
import org.activiti.impl.interceptor.CommandContext;
import org.activiti.impl.job.JobImpl;
import org.activiti.impl.persistence.PersistenceSession;


/**
 * @author Tom Baeyens
 */
public class DeleteJobsCmd implements Command<Void> {

  List<String> jobIds;
  
  public DeleteJobsCmd(List<String> jobIds) {
    this.jobIds = jobIds;
  }

  public DeleteJobsCmd(String jobId) {
    this.jobIds = new ArrayList<String>();
    jobIds.add(jobId);
  }

  public Void execute(CommandContext commandContext) {
    PersistenceSession persistenceSession = commandContext.getPersistenceSession();
    for (String jobId: jobIds) {
      JobImpl job = persistenceSession.findJobById(jobId);
      persistenceSession.delete(job);
    }
    return null;
  }
}
