package org.activiti.rest.api.cycle;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.activiti.cycle.ParameterizedAction;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryConnector;
import org.activiti.rest.util.ActivitiRequest;
import org.activiti.rest.util.ActivitiWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;

public class ArtifactActionFormGet extends ActivitiWebScript {

  /**
   * Returns an action's form.
   * 
   * @param req The webscripts request
   * @param status The webscripts status
   * @param cache The webscript cache
   * @param model The webscripts template model
   */
  @Override
  protected void executeWebScript(ActivitiRequest req, Status status, Cache cache, Map<String, Object> model) {
    // Retrieve the artifactId from the request
    String artifactId = req.getMandatoryString("artifactId");
    String actionId = req.getMandatoryString("actionName");
    
    // Retrieve session and repo connector
    String cuid = req.getCurrentUserId();

    HttpSession session = req.getHttpSession();
    RepositoryConnector conn = SessionUtil.getRepositoryConnector(cuid, session);

    // Retrieve the artifact from the repository
    RepositoryArtifact artifact = conn.getRepositoryArtifact(artifactId);

    if(artifact == null) {
      throw new WebScriptException(Status.STATUS_NOT_FOUND, "There is no artifact with id '" + artifactId + "'.");
    }
    
    // Retrieve the action and its form
    String form = null;
    for (ParameterizedAction action : artifact.getArtifactType().getParameterizedActions()) {
      if (action.getId().equals(actionId)) {
        form = action.getFormAsHtml();
        break;
      }
    }
    
    // Place the form in the response
    if (form != null) {
      model.put("form", form);
    } else {
      throw new WebScriptException(Status.STATUS_NOT_FOUND, "There is no form for action '" + actionId + "'.");
    }
  }
}
