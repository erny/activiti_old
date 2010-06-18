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
package org.activiti;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.activiti.impl.ProcessEngineImpl;
import org.activiti.impl.cfg.IbatisPersistenceSessionFactory;
import org.activiti.impl.cfg.PersistenceSessionFactory;
import org.activiti.impl.cfg.ProcessEngineConfiguration;


/**
 * TODO
 * 
 * Typical usage
 * <pre>TODO</pre>
 * 
 * 
 * @see ProcessEngines
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class DbProcessEngineBuilder {
  
  private static final String DATABASE_NAME_H2 = "h2";
  private static final String DATABASE_NAME_H2_IN_MEMORY = "h2-in-memory";
  
  String processEngineName = ProcessEngines.NAME_DEFAULT;
  String databaseName;
  String jdbcDriver;
  String jdbcUrl;
  String jdbcUsername;
  String jdbcPassword;
  DbSchemaStrategy dbSchemaStrategy;
  boolean jobExecutorAutoActivate = true;
  
  public String getProcessEngineName() {
    return processEngineName;
  }

  
  public DbProcessEngineBuilder setProcessEngineName(String processEngineName) {
    this.processEngineName = processEngineName;
    return this;
  }

  
  public String getDatabaseName() {
    return databaseName;
  }

  
  public DbProcessEngineBuilder setDatabaseName(String databaseName) {
    this.databaseName = databaseName;
    return this;
  }

  
  public String getJdbcDriver() {
    return jdbcDriver;
  }

  
  public DbProcessEngineBuilder setJdbcDriver(String jdbcDriver) {
    this.jdbcDriver = jdbcDriver;
    return this;
  }

  
  public String getJdbcUrl() {
    return jdbcUrl;
  }

  
  public DbProcessEngineBuilder setJdbcUrl(String jdbcUrl) {
    this.jdbcUrl = jdbcUrl;
    return this;
  }

  
  public String getJdbcUsername() {
    return jdbcUsername;
  }

  
  public DbProcessEngineBuilder setJdbcUsername(String jdbcUsername) {
    this.jdbcUsername = jdbcUsername;
    return this;
  }

  
  public String getJdbcPassword() {
    return jdbcPassword;
  }

  
  public DbProcessEngineBuilder setJdbcPassword(String jdbcPassword) {
    this.jdbcPassword = jdbcPassword;
    return this;
  }

  
  public DbSchemaStrategy getDbSchemaStrategy() {
    return dbSchemaStrategy;
  }

  
  public DbProcessEngineBuilder setDbSchemaStrategy(DbSchemaStrategy dbSchemaStrategy) {
    this.dbSchemaStrategy = dbSchemaStrategy;
    return this;
  }

  public DbProcessEngineBuilder setDatabaseH2() {
    if (databaseName==null) databaseName = DATABASE_NAME_H2; 
    if (jdbcDriver==null) jdbcDriver = "org.h2.Driver";
    if (jdbcUrl==null) jdbcUrl = "jdbc:h2:tcp://localhost/activiti"; 
    if (jdbcUsername==null) jdbcUsername = "sa"; 
    if (jdbcPassword==null) jdbcPassword = "";
    if (dbSchemaStrategy==null) dbSchemaStrategy = DbSchemaStrategy.CHECK_VERSION;
    return this;
  }

  public DbProcessEngineBuilder setDatabaseH2InMemory() {
    if (databaseName==null) databaseName = DATABASE_NAME_H2_IN_MEMORY; 
    if (jdbcDriver==null) jdbcDriver = "org.h2.Driver";
    if (jdbcUrl==null) jdbcUrl = "jdbc:h2:mem:activiti"; 
    if (jdbcUsername==null) jdbcUsername = "sa"; 
    if (jdbcPassword==null) jdbcPassword = "";
    if (dbSchemaStrategy==null) dbSchemaStrategy = DbSchemaStrategy.CHECK_VERSION;
    return this;
  }

  public DbProcessEngineBuilder configureFromProperties(Properties configurationProperties) {
    if (configurationProperties==null) {
      throw new ActivitiException("configurationProperties is null");
    }
    
    String processEngineName = configurationProperties.getProperty("process.engine.name");
    if (processEngineName!=null) {
      this.processEngineName = processEngineName;
    }
    
    String databaseName = configurationProperties.getProperty("database");
    if (DATABASE_NAME_H2.equals(databaseName)) {
      setDatabaseH2();
    } else if (DATABASE_NAME_H2_IN_MEMORY.equals(databaseName)) {
      setDatabaseH2InMemory();
    } else {
      throw new ActivitiException("unknown database: '"+databaseName+"'. available options: "+Arrays.asList(new String[]{
        DATABASE_NAME_H2,
        DATABASE_NAME_H2_IN_MEMORY
      }));
    }
    
    String jdbcDriver = configurationProperties.getProperty("jdbc.driver");
    if (jdbcDriver!=null) {
      this.jdbcDriver = jdbcDriver;
    }
    
    String jdbcUrl = configurationProperties.getProperty("jdbc.url");
    if (jdbcUrl!=null) {
      this.jdbcUrl = jdbcUrl;
    }
    
    String jdbcUsername = configurationProperties.getProperty("jdbc.username");
    if (jdbcUsername!=null) {
      this.jdbcUsername = jdbcUsername;
    }
    
    String jdbcPassword = configurationProperties.getProperty("jdbc.password");
    if (jdbcPassword!=null) {
      this.jdbcPassword = jdbcPassword;
    }
    
    String dbSchemaStrategy = configurationProperties.getProperty("db.schema.strategy");
    if (dbSchemaStrategy!=null) {
      if ("create-drop".equals(dbSchemaStrategy)) {
        this.dbSchemaStrategy = DbSchemaStrategy.CREATE_DROP;
      } else if ("check-version".equals(dbSchemaStrategy)) {
        this.dbSchemaStrategy = DbSchemaStrategy.CHECK_VERSION;
      } else {
        throw new ActivitiException("unknown db.schema.strategy: '"+dbSchemaStrategy+"': should be 'create-drop' or 'check-version'");
      }
    }

    String jobExecutorAutoActivate = configurationProperties.getProperty("job.executor.auto.activate");
    if ( (jobExecutorAutoActivate!=null)
         && ( ("false".equals(jobExecutorAutoActivate))
              || ("disabled".equals(jobExecutorAutoActivate))
              || ("off".equals(jobExecutorAutoActivate))
            )
       ) {
      this.jobExecutorAutoActivate = false;
    }

    return this;
  }
  
  public DbProcessEngineBuilder configureFromPropertiesInputStream(InputStream inputStream) {
    if (inputStream==null) {
      throw new ActivitiException("inputStream is null");
    }
    Properties properties = new Properties();
    try {
      properties.load(inputStream);
    } catch (IOException e) {
      throw new ActivitiException("problem while reading activiti configuration properties "+e.getMessage(), e);
    }
    configureFromProperties(properties);
    return this;
  }
  
  public DbProcessEngineBuilder configureFromPropertiesResource(String propertiesResource) {
    InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesResource);
    if (inputStream==null) {
      throw new ActivitiException("configuration properties resource '"+propertiesResource+"' is unavailable on classpath "+System.getProperty("java.class.path"));
    }
    configureFromPropertiesInputStream(inputStream);
    return this;
  }
  
  public DbProcessEngineBuilder setJobExecutorAutoActivation(boolean jobExecutorAutoActivate) {
    this.jobExecutorAutoActivate = jobExecutorAutoActivate;
    return this;
  }
  
  public ProcessEngine buildProcessEngine() {
    PersistenceSessionFactory persistenceSessionFactory = new IbatisPersistenceSessionFactory(
            databaseName,
            jdbcDriver,
            jdbcUrl,
            jdbcUsername,
            jdbcPassword);
    
    ProcessEngineConfiguration processEngineConfiguration = new ProcessEngineConfiguration();
    processEngineConfiguration.setProcessEngineName(processEngineName);
    processEngineConfiguration.setPersistenceSessionFactory(persistenceSessionFactory);
    processEngineConfiguration.setDbSchemaStrategy(dbSchemaStrategy);
    processEngineConfiguration.setJobExecutorAutoActivate(jobExecutorAutoActivate);
    ProcessEngineImpl processEngine = processEngineConfiguration.buildProcessEngine();
    
    return processEngine;
  }
}
