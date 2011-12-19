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

import java.util.TimerTask;

/**
 * Simple adapter to wrap a {@link Runnable} as a {@link java.util.TimerTask}
 * 
 * @author Daniel Meyer
 */
public class TimerTaskAdapter extends TimerTask {

  private final Runnable delegate;

  public TimerTaskAdapter(Runnable r) {
    this.delegate = r;
  }

  @Override
  public void run() {
    delegate.run();
  }

}
