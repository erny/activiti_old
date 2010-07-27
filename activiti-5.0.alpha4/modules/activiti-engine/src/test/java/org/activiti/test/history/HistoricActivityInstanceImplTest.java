/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.test.history;

import static org.junit.Assert.*;

import java.util.Date;

import org.activiti.impl.history.HistoricActivityInstanceImpl;
import org.junit.Test;

/**
 * @author Christian Stettler
 */
public class HistoricActivityInstanceImplTest {

  @Test
  public void testInitializeHistoricActivityInstance() {
    Date startTime = new Date();

    HistoricActivityInstanceImpl historicActivityInstance = new HistoricActivityInstanceImpl("activityId", "activityName", "activityType", "processInstanceId", "processDefinitionId", startTime);

    assertEquals("activityName", historicActivityInstance.getActivityName());
    assertEquals("activityType", historicActivityInstance.getActivityType());
    assertEquals("processInstanceId", historicActivityInstance.getProcessInstanceId());
    assertEquals("processDefinitionId", historicActivityInstance.getProcessDefinitionId());
    assertEquals(startTime, historicActivityInstance.getStartTime());

    assertNull(historicActivityInstance.getEndTime());
    assertNull(historicActivityInstance.getDurationInMillis());
  }

  @Test
  public void testMarkHistoricActivityInstanceEnded() {
    Date startTime = new Date();
    Date endTime = new Date(startTime.getTime() + 1234);

    HistoricActivityInstanceImpl historicActivityInstance = new HistoricActivityInstanceImpl("activityId", "activityName", "activityType", "processInstanceId", "processDefinitionId", startTime);
    historicActivityInstance.markEnded(endTime);

    assertEquals(endTime, historicActivityInstance.getEndTime());
    assertEquals(Long.valueOf(1234), historicActivityInstance.getDurationInMillis());
  }

  @Test
  public void testMandatoryStateForHistoricProcessInstanceInitialization() {
    assertIllegalArgumentException("activity id", new Runnable() {
      public void run() {
        new HistoricActivityInstanceImpl(null, "activityName", "activityType", "processInstanceId", "processDefinitionId", new Date());
      }
    });

    assertIllegalArgumentException("activity type", new Runnable() {
      public void run() {
        new HistoricActivityInstanceImpl("activityId", "activityName", null, "processInstanceId", "processDefinitionId", new Date());
      }
    });

    assertIllegalArgumentException("process instance id", new Runnable() {
      public void run() {
        new HistoricActivityInstanceImpl("activityId", "activityName", "activityType", null, "processDefinitionId", new Date());
      }
    });

    assertIllegalArgumentException("process definition id", new Runnable() {
      public void run() {
        new HistoricActivityInstanceImpl("activityId", "activityName", "activityType", "processInstanceId", null, new Date());
      }
    });

    assertIllegalArgumentException("start time", new Runnable() {
      public void run() {
        new HistoricActivityInstanceImpl("activityId", "activityName", "activityType", "processInstanceId", "processDefinitionId", null);
      }
    });

    new HistoricActivityInstanceImpl("activityId", null, "activityType", "processInstanceId", "processDefinitionId", new Date());    
  }

  @Test
  public void testMandatoryStateForMarkHistoricProcessInstanceEnded() {
    assertIllegalArgumentException("end time", new Runnable() {
      public void run() {
        HistoricActivityInstanceImpl historicActivityInstance = new HistoricActivityInstanceImpl("activityId", "activityName", "activityType", "processInstanceId", "processDefinitionId", new Date());
        historicActivityInstance.markEnded(null);
      }
    });
  }

  @Test
  public void testEndTimeMustBeAfterStartTime() {
    assertIllegalArgumentException("before start time", new Runnable() {
      public void run() {
        Date startTime = new Date();
        Date endTime = new Date(startTime.getTime() - 1000);

        HistoricActivityInstanceImpl historicActivityInstance = new HistoricActivityInstanceImpl("activityId", "activityName", "activityType", "processInstanceId", "processDefinitionId", startTime);
        historicActivityInstance.markEnded(endTime);
      }
    });
  }

  private static void assertIllegalArgumentException(String message, Runnable runnable) {
    try {
      runnable.run();
      fail("expected IllegalArgumentException with message '" + message + "'");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().toLowerCase().contains(message));
    }
  }

}
