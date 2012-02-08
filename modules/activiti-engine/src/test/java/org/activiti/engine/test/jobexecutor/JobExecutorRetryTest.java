package org.activiti.engine.test.jobexecutor;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.persistence.entity.JobEntity;
import org.activiti.engine.impl.persistence.entity.MessageEntity;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;

/**
 * Test case for jira ACT-1046
 * 
 * @author Dawid Wrzosek
 */
public class JobExecutorRetryTest extends PluggableActivitiTestCase {

  protected TweetExceptionHandler tweetExceptionHandler = new TweetExceptionHandler();

  protected CommandExecutor commandExecutor;

  public void setUp() throws Exception {
    processEngineConfiguration.getJobHandlers().put(tweetExceptionHandler.getType(), tweetExceptionHandler);
    processEngineConfiguration.getJobExecutor().setRetriesDelayInMills(2000);
    processEngineConfiguration.getJobExecutor().setWaitTimeInMillis(1000);
    this.commandExecutor = processEngineConfiguration.getCommandExecutorTxRequired();
  }

  public void tearDown() throws Exception {
    processEngineConfiguration.getJobHandlers().remove(tweetExceptionHandler.getType());
    processEngineConfiguration.getJobExecutor().setAmountOfRetries(JobEntity.DEFAULT_RETRIES);
    processEngineConfiguration.getJobExecutor().setRetriesDelayInMills(0);
  }

  public void testDelayWithoutRetry() throws Exception {

    // when

    tweetExceptionHandler.setExceptionsRemaining(1);

    String jobId = commandExecutor.execute(new Command<String>() {

      public String execute(CommandContext commandContext) {
        MessageEntity message = createTweetExceptionMessage();
        commandContext.getJobManager().send(message);

        return message.getId();
      }
    });

    waitForJobExecutor(1500, 1000, false);

    // then
    JobEntity job = (JobEntity) managementService.createJobQuery().jobId(jobId).singleResult();
    assertNotNull(job);
    assertEquals(2, job.getRetries());
    assertNotNull(job.getLockExpirationTime());

    // clean up
    processEngineConfiguration.getJobExecutor().setRetriesDelayInMills(0);
    waitForJobExecutor(100000, 1000, true);

  }

  public void testDelayWith1Retry() throws Exception {

    // when
    tweetExceptionHandler.setExceptionsRemaining(2);

    String jobId = commandExecutor.execute(new Command<String>() {

      public String execute(CommandContext commandContext) {
        MessageEntity message = createTweetExceptionMessage();
        commandContext.getJobManager().send(message);

        return message.getId();
      }
    });

    waitForJobExecutor(4000, 200, false);

    // then
    JobEntity job = (JobEntity) managementService.createJobQuery().jobId(jobId).singleResult();

    assertNotNull(job);
    assertEquals(1, job.getRetries());
    assertNotNull(job.getLockExpirationTime());
    // clean up
    processEngineConfiguration.getJobExecutor().setRetriesDelayInMills(0);
    waitForJobExecutor(100000, 1000, true);

  }

  public void testAmountOfRetries() throws Exception {

    // when
    tweetExceptionHandler.setExceptionsRemaining(10);
    processEngineConfiguration.getJobExecutor().setAmountOfRetries(5);

    String jobId = commandExecutor.execute(new Command<String>() {

      public String execute(CommandContext commandContext) {
        MessageEntity message = createTweetExceptionMessage();
        message.setRetries(processEngineConfiguration.getJobExecutor().getAmountOfRetries());

        commandContext.getJobManager().send(message);

        return message.getId();
      }
    });

    waitForJobExecutor(15000, 100, true);

    // then
    JobEntity job = (JobEntity) managementService.createJobQuery().jobId(jobId).singleResult();
    assertNotNull(jobId);
    assertEquals(0, job.getRetries());
    assertEquals(5, tweetExceptionHandler.getExceptionsRemaining());

    // clean up
    tweetExceptionHandler.setExceptionsRemaining(0);
    managementService.executeJob(jobId);

  }

  protected MessageEntity createTweetExceptionMessage() {
    MessageEntity message = new MessageEntity();
    message.setJobHandlerType("tweet-exception");
    return message;
  }

}
