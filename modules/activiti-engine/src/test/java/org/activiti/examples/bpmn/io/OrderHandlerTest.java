/**
 * 
 */
package org.activiti.examples.bpmn.io;

import java.util.Map;
import java.util.TreeMap;

import junit.framework.Assert;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.impl.test.PluggableActivitiTestCase;
import org.activiti.engine.impl.util.CollectionUtil;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.test.Deployment;

/**
 * @author alumyanski
 */
public class OrderHandlerTest extends PluggableActivitiTestCase implements ExecutionListener {
	private static Map<String, Double> totalVarValues = new TreeMap<String, Double>();
	@Deployment
	public void testDataObjects() {
		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
						"OrderHandler",
						CollectionUtil.singletonMap("order", 340.0));
		Assert.assertEquals(new Double(340.0), totalVarValues.get(pi.getProcessInstanceId()));

		pi = runtimeService.startProcessInstanceByKey(
				"OrderHandler",
				CollectionUtil.singletonMap("order", 1010.0));
		Assert.assertEquals(new Double(909.0), totalVarValues.get(pi.getProcessInstanceId()));

		pi = runtimeService.startProcessInstanceByKey(
				"OrderHandler",
				CollectionUtil.singletonMap("order", 540.0));
		Assert.assertEquals(new Double(513.0), totalVarValues.get(pi.getProcessInstanceId()));

	}

	@Deployment(resources = {
			"org/activiti/examples/bpmn/io/CalculateDiscount.bpmn20.xml",
			"org/activiti/examples/bpmn/io/OrderHandler2.bpmn20.xml" })
	public void testDataObjectsWithCallActivity() {
		ProcessInstance pi = runtimeService.startProcessInstanceByKey(
						"OrderHandler2",
						CollectionUtil.singletonMap("order", 340.0));
		Assert.assertEquals(new Double(340.0), totalVarValues.get(pi.getProcessInstanceId()));

		pi = runtimeService.startProcessInstanceByKey(
				"OrderHandler2",
				CollectionUtil.singletonMap("order", 1010.0));
		Assert.assertEquals(new Double(909.0), totalVarValues.get(pi.getProcessInstanceId()));

		pi = runtimeService.startProcessInstanceByKey(
				"OrderHandler2",
				CollectionUtil.singletonMap("order", 540.0));
		Assert.assertEquals(new Double(513.0), totalVarValues.get(pi.getProcessInstanceId()));

	}

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		totalVarValues.put(execution.getProcessInstanceId(), (Double) execution.getVariable("total")); 
	}
}
