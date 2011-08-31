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
package org.activiti.engine.impl.pvm.runtime;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @author Daniel Meyer
 */
public class AtomicOperationCodes {

  private final static Map<String, AtomicOperation> opsForCodes = new HashMap<String, AtomicOperation>();
  private final static Map<AtomicOperation, String> codesForOps = new HashMap<AtomicOperation, String>();
  
  static {
    opsForCodes.put("TRANSITION_CREATE_SCOPE", AtomicOperation.TRANSITION_CREATE_SCOPE);
    codesForOps.put(AtomicOperation.TRANSITION_CREATE_SCOPE, "TRANSITION_CREATE_SCOPE");
  }
  
  public static AtomicOperation getAtomicOperation(String code) {
    return opsForCodes.get(code);
  }
  
  public static String getCode(AtomicOperation atomicOperation) {
    return codesForOps.get(atomicOperation);
  }
  

}
