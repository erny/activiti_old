/**
 * 
 */
package org.activiti.engine.impl.bpmn.data;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;

/**
 *  A simple data association between a source and a target with assignments.
 *  
 *  @author Esteban Robles Luna
 *  @author Andrey Lumyanski
 */
public class SimpleDataAssociation extends AbstractDataAssociation {
	protected List<Assignment> assignments = new ArrayList<Assignment>();

	/**
	 * Creates {@code SimpleDataAssociation}
	 * @param sourceExpression source expression
	 * @param target target name
	 */
	public SimpleDataAssociation(Expression sourceExpression, String target) {
	  super(sourceExpression, target);
  }

	/**
	 * Creates {@code SimpleDataAssociation}
	 * @param source source name
	 * @param target target name
	 */
	public SimpleDataAssociation(String source, String target) {
	  super(source, target);
  }

	/**
	 * Adds assignments.
	 * @param assignment {@link Assignment} object
	 */
  public void addAssignment(Assignment assignment) {
    this.assignments.add(assignment);
  }

  /* (non-Javadoc)
	 * @see org.activiti.engine.impl.bpmn.data.AbstractDataAssociation#evaluate(org.activiti.engine.impl.pvm.delegate.ActivityExecution)
	 */
	@Override
	public void evaluate(ActivityExecution execution) {
		if (source != null && target != null) {
			execution.setVariable(target, execution.getVariable(source));
		}
    for (Assignment assignment : this.assignments) {
      assignment.evaluate(execution);
    }
	}

}
