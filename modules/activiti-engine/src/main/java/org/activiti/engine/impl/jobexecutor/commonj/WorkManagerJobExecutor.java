package org.activiti.engine.impl.jobexecutor.commonj;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javax.naming.InitialContext;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.jobexecutor.ExecuteJobsRunnable;
import org.activiti.engine.impl.jobexecutor.JobExecutor;

import commonj.timers.Timer;
import commonj.timers.TimerManager;
import commonj.work.WorkException;
import commonj.work.WorkManager;
import commonj.work.WorkRejectedException;

/**
 * <p>{@link JobExecutor} implementation delegating to a CommonJ WorkManager</p>
 * 
 * <p><em>This implementation is intended to be used in environments where 
 * self-management of threads is not permitted and a CommonJ Work Management 
 * implementation is available (such as in IBM WebSphere 6.0+ and BEA WebLogic
 * 9.0+)</em></p> 
 * 
 * @author Daniel Meyer
 * @since 5.9
 */
public class WorkManagerJobExecutor extends JobExecutor {
  
  private static Logger log = Logger.getLogger(WorkManagerJobExecutor.class.getName());
  
  protected String workManagerJndiName = "java:comp/env/wm/default";
  protected String timerManagerJndiName = "java:comp/env/tm/default";
  
  protected TimerManager timerManager;
  protected WorkManager workManager;

  protected Timer scheduledAcquisition;

  protected void startExecutingJobs() {
    if(timerManager == null) {
      timerManager = lookupTimerManager();
    }
    if(workManager == null) {
      workManager = lookupWorkMananger();
    }
  }

  protected void stopExecutingJobs() {
    timerManager.stop();
    timerManager = null;
  }
  
  @Override
  protected void scheduleAcquisitionTimer(Runnable acquireJobsRunnable, long startDelayInMillis, long period) {
    scheduledAcquisition = timerManager.schedule(new TimerListenerAdapter(acquireJobsRunnable), startDelayInMillis, period);
  }

  @Override
  protected void cancelAcquisitionTimer() {
    if(scheduledAcquisition == null) {
      return;
    }
    scheduledAcquisition.cancel();
  }

  @Override
  protected void execute(List<String> jobIds) {
    ExecuteJobsRunnable executeJobsRunnable = new ExecuteJobsRunnable(commandExecutor,jobIds);
    try {
      
      workManager.schedule(new WorkAdapter(executeJobsRunnable));
      
    } catch (WorkRejectedException e) {
      log.log(Level.FINE, "Work was rejected by CommonJ WorkManager: " + jobIds 
        + ": executing " + ExecuteJobsRunnable.class.getName() + " in caller thread.", e);
      // execute the runnable in the caller thread (potentially blocking job acquisition).
      executeJobsRunnable.run();
    } catch (WorkException e) {
      throw new ActivitiException("Could not submit work to CommonJ Work Mananger", e);
    }
  }
  
  protected TimerManager lookupTimerManager() {
    try {
      InitialContext initialContext = new InitialContext();
      return (TimerManager) initialContext.lookup(timerManagerJndiName);
    } catch (Exception e) {
      throw new ActivitiException("Error while starting JobExecutor: could not look up CommonJ TimerManager in Jndi: "+e.getMessage(), e);
    }   
  }
  
  protected WorkManager lookupWorkMananger() {
    try {
      InitialContext initialContext = new InitialContext();
      return (WorkManager) initialContext.lookup(workManagerJndiName);
    } catch (Exception e) {
      throw new ActivitiException("Error while starting JobExecutor: could not look up CommonJ WorkManager in Jndi: "+e.getMessage(), e);
    }   
  }
  
  // getters setters

  
  public String getWorkManagerJndiName() {
    return workManagerJndiName;
  }

  
  public void setWorkManagerJndiName(String workManagerJndiName) {
    this.workManagerJndiName = workManagerJndiName;
  }

  
  public String getTimerManagerJndiName() {
    return timerManagerJndiName;
  }

  
  public void setTimerManagerJndiName(String timerManagerJndiName) {
    this.timerManagerJndiName = timerManagerJndiName;
  }

  
  public TimerManager getTimerManager() {
    return timerManager;
  }

  
  public void setTimerManager(TimerManager timerManager) {
    this.timerManager = timerManager;
  }

  
  public WorkManager getWorkManager() {
    return workManager;
  }

  
  public void setWorkManager(WorkManager workManager) {
    this.workManager = workManager;
  }
  

}
