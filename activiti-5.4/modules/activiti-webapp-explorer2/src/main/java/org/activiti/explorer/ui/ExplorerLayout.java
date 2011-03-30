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

package org.activiti.explorer.ui;



/**
 * @author Joram Barrez
 */
public interface ExplorerLayout {
  
  // Application theme
  static final String THEME = "activiti";
  
  // Custom layouts (found in /VAADIN/themes/${THEME}/layouyts
  static final String CUSTOM_LAYOUT_DEFAULT = "activiti";
  static final String CUSTOM_LAYOUT_LOGIN = "login";
  
  // Locations defined in the layout .html files
  static final String LOCATION_LOGIN = "login-content";
  static final String LOCATION_CONTENT = "content";
  static final String LOCATION_SEARCH = "search";
  static final String LOCATION_LOGOUT = "logout";
  static final String LOCATION_MAIN_MENU = "main-menu";
  static final String LOCATION_HIDDEN = "hidden";

  // ---------- 
  // Css styles
  // ----------
  
  // General
  static final String STYLE_SMALL_TEXTFIELD = "small";
  static final String STYLE_SEARCHBOX = "searchBox";
  static final String STYLE_LOGOUT_BUTTON = "logout";
  static final String STYLE_USER_PROFILE = "user";
  static final String STYLE_LABEL_BOLD = "bold";
  
  //Forms
  static final String STYLE_FORMPROPERTY_READONLY = "formprop-readonly";
  static final String STYLE_FORMPROPERTY_LABEL = "formprop-label";
  
  // Login page
  static final String STYLE_LOGIN_PAGE = "login-general";
  
  // Menu bar
  static final String STYLE_MENUBAR = "menubar";
  static final String STYLE_MENUBAR_BUTTON = "menu-button";
  
  // Action Bar
  static final String STYLE_ACTION_BAR = "action-bar";
  
  // Profile page
  static final String STYLE_PROFILE_LAYOUT = "profile-layout";
  static final String STYLE_PROFILE_HEADER = "profile-header";
  static final String STYLE_PROFILE_FIELD = "profile-field";
  static final String STYLE_PROFILE_PICTURE = "profile-picture";
  
  // Task pages
  static final String STYLE_TASK_LIST = "task-list";
  static final String STYLE_TASK_DETAILS = "task-details";
  static final String STYLE_TASK_DETAILS_HEADER = "task-details-header";
  static final String STYLE_TASK_COMMENT = "task-comment";
  static final String STYLE_TASK_COMMENT_AUTHOR = "task-comment-author";
  static final String STYLE_TASK_COMMENT_TIME = "task-comment-time";
  static final String STYLE_TASK_COMMENT_PICTURE = "task-comment-picture";

  // Flow pages
  static final String STYLE_PROCESS_DEFINITION_LIST = "proc-def-list";
  static final String STYLE_PROCESS_DEFINITION_DETAILS_HEADER = "proc-def-details-header";
  
  // Database page
  static final String STYLE_DATABASE_DETAILS = "database-details";
  static final String STYLE_DATABASE_TABLE_ROW = "database-table-row";
  
  // Deployment page
  static final String STYLE_DEPLOYMENT_DETAILS_HEADER = "deployment-details-header";
  static final String STYLE_DEPLOYMENT_UPLOAD_DESCRIPTION = "upload-description";
  static final String STYLE_DEPLOYMENT_UPLOAD_BUTTON = "upload-button";
  
  // Jobs page
  static final String STYLE_JOB_DETAILS_HEADER = "job-details-header";
  static final String STYLE_JOB_EXCEPTION_MESSAGE = "job-exception-message";
  static final String STYLE_JOB_EXCEPTION_TRACE = "job-exception-trace";
  
}
