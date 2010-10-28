package org.activiti.cycle.impl.db;

import java.util.List;

import org.activiti.cycle.impl.CycleTagContentImpl;
import org.activiti.cycle.impl.db.entity.CycleArtifactTagEntity;
import org.activiti.cycle.impl.db.entity.RepositoryArtifactLinkImpl;

public interface CycleDAO {

  public List<RepositoryArtifactLinkImpl> getOutgoingCycleLinks(String sourceConnectorId, String sourceArtifactId);
  public List<RepositoryArtifactLinkImpl> getIncomingCycleLinks(String targetConnectorId, String targetArtifactId);

  // public CycleLink findCycleLinkById(String id);

  // public void updateCycleLink(CycleLink cycleLink);

  public void insertCycleLink(RepositoryArtifactLinkImpl cycleLink);
  public void deleteCycleLink(String id);
  
  public List<CycleTagContentImpl> getTagsGroupedByName();
  public List<CycleArtifactTagEntity> getTagsForNode(String connectorId, String artifactId);

  public CycleTagContentImpl getTagContent(String alias);
  
  public void insertTag(CycleArtifactTagEntity tag);
  public void deleteTag(String connectorId, String artifactId, String tagName);

  // public void deleteTag(String id);

}
