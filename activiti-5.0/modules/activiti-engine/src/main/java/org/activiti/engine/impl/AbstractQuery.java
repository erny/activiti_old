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
package org.activiti.engine.impl;

import java.util.List;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.query.Query;
import org.activiti.engine.query.QueryProperty;
import org.activiti.engine.repository.DeploymentQuery;


/**
 * Abstract superclass for all query types.
 *  
 * @author Joram Barrez
 */
public abstract class AbstractQuery<T extends Query<?,?>, U> implements Command<Object>, Query<T,U>{
  
  public static final String SORTORDER_ASC = "asc";
  public static final String SORTORDER_DESC = "desc";
  
  private static enum ResultType {
    LIST, LIST_PAGE, SINGLE_RESULT, COUNT
  }
    
  protected CommandExecutor commandExecutor;
  protected String orderBy;
  
  protected int firstResult;
  protected int maxResults;
  protected ResultType resultType;
 
  protected AbstractQuery() {
  }

  protected AbstractQuery(CommandExecutor commandExecutor) {
    this.commandExecutor = commandExecutor;
  }
  
  protected QueryProperty orderProperty;

  public T orderBy(QueryProperty property) {
    this.orderProperty = property;
    return (T) this;
  }
  
  public T asc() {
    return direction(Direction.ASCENDING);
  }
  
  public T desc() {
    return direction(Direction.DESCENDING);
  }
  
  public T direction(Direction direction) {
    if (orderProperty==null) {
      throw new ActivitiException("You should call any of the orderBy methods first before specifying a direction");
    }
    addOrder(orderProperty.getName(), direction.getName());
    orderProperty = null;
    return (T) this;
  }
  
  protected void checkQueryOk() {
    if (orderProperty != null) {
      throw new ActivitiException("Invalid query: call asc() or desc() after using orderByXX()");
    }
  }
  
  @SuppressWarnings("unchecked")
  public U singleResult() {
    this.resultType = ResultType.SINGLE_RESULT;
    return (U) commandExecutor.execute(this);
  }

  @SuppressWarnings("unchecked")
  public List<U> list() {
    this.resultType = ResultType.LIST;
    return (List<U>) commandExecutor.execute(this);
  }
  
  @SuppressWarnings("unchecked")
  public List<U> listPage(int firstResult, int maxResults) {
    this.firstResult = firstResult;
    this.maxResults = maxResults;
    this.resultType = ResultType.LIST_PAGE;
    return (List<U>) commandExecutor.execute(this);
  }
  
  public long count() {
    this.resultType = ResultType.COUNT;
    return (Long) commandExecutor.execute(this);
  }
  
  public Object execute(CommandContext commandContext) {
    if (resultType==ResultType.LIST) {
      return executeList(commandContext, null);
    } else if (resultType==ResultType.SINGLE_RESULT) {
      return executeSingleResult(commandContext);
    } else if (resultType==ResultType.LIST_PAGE) {
      return executeList(commandContext, new Page(firstResult, maxResults));
    } else {
      return executeCount(commandContext);
    }
  }

  public abstract long executeCount(CommandContext commandContext);
  
  /**
   * Executes the actual query to retrieve the list of results.
   * @param page used if the results must be paged. If null, no paging will be applied. 
   */
  public abstract List<U> executeList(CommandContext commandContext, Page page);
  
  public U executeSingleResult(CommandContext commandContext) {
    List<U> results = executeList(commandContext, null);
    if (results.size() == 1) {
      return results.get(0);
    } else if (results.size() > 1) {
     throw new ActivitiException("Query return "+results.size()+" results instead of max 1");
    } 
    return null;
  }

  protected void addOrder(String column, String sortOrder) {
    if (orderBy==null) {
      orderBy = "";
    } else {
      orderBy = orderBy+", ";
    }
    orderBy = orderBy+column+" "+sortOrder;
  }

  public String getOrderBy() {
    return orderBy;
  }
}
