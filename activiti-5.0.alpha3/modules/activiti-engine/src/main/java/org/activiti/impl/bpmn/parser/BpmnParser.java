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
package org.activiti.impl.bpmn.parser;

import org.activiti.impl.xml.Parser;


/**
 * Parser for BPMN 2.0 process models.
 * 
 * There is only one instance of this parser in the process engine.
 * This {@link Parser} creates {@link BpmnParse} instances that 
 * can be used to actually parse the BPMN 2.0 XML process definitions.
 * 
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class BpmnParser extends Parser {

  /**
   * The singleton instance of the BPMN 2.0 parser.
   */
  public static final BpmnParser INSTANCE = new BpmnParser();
  
  /**
   * The location of the BPMN 2.0 XML schema
   */
  public static final String SCHEMA_RESOURCE = "org/activiti/impl/bpmn/parser/BPMN20.xsd";

  /**
   * The namespace of the Activiti custom BPMN extensions
   */
  public static final String BPMN_EXTENSIONS_NS = "http://activiti.org/bpmn-extensions";

  /**
   * The Schema-Instance namespace.
   */
  public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";
  
  /**
   * Creates a new {@link BpmnParse} instance that can be used
   * to parse only one BPMN 2.0 process definition.
   */
  public BpmnParse createParse() {
    return new BpmnParse(this);
  }
  
}
