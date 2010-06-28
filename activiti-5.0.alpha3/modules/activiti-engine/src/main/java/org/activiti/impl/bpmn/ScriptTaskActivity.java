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
package org.activiti.impl.bpmn;

import org.activiti.impl.execution.ExecutionImpl;
import org.activiti.impl.scripting.ScriptingEngines;
import org.activiti.pvm.ActivityExecution;


/**
 * activity implementation of the BPMN 2.0 script task.
 * 
 * @author Joram Barrez
 */
public class ScriptTaskActivity extends TaskActivity {
  
  protected String script;
  
  protected String language;
  
  public ScriptTaskActivity() {
    
  }
  
  public ScriptTaskActivity(String script, String language) {
    this.script = script;
    this.language = language;
  }
  
  public void execute(ActivityExecution execution) throws Exception {
    ScriptingEngines.getScriptingEngines().evaluate(script, language, (ExecutionImpl) execution);
    leave(execution, true);
  }

  public void setScript(String script) {
    this.script = script;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
  
}
