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

import java.util.Date;
import java.util.List;

import org.activiti.impl.interceptor.Command;
import org.activiti.impl.interceptor.CommandContext;
import org.activiti.impl.job.TimerImpl;


/**
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class GetUnlockedTimersByDuedateCmd implements Command<List<TimerImpl>> {

  protected Date duedate;
  
  protected int nrOfTimers = -1;
  
  public GetUnlockedTimersByDuedateCmd(Date duedate, int nrOfTimers) {
	  this.duedate = duedate;
	  this.nrOfTimers = nrOfTimers;
  }

  public List<TimerImpl> execute(CommandContext commandContext) {
    return commandContext.getPersistenceSession().findUnlockedTimersByDuedate(duedate, nrOfTimers);
  }
}
