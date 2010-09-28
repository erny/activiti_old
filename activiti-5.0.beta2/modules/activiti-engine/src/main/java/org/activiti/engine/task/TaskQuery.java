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
package org.activiti.engine.task;

import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.task.TaskQueryProperty;

/**
 * Allows programmatic querying of {@link Task}s;
 * 
 * @author Joram Barrez
 */
public interface TaskQuery {

  /**
   * Only select tasks with the given task id (in practice, there will be
   * maximum one of this kind)
   */
  TaskQuery taskId(String taskId);

  /** Only select tasks with the given name */
  TaskQuery name(String name);
  
  /** Only select tasks with a name matching the parameter.
   *  The syntax is that of SQL: for example usage: nameLike(%activiti%)*/
  TaskQuery nameLike(String nameLike);
  
  /** Only select tasks with the given description. */
  TaskQuery description(String description);
  
  /** Only select tasks with a description matching the parameter .
   *  The syntax is that of SQL: for example usage: descriptionLike(%activiti%)*/
  TaskQuery descriptionLike(String descriptionLike);
  
  /** Only select tasks with the given priority. */
  TaskQuery priority(Integer priority);

  /** Only select tasks which are assigned to the given user. */
  TaskQuery assignee(String assignee);

  /** Only select tasks for which the given user is a candidate. */
  TaskQuery candidateUser(String candidateUser);

  /** Only select tasks for which users in the given group are candidates. */
  TaskQuery candidateGroup(String candidateGroup);

  /** Only select tasks for the given process instance id. */
  TaskQuery processInstanceId(String processInstanceId);

  /** Only select tasks for the given execution. */
  TaskQuery executionId(String executionId);

  // ordering ////////////////////////////////////////////////////////////
  
  /** Order by task id (needs to be followed by {@link #asc()} or {@link #desc()}). */
  TaskQuery orderByTaskId();
  
  /** Order by task name (needs to be followed by {@link #asc()} or {@link #desc()}). */
  TaskQuery orderByName();
  
  /** Order by description (needs to be followed by {@link #asc()} or {@link #desc()}). */
  TaskQuery orderByDescription();
  
  /** Order by priority (needs to be followed by {@link #asc()} or {@link #desc()}). */
  TaskQuery orderByPriority();
  
  /** Order by assignee (needs to be followed by {@link #asc()} or {@link #desc()}). */
  TaskQuery orderByAssignee();
  
  /** Order by process instance id (needs to be followed by {@link #asc()} or {@link #desc()}). */
  TaskQuery orderByProcessInstanceId();
  
  /** Order by execution id (needs to be followed by {@link #asc()} or {@link #desc()}). */
  TaskQuery orderByExecutionId();
  
  /** Order by the given property (needs to be followed by {@link #asc()} or {@link #desc()}). */
  TaskQuery orderBy(TaskQueryProperty property);
  
  /** Order the results ascending on the given property as
   * defined in this class (needs to come after a call to one of the orderByXxxx methods). */
  TaskQuery asc();

  /** Order the results descending on the given property as
   * defined in this class (needs to come after a call to one of the orderByXxxx methods). */
  TaskQuery desc();

  // results ////////////////////////////////////////////////////////////

  /** Execute the query and return the number of results. */
  long count();

  /**
   * Executes the query and returns the {@link Task}.
   * 
   * @throws ActivitiException
   *           when the query results in more than one process definition.
   */
  Task singleResult();

  /** Executes the query and get a list of {@link Task}s as the result. */
  List<Task> list();

  /** Executes the query and get a list of {@link Task}s as the result. */
  List<Task> listPage(int firstResult, int maxResults);

}
