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
package org.activiti.explorer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;


/**
 * Wrapper around a {@link User}, containing the data of a logged in user
 * and adding data such as security roles, etc.
 * 
 * @author Joram Barrez
 */
public class LoggedInUser implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  protected User user;
  protected String password;
  protected boolean isUser;
  protected boolean isAdmin;
  protected List<Group> securityRoles = new ArrayList<Group>();
  protected List<Group> groups = new ArrayList<Group>();
  
  public LoggedInUser(User user, String password) {
    this.user = user;
    this.password = password;
  }
  
  public String getId() {
    return user.getId();
  }
  public String getFirstName() {
    return user.getFirstName();
  }
  public String getLastName() {
    return user.getLastName();
  }
  public String getFullName() {
    return getFirstName() + " " + getLastName();
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    user.setPassword(password);
    this.password = password;
  }
  public boolean isUser() {
    return isUser;
  }
  public void setUser(boolean isUser) {
    this.isUser = isUser;
  }
  public boolean isAdmin() {
    return isAdmin;
  }
  public void setAdmin(boolean isAdmin) {
    this.isAdmin = isAdmin;
  }
  public void addSecurityRoleGroup(Group securityRoleGroup) {
    securityRoles.add(securityRoleGroup);
  }
  public List<Group> getSecurityRoles() {
    return securityRoles;
  }
  public void addGroup(Group group) {
    groups.add(group);
  }
  
}
