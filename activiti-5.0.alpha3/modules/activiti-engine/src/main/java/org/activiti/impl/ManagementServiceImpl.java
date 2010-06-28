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
package org.activiti.impl;

import java.util.Map;

import org.activiti.JobQuery;
import org.activiti.ManagementService;
import org.activiti.TableMetaData;
import org.activiti.TablePageQuery;
import org.activiti.impl.cmd.GetTableCountCmd;
import org.activiti.impl.cmd.GetTableMetaDataCmd;
import org.activiti.impl.interceptor.CommandExecutor;
import org.activiti.impl.job.JobQueryImpl;
import org.activiti.impl.query.TablePageQueryImpl;


/**
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class ManagementServiceImpl implements ManagementService {

  /** must be injected with {@link #setCommandExecutor(CommandExecutor)} */
  protected CommandExecutor commandExecutor;
  
  public Map<String, Long> getTableCount() {
    return commandExecutor.execute(new GetTableCountCmd());
  }
  
  public TableMetaData getTableMetaData(String tableName) {
    return commandExecutor.execute(new GetTableMetaDataCmd(tableName));
  }

  public TablePageQuery createTablePageQuery() {
    return new TablePageQueryImpl(commandExecutor);
  }
  
  public JobQuery createJobQuery() {
    return new JobQueryImpl(commandExecutor);
  }
  
  // getters and setters //////////////////////////////////////////////////////
  
  public CommandExecutor getCommandExecutor() {
    return commandExecutor;
  }
  
  public void setCommandExecutor(CommandExecutor commandExecutor) {
    this.commandExecutor = commandExecutor;
  }
}
