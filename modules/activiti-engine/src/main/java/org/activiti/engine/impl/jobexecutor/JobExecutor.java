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

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.cmd.AcquireJobsCmd;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.runtime.Job;

/**
 * <p>Interface to the work management component of activiti.</p>
 * 
 * <p>This component is responsible for performing all background work 
 * ({@link Job Jobs}) scheduled by activiti.</p>
 * 
 * <p>You should generally only have one of these per Activiti instance in a JVM.
 * In clustered situations, you can have multiple of these running against the
 * same queue + pending job list.</p>
 * 
 * @author Daniel Meyer
 */
public abstract class JobExecutor {

  private static Logger log = Logger.getLogger(JobExecutor.class.getName());

  protected CommandExecutor commandExecutor;
  protected boolean isAutoActivate = false;

  protected int maxJobsPerAcquisition = 3;
  protected int waitTimeInMillis = 5 * 1000;
  protected String lockOwner = UUID.randomUUID().toString();
  protected int lockTimeInMillis = 5 * 60 * 1000;
  
  protected boolean isActive = false;
  
  protected AcquireJobsRunnable acquireJobsRunnable;
  protected AcquireJobsCmd acquireJobsCmd;
  
  public synchronized void start() {
    if (isActive) {
      log.info("Ignoring duplicate JobExecutor start invocation");
      return;
    }
    log.info("Starting up the JobExecutor["+getClass().getName()+"].");
    ensureInitialization();    
    startExecutingJobs();
    scheduleAcquisitionTimer(acquireJobsRunnable, 0, waitTimeInMillis);    
    isActive = true;
  }
  
  public synchronized void shutdown() {
    if (!isActive) {
      log.info("Ignoring request to shut down non-active JobExecutor");
      return;
    }
    log.info("Shutting down the JobExecutor["+getClass().getName()+"].");
    acquireJobsRunnable.interrupt();
    cancelAcquisitionTimer();
    stopExecutingJobs();
    isActive = false;
  }
  
  public void jobWasAdded() {
    if(isActive) {
      // run immediately (ASAP)
      rescheduleAcquisition(0);
    }
  }
  
  public void rescheduleAcquisition(long waitUntilStart) {
    if(! isActive) {
      throw new ActivitiException("JobExecutor cannot accept work: not active.");
    } 
    cancelAcquisitionTimer();
    scheduleAcquisitionTimer(acquireJobsRunnable, waitUntilStart, waitTimeInMillis);
  }
  
  public void executeJobs(List<String> jobIds) {
    if(! isActive) {
      throw new ActivitiException("JobExecutor cannot accept work: not active.");
    } 
    execute(jobIds);
  }
  

  protected void ensureInitialization() {
   if(acquireJobsCmd == null) {
     acquireJobsCmd = new AcquireJobsCmd(this);     
   }
   if(acquireJobsRunnable == null) {
     acquireJobsRunnable = new AcquireJobsRunnable(this);
   }
  }
  
  protected abstract void startExecutingJobs();
  protected abstract void stopExecutingJobs(); 

  protected abstract void scheduleAcquisitionTimer(Runnable acquireJobsRunnable, long startDelayInMillis, long period);
  protected abstract void cancelAcquisitionTimer();
  protected abstract void execute(List<String> jobIds);
  
  // getters and setters //////////////////////////////////////////////////////

  public CommandExecutor getCommandExecutor() {
    return commandExecutor;
  }

  public int getWaitTimeInMillis() {
    return waitTimeInMillis;
  }

  public void setWaitTimeInMillis(int waitTimeInMillis) {
    this.waitTimeInMillis = waitTimeInMillis;
  }

  public int getLockTimeInMillis() {
    return lockTimeInMillis;
  }

  public void setLockTimeInMillis(int lockTimeInMillis) {
    this.lockTimeInMillis = lockTimeInMillis;
  }

  public boolean isActive() {
    return isActive;
  }

  public String getLockOwner() {
    return lockOwner;
  }

  public void setLockOwner(String lockOwner) {
    this.lockOwner = lockOwner;
  }

  public boolean isAutoActivate() {
    return isAutoActivate;
  }

  public void setCommandExecutor(CommandExecutor commandExecutor) {
    this.commandExecutor = commandExecutor;
  }

  public void setAutoActivate(boolean isAutoActivate) {
    this.isAutoActivate = isAutoActivate;
  }

  public int getMaxJobsPerAcquisition() {
    return maxJobsPerAcquisition;
  }
  
  public void setMaxJobsPerAcquisition(int maxJobsPerAcquisition) {
    this.maxJobsPerAcquisition = maxJobsPerAcquisition;
  }
  
  
  public AcquireJobsCmd getAcquireJobsCmd() {
    return acquireJobsCmd;
  }
    
  public void setAcquireJobsCmd(AcquireJobsCmd acquireJobsCmd) {
    this.acquireJobsCmd = acquireJobsCmd;
  }
    
  public AcquireJobsRunnable getAcquireJobsRunnable() {
    return acquireJobsRunnable;
  }
    
  public void setAcquireJobsRunnable(AcquireJobsRunnable acquireJobsRunnable) {
    this.acquireJobsRunnable = acquireJobsRunnable;
  }
    
}
