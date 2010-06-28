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
package org.activiti.test.jobexecutor;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.activiti.impl.ProcessEngineImpl;
import org.activiti.impl.interceptor.Command;
import org.activiti.impl.interceptor.CommandContext;
import org.activiti.impl.interceptor.CommandExecutor;
import org.activiti.impl.job.MessageImpl;
import org.activiti.impl.job.TimerImpl;
import org.activiti.impl.jobexecutor.AcquireJobsCmd;
import org.activiti.impl.jobexecutor.AcquiredJobs;
import org.activiti.impl.jobexecutor.ExecuteJobsCmd;
import org.activiti.impl.time.Clock;
import org.junit.Test;

/**
 * @author Tom Baeyens
 */
public class JobExecutorCmdHappyTest extends JobExecutorTestCase {

  @Test
  public void testJobCommandsWithMessage() {
    ProcessEngineImpl processEngineImpl = (ProcessEngineImpl) deployer.getProcessEngine();
    CommandExecutor commandExecutor = processEngineImpl.getProcessEngineConfiguration().getCommandExecutor();
    String jobId = commandExecutor.execute(new Command<String>() {

      public String execute(CommandContext commandContext) {
        MessageImpl message = createTweetMessage("i'm coding a test");
        commandContext.getMessageSession().send(message);
        return message.getId();
      }
    });

    AcquiredJobs acquiredJobs = commandExecutor.execute(new AcquireJobsCmd());
    List<List<String>> jobIdsList = acquiredJobs.getJobIdsList();
    assertEquals(1, jobIdsList.size());

    List<String> jobIds = jobIdsList.get(0);

    List<String> expectedJobIds = new ArrayList<String>();
    expectedJobIds.add(jobId);

    assertEquals(expectedJobIds, new ArrayList<String>(jobIds));
    assertEquals(0, tweetHandler.getMessages().size());

    commandExecutor.execute(new ExecuteJobsCmd(jobId));

    assertEquals("i'm coding a test", tweetHandler.getMessages().get(0));
    assertEquals(1, tweetHandler.getMessages().size());
  }

  static final long SOME_TIME = 928374923546L;
  static final long SECOND = 1000;

  @Test
  public void testJobCommandsWithTimer() {
    // clock gets automatically reset in LogTestCase.runTest
    Clock.setCurrentTime(new Date(SOME_TIME));

    ProcessEngineImpl processEngineImpl = (ProcessEngineImpl) deployer.getProcessEngine();
    CommandExecutor commandExecutor = processEngineImpl.getProcessEngineConfiguration().getCommandExecutor();
    String jobId = commandExecutor.execute(new Command<String>() {

      public String execute(CommandContext commandContext) {
        TimerImpl timer = createTweetTimer("i'm coding a test", new Date(SOME_TIME + (10 * SECOND)));
        commandContext.getTimerSession().schedule(timer);
        return timer.getId();
      }
    });

    AcquiredJobs acquiredJobs = commandExecutor.execute(new AcquireJobsCmd());
    List<List<String>> jobIdsList = acquiredJobs.getJobIdsList();
    assertEquals(0, jobIdsList.size());

    List<String> expectedJobIds = new ArrayList<String>();

    Clock.setCurrentTime(new Date(SOME_TIME + (20 * SECOND)));

    acquiredJobs = commandExecutor.execute(new AcquireJobsCmd());
    jobIdsList = acquiredJobs.getJobIdsList();
    assertEquals(1, jobIdsList.size());

    List<String> jobIds = jobIdsList.get(0);

    expectedJobIds.add(jobId);
    assertEquals(expectedJobIds, new ArrayList<String>(jobIds));

    assertEquals(0, tweetHandler.getMessages().size());

    commandExecutor.execute(new ExecuteJobsCmd(jobId));

    assertEquals("i'm coding a test", tweetHandler.getMessages().get(0));
    assertEquals(1, tweetHandler.getMessages().size());
  }
}
