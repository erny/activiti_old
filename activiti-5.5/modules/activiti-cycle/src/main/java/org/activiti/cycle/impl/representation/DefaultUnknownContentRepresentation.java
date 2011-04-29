package org.activiti.cycle.impl.representation;

import org.activiti.cycle.ContentRepresentation;
import org.activiti.cycle.MimeType;
import org.activiti.cycle.RenderInfo;
import org.activiti.cycle.annotations.CycleComponent;
import org.activiti.cycle.context.CycleContextType;
import org.activiti.cycle.impl.mimetype.UnknownMimeType;

/**
 * Default {@link ContentRepresentation} for {@link UnknownMimeType}
 * 
 * @author daniel.meyer@camunda.com
 */
@CycleComponent(context = CycleContextType.APPLICATION)
public class DefaultUnknownContentRepresentation extends AbstractBasicArtifactTypeContentRepresentation {

  private static final long serialVersionUID = 1L;

  public RenderInfo getRenderInfo() {
    // TODO: render unknown content type as text... ?
    return RenderInfo.TEXT_PLAIN;
  }

  protected Class< ? extends MimeType> getMimeType() {
    return UnknownMimeType.class;
  }

}
