package org.activiti.cycle.impl.connector.signavio.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.cycle.Content;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryException;
import org.activiti.cycle.impl.connector.signavio.SignavioConnector;
import org.activiti.cycle.impl.connector.signavio.SignavioPluginDefinition;
import org.activiti.cycle.impl.connector.signavio.util.SignavioSvgApiBuilder;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;
import org.oryxeditor.server.diagram.Shape;

/**
 * <b>EXPERIMENTAL</b> proof of concept of an easy DIFF functionality with
 * Signavio within Cycle. Pretty hacky, so don't use this in real life
 * 
 * @author ruecker
 */
public class SignavioDiffProvider extends SignavioContentRepresentationProvider {

  // private static final String ERROR_COLOR = "red";
  // private static final String WARN_COLOR = "#FFE736";
  private static final String INFO_COLOR = "#8CC0ED";

  public static RepositoryArtifact targetArtifact;

  public void addValueToContent(Content content, SignavioConnector connector, RepositoryArtifact artifact) {
    RepositoryArtifact diffTarget = targetArtifact;
    if (targetArtifact == null) {
      // if nothing is selected we diff against ourself (boooring ;-))
      diffTarget = artifact;
    }

    Map<String, List<String>> missingSourceElements = new HashMap<String, List<String>>();
    Map<String, List<String>> missingTargetElements = new HashMap<String, List<String>>();
    
    // create DIFF
    String sourceJson = connector.getContent(artifact.getNodeId(), SignavioPluginDefinition.CONTENT_REPRESENTATION_ID_JSON).asString();
    String targetJson = connector.getContent(diffTarget.getNodeId(), SignavioPluginDefinition.CONTENT_REPRESENTATION_ID_JSON).asString();
    
    try {
      Diagram sourceDiagram = DiagramBuilder.parseJson(sourceJson);
      Diagram targetDiagram = DiagramBuilder.parseJson(targetJson);
    
      // First quick HACKY way to find missing elements on the top level
      for (Shape sourceShape : sourceDiagram.getShapes()) {
        String sourceId = sourceShape.getResourceId();
        // check if existant in target
        boolean existant = false;
        for (Shape targetShape : targetDiagram.getShapes()) {
          if (targetShape.getResourceId() != null && targetShape.getResourceId().equals(sourceId)) {
            existant = true;
          }
        }
        if (!existant) {
          // add to missing nodes in target artifact
          ArrayList<String> messages = new ArrayList<String>();
          messages.add("MISSING");
          missingSourceElements.put(sourceId, messages);
        }
      }
      for (Shape targetShape : targetDiagram.getShapes()) {
        String targetId = targetShape.getResourceId();
        // check if existant in target
        boolean existant = false;
        for (Shape sourceShape : sourceDiagram.getShapes()) {
          if (sourceShape.getResourceId() != null && sourceShape.getResourceId().equals(targetId)) {
            existant = true;
          }
        }
        if (!existant) {
          // add to missing nodes in target artifact
          ArrayList<String> messages = new ArrayList<String>();
          messages.add("MISSING");
          missingTargetElements.put(targetId, messages);
        }
      }
    } catch (Exception e) {
      throw new RepositoryException("Could not create DIFF due to exception", e);
    }

    // and create resulting HTML
    String script1 = new SignavioSvgApiBuilder(connector, artifact).highlightNodes(missingSourceElements, INFO_COLOR).buildScript(75);
    String script2 = new SignavioSvgApiBuilder(connector, diffTarget).highlightNodes(missingTargetElements, INFO_COLOR).buildScript(75);

    Integer height = 200;
    String htmlContent = "<p><b>Expertimental</b> feature to play around with Signavio diffing.<br/>Currently, showing diff against artifact "
            + diffTarget.getGlobalUniqueId() + ".<br/>Use the Actions menu to select another diff target.</p>";
    
    String additionalContent = "<p>Changes from " + diffTarget.getMetadata().getName() + " in " + artifact.getMetadata().getName();
    additionalContent += "<div id=\"model1\" style=\"height: " + height + "px;\">" + script1 + "</div>";
    additionalContent += "Changes from " + artifact.getMetadata().getName() + " in " + diffTarget.getMetadata().getName();
    additionalContent += "<div id=\"model2\" style=\"height: " + height + "px;\">" + script2 + "</div>";

    String html = SignavioSvgApiBuilder.buildHtml(htmlContent, additionalContent);
    content.setValue(html);
    
  }
}
