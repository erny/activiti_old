package org.activiti.cycle.service;

import java.util.List;
import java.util.Set;

import org.activiti.cycle.RepositoryArtifact;
import org.activiti.cycle.RepositoryArtifactType;
import org.activiti.cycle.action.Action;
import org.activiti.cycle.action.CreateUrlAction;
import org.activiti.cycle.action.DownloadContentAction;
import org.activiti.cycle.action.ParameterizedAction;
import org.activiti.cycle.action.RepositoryArtifactOpenLinkAction;

/**
 * Activiti Cycle Service providing access to plugin-components like
 * {@link Action}s or {@link ContentProvider}s.
 * 
 * @author daniel.meyer@camunda.com
 */
public interface CyclePluginService {

  public Set<ParameterizedAction> getParameterizedActions(RepositoryArtifactType type);

  public Set<CreateUrlAction> getCreateUrlActions(RepositoryArtifactType type);

  public List<DownloadContentAction> getDownloadContentActions(RepositoryArtifactType type);

  public List<ParameterizedAction> getParameterizedActions(RepositoryArtifact artifact);

  public List<CreateUrlAction> getCreateUrlActions(RepositoryArtifact artifact);

  public List<DownloadContentAction> getDownloadContentActions(RepositoryArtifact artifact);

  public Set<RepositoryArtifactOpenLinkAction> getArtifactOpenLinkActions(RepositoryArtifact artifact);

  public ParameterizedAction getParameterizedActionById(String actionId);

}
