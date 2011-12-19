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

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.cmd.AcquireJobsCmd;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.persistence.entity.TimerEntity;
import org.activiti.engine.impl.util.ClockUtil;

/**
 * 
 * @author Daniel Meyer
 */
public class AcquireJobsRunnable implements Runnable {

  private static Logger log = Logger.getLogger(AcquireJobsRunnable.class.getName());

  protected final JobExecutor jobExecutor;

  protected long millisToWait = 0;
  protected float waitIncreaseFactor = 2;
  protected long maxWait = 60 * 1000;

  protected volatile boolean isInterrupted = false;

  public AcquireJobsRunnable(JobExecutor jobExecutor) {
    this.jobExecutor = jobExecutor;
  }

  public void run() {
    isInterrupted = false;

    final CommandExecutor commandExecutor = jobExecutor.getCommandExecutor();
    final int maxJobsPerAcquisition = jobExecutor.getMaxJobsPerAcquisition();
    while (!isInterrupted) {      
      try {

        // perform the actual acquisition
        int numberOfAcquiredJobs = performJobAcquisition();

        millisToWait = jobExecutor.getWaitTimeInMillis();
        if (numberOfAcquiredJobs < maxJobsPerAcquisition) {

          // check if the next timer should fire before the normal sleep time is over
          Date duedate = new Date(ClockUtil.getCurrentTime().getTime() + millisToWait);
          List<TimerEntity> nextTimers = commandExecutor.execute(new GetUnlockedTimersByDuedateCmd(duedate, new Page(0, 1)));

          if (!nextTimers.isEmpty()) {
            long millisTillNextTimer = nextTimers.get(0).getDuedate().getTime() - ClockUtil.getCurrentTime().getTime();
            if (millisTillNextTimer < millisToWait) {
              jobExecutor.rescheduleAcquisition(millisTillNextTimer);
              break;
            }
          }else {
            break;
          }          
        } 
      } catch (Exception e) {
        log.log(Level.SEVERE, "exception during job acquisition: " + e.getMessage(), e);
        millisToWait *= waitIncreaseFactor;
        if (millisToWait > maxWait) {
          millisToWait = maxWait;
        }
        jobExecutor.rescheduleAcquisition(millisToWait);
        break;
      }
    }
  }

  protected int performJobAcquisition() {
    AcquireJobsCmd acquireJobsCmd = jobExecutor.getAcquireJobsCmd();
    CommandExecutor commandExecutor = jobExecutor.getCommandExecutor();
    AcquiredJobs acquiredJobs = commandExecutor.execute(acquireJobsCmd);

    for (List<String> jobIds : acquiredJobs.getJobIdBatches()) {
      jobExecutor.executeJobs(jobIds);
    }

    return acquiredJobs.size();
  }

  public void interrupt() {
    isInterrupted = true;   
  }

}
