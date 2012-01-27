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
package org.activiti.engine.impl.bpmn.behavior;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.impl.bpmn.data.AbstractDataAssociation;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

/**
 * implementation of the 'none start event': a start event that has no specific
 * trigger but the programmatic one (processService.startProcessInstanceXXX()).
 * 
 * 
 * @author Joram Barrez
 * @author Andrey Lumyanski
 */
public class NoneStartEventActivityBehavior extends FlowNodeActivityBehavior {

	protected final List<AbstractDataAssociation> dataOutputAssociations = new ArrayList<AbstractDataAssociation>();

  public void addDataOutputAssociation(AbstractDataAssociation dataAssociation) {
    this.dataOutputAssociations.add(dataAssociation);
  }

  @Override
  public void execute(ActivityExecution execution) throws Exception {
    // evaluate data output associations:
  	for (AbstractDataAssociation dataAssociation : this.dataOutputAssociations) {
      dataAssociation.evaluate(execution);
    }

    super.execute(execution);
  }
}
