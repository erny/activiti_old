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
package org.activiti.rest.api.cycle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.activiti.cycle.ContentRepresentationDefinition;
import org.activiti.cycle.ContentType;
import org.activiti.cycle.DownloadContentAction;
import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryConnector;
import org.activiti.rest.api.cycle.dto.ContentView;
import org.activiti.rest.api.cycle.dto.DownloadActionView;
import org.activiti.rest.util.ActivitiWebScript;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

/**
 * @author Nils Preusker
 */
public class ArtifactGet extends ActivitiWebScript {

  @Override
  protected void executeWebScript(WebScriptRequest req, Status status, Cache cache, Map<String, Object> model) {
    // Retrieve the artifactId from the request
    String artifactId = getString(req, "artifactId");

    // Retrieve session and repo connector
    String cuid = getCurrentUserId(req);

    HttpSession session = ((WebScriptServletRequest) req).getHttpServletRequest().getSession(true);
    RepositoryConnector conn = SessionUtil.getRepositoryConnector(cuid, session);

    // Retrieve the artifact from the repository
    RepositoryArtifact artifact = conn.getRepositoryArtifact(artifactId);

    List<ContentView> contentViews = new ArrayList<ContentView>();
    for (ContentRepresentationDefinition representation : artifact.getContentRepresentationDefinitions()) {
      try {
        if (representation.getType().equals(ContentType.TEXT) || representation.getType().equals(ContentType.XML) || representation.getType().equals(ContentType.HTML)) {
          String content = conn.getContent(artifactId, representation.getName()).asString();
          contentViews.add(new ContentView(representation.getType(), representation.getName(), content));
        } else if (representation.getType().startsWith("image/")) {
          String url = req.getServerPath() + req.getContextPath() + "/service/content?artifactId=" + URLEncoder.encode(artifactId, "UTF-8") + "&content-type=" + URLEncoder.encode(representation.getType(), "UTF-8");
          contentViews.add(new ContentView(representation.getType(), representation.getName(), url));
        }
      } catch (UnsupportedEncodingException e) {
        // should never be reached as long as we use UTF-8, which is valid in
        // java on all platforms
        throw new RuntimeException(e);
      }
    }

    model.put("actions", artifact.getParametrizedActions()); // label, name

    // Create downloadContentView DTOs
    List<DownloadActionView> downloads = new ArrayList<DownloadActionView>();
    for (DownloadContentAction action : artifact.getDownloadContentActions()) {
      try {
        String url = req.getServerPath() + req.getContextPath() + "/service/content?artifactId=" + URLEncoder.encode(artifactId, "UTF-8") + "&content-type="
                + URLEncoder.encode(action.getDefiniton().getType(), "UTF-8");
        downloads.add(new DownloadActionView(action.getLabel(), url, action.getDefiniton().getType(), action.getDefiniton().getName()));
      } catch (UnsupportedEncodingException e) {
        // should never be reached as long as we use UTF-8, which is valid in
        // java on all platforms
        throw new RuntimeException(e);
      }
    }
    model.put("downloads", downloads);

    model.put("links", artifact.getOpenUrlActions()); // label, name, url

    model.put("artifactId", artifact.getId());
    model.put("contentViews", contentViews);

  }
}
