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
package org.activiti.impl.cmd;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.Deployment;
import org.activiti.impl.bytes.ByteArrayImpl;
import org.activiti.impl.interceptor.Command;
import org.activiti.impl.interceptor.CommandContext;
import org.activiti.impl.persistence.PersistenceSession;
import org.activiti.impl.repository.DeployerManager;
import org.activiti.impl.repository.DeploymentImpl;
import org.activiti.impl.time.Clock;

/**
 * @author Tom Baeyens
 */
public class DeployCmd<T> implements Command<Deployment> {

  private final DeploymentImpl deployment;
  private final DeployerManager deployerManager;

  public DeployCmd(DeployerManager deployerManager, DeploymentImpl deployment) {
    this.deployerManager = deployerManager;
    this.deployment = deployment;
  }

  public Deployment execute(CommandContext commandContext) {
    DeploymentImpl deployment = this.deployment;
    PersistenceSession persistenceSession = commandContext.getPersistenceSession();
    List<DeploymentImpl> deployments = persistenceSession.findDeploymentsByName(deployment.getName());
    if (deployments.isEmpty() || deploymentsDiffer(deployment, deployments.get(0))) {
      insertDeployment(persistenceSession);
    } else {
      deployment = deployments.get(0);
    }
    
    // Try to deploy the process
    // If something goes wrong during parsing, the deployment must be deleted from the databse
    try {
      deployerManager.deploy(deployment, persistenceSession);
    } catch (RuntimeException e) {
      persistenceSession.deleteDeployment(deployment.getId());
      throw e;
    }
    
    return deployment;
  }

  private boolean deploymentsDiffer(DeploymentImpl deployment, DeploymentImpl saved) {
    Map<String, ByteArrayImpl> resources = deployment.getResources();
    Map<String, ByteArrayImpl> savedResources = saved.getResources();
    for (Entry<String, ByteArrayImpl> entry : resources.entrySet()) {
      if (resourcesDiffer(entry.getValue(), savedResources.get(entry.getKey()))) {
        return true;
      }
    }
    return false;
  }

  private boolean resourcesDiffer(ByteArrayImpl value, ByteArrayImpl other) {
    if (value == null && other == null) {
      return false;
    }
    String bytes = createKey(value.getBytes());
    String savedBytes = other == null ? null : createKey(other.getBytes());
    return !bytes.equals(savedBytes);
  }

  private String createKey(byte[] bytes) {
    if (bytes == null) {
      return "";
    }
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
    }
    bytes = digest.digest(bytes);
    return String.format("%032x", new BigInteger(1, bytes));
  }

  private void insertDeployment(PersistenceSession persistenceSession) {
    deployment.setDeploymentTime(Clock.getCurrentTime());
    persistenceSession.insertDeployment(deployment);
  }
}
