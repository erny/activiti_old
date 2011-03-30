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
package org.activiti.cycle;

/**
 * Provides a simple interface to retrieve the content-type of artifacts and
 * their content representations.
 * 
 * 
 * @author nils.preusker@camunda.com
 */
public interface MimeType {

  public String getName();
  
  public String getContentType();

  public String[] getCommonFileExtensions();

}
