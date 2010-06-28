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
package org.activiti.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.AssertionFailedError;

import org.activiti.impl.time.Clock;
import org.activiti.impl.util.LogUtil;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;


/**
 * @author Tom Baeyens
 * @author Dave Syer
 */
public class LogInitializer implements MethodRule {

  private static final String EMPTY_LINE = "                                                                                           ";

  static {
    LogUtil.readJavaUtilLoggingConfigFromClasspath();
  }
  
  private static Logger log = Logger.getLogger(LogInitializer.class.getName());
  
  public Statement apply(Statement base, FrameworkMethod method, Object target) {
    return new LogUtilStatement(base, method);
  }
 
  private class LogUtilStatement extends Statement {

    private final Statement base;
    private final FrameworkMethod method;

    public LogUtilStatement(Statement base, FrameworkMethod method) {
      this.base= base;
      this.method = method;
    }

    @Override
    public void evaluate() throws Throwable {

      LogUtil.resetThreadIndents();
      log.fine(EMPTY_LINE);
      log.fine("---- START "+method.getMethod().getDeclaringClass().getName()+"."+method.getName()+" ------------------------------------------------------");

      try {
        
        base.evaluate();

      }  catch (AssertionFailedError e) {
        log.severe(EMPTY_LINE);
        log.log(Level.SEVERE, "ASSERTION FAILED: "+e, e);
        throw e;
      } catch (Throwable e) {
        log.severe(EMPTY_LINE);
        log.log(Level.SEVERE, "EXCEPTION: "+e, e);
        throw e;
      } finally {
        Clock.reset();
        log.fine("---- END "+method.getMethod().getDeclaringClass().getName()+"."+method.getName()+" ------------------------------------------------------");
      }
    }
 
  }
  
}
