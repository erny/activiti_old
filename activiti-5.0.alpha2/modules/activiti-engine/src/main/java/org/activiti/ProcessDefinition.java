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
package org.activiti;

/** is an object structure representing an executable process composed of 
 * activities and transitions.
 * 
 * Business processes are often created with graphical editors that store the
 * process definition in certain file format. These files can be added to a
 * {@link Deployment} artifact, such as for example a Business Archive (.bar)
 * file.
 * 
 * At deploy time, the engine will then parse the process definition files to an
 * executable instance of this class, that can be used to start a {@link ProcessInstance}.
 * 
 * @author Tom Baeyens
 * @author Joram Barez
 */
public interface ProcessDefinition {

  /** unique identifier */
  String getId();

  /** unique name for all versions this process definitions */
  String getKey();
  
  /** version of this process definition */
  int getVersion();

  /** label used for display purposes */
  String getName();
}
