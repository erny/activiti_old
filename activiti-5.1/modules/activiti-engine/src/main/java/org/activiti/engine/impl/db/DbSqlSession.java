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

package org.activiti.engine.impl.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiOptimisticLockingException;
import org.activiti.engine.ActivitiWrongDbException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.db.upgrade.DbUpgradeStep;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.repository.PropertyEntity;
import org.activiti.engine.impl.runtime.VariableInstanceEntity;
import org.activiti.engine.impl.util.ClassNameUtil;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.impl.util.ReflectUtil;
import org.activiti.engine.impl.variable.DeserializedObject;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;


/** responsibilities:
 *   - delayed flushing of inserts updates and deletes
 *   - optional dirty checking
 *   - db specific statement name mapping
 *   
 * @author Tom Baeyens
 */
public class DbSqlSession implements Session {
  
  private static Logger log = Logger.getLogger(DbSqlSession.class.getName());

  protected SqlSession sqlSession;
  protected DbSqlSessionFactory dbSqlSessionFactory;
  protected List<PersistentObject> insertedObjects = new ArrayList<PersistentObject>();
  protected Map<Class<?>, Map<String, CachedObject>> cachedObjects = new HashMap<Class<?>, Map<String,CachedObject>>();
  protected List<DeleteOperation> deletedObjects = new ArrayList<DeleteOperation>();
  protected List<DeserializedObject> deserializedObjects = new ArrayList<DeserializedObject>();

  public DbSqlSession(DbSqlSessionFactory dbSqlSessionFactory) {
    this.dbSqlSessionFactory = dbSqlSessionFactory;
    this.sqlSession = dbSqlSessionFactory
      .getSqlSessionFactory()
      .openSession();
  }

  // insert ///////////////////////////////////////////////////////////////////
  
  public void insert(PersistentObject persistentObject) {
    if (persistentObject.getId()==null) {
      long nextId = dbSqlSessionFactory.getIdGenerator().getNextId();
      String id = Long.toString(nextId);
      persistentObject.setId(id);
    }
    insertedObjects.add(persistentObject);
    cachePut(persistentObject, false);
  }
  
  // delete ///////////////////////////////////////////////////////////////////
  
  public void delete(Class<?> persistentObjectClass, String persistentObjectId) {
    for (DeleteOperation deleteOperation: deletedObjects) {
      if (deleteOperation instanceof DeleteById) {
        DeleteById deleteById = (DeleteById) deleteOperation;
        if ( persistentObjectClass.equals(deleteById.persistenceObjectClass)
             && persistentObjectId.equals(deleteById.persistentObjectId)
           ) {
          // skip this delete
          return;
        }
      }
    }
    deletedObjects.add(new DeleteById(persistentObjectClass, persistentObjectId));
  }
  
  public interface DeleteOperation {
    void execute();
  }

  public class DeleteById implements DeleteOperation {
    Class<?> persistenceObjectClass;
    String persistentObjectId;
    public DeleteById(Class< ? > clazz, String id) {
      this.persistenceObjectClass = clazz;
      this.persistentObjectId = id;
    }
    public void execute() {
      String deleteStatement = dbSqlSessionFactory.getDeleteStatement(persistenceObjectClass);
      deleteStatement = dbSqlSessionFactory.mapStatement(deleteStatement);
      if (deleteStatement==null) {
        throw new ActivitiException("no delete statement for "+persistenceObjectClass+" in the ibatis mapping files");
      }
      log.fine("deleting: "+ClassNameUtil.getClassNameWithoutPackage(persistenceObjectClass)+"["+persistentObjectId+"]");
      sqlSession.delete(deleteStatement, persistentObjectId);
    }
    public String toString() {
      return "delete "+ClassNameUtil.getClassNameWithoutPackage(persistenceObjectClass)+"["+persistentObjectId+"]";
    }
  }
  
  public void delete(String statement, Object parameter) {
    deletedObjects.add(new DeleteBulk(statement, parameter));
  }
  
  public class DeleteBulk implements DeleteOperation {
    String statement;
    Object parameter;
    public DeleteBulk(String statement, Object parameter) {
      this.statement = dbSqlSessionFactory.mapStatement(statement);
      this.parameter = parameter;
    }
    public void execute() {
      sqlSession.delete(statement, parameter);
    }
    public String toString() {
      return "bulk delete: "+statement;
    }
  }
  
  // select ///////////////////////////////////////////////////////////////////

  @SuppressWarnings("unchecked")
  public List selectList(String statement) {
    return selectList(statement, null);
  }

  @SuppressWarnings("unchecked")
  public List selectList(String statement, Object parameter) {
    statement = dbSqlSessionFactory.mapStatement(statement);
    List loadedObjects = sqlSession.selectList(statement, parameter);
    return filterLoadedObjects(loadedObjects);
  }
  
  @SuppressWarnings("unchecked")
  public List selectList(String statement, Object parameter, Page page) {
    statement = dbSqlSessionFactory.mapStatement(statement);
    List loadedObjects;
    if (page!=null) {
      loadedObjects = sqlSession.selectList(statement, parameter, new RowBounds(page.getFirstResult(), page.getMaxResults()));
    } else {
      loadedObjects = sqlSession.selectList(statement, parameter);
    }
    return filterLoadedObjects(loadedObjects);
  }


  public Object selectOne(String statement, Object parameter) {
    statement = dbSqlSessionFactory.mapStatement(statement);
    Object result = sqlSession.selectOne(statement, parameter);
    if (result instanceof PersistentObject) {
      PersistentObject loadedObject = (PersistentObject) result;
      result = cacheFilter(loadedObject);
    }
    return result;
  }
  
  @SuppressWarnings("unchecked")
  public <T extends PersistentObject> T selectById(Class<T> entityClass, String id) {
    T persistentObject = cacheGet(entityClass, id);
    if (persistentObject!=null) {
      return persistentObject;
    }
    String selectStatement = dbSqlSessionFactory.getSelectStatement(entityClass);
    selectStatement = dbSqlSessionFactory.mapStatement(selectStatement);
    persistentObject = (T) sqlSession.selectOne(selectStatement, id);
    if (persistentObject==null) {
      return null;
    }
    cachePut(persistentObject, true);
    return persistentObject;
  }

  // internal session cache ///////////////////////////////////////////////////
  
  @SuppressWarnings("unchecked")
  protected List filterLoadedObjects(List<PersistentObject> loadedObjects) {
    List<PersistentObject> filteredObjects = new ArrayList<PersistentObject>(loadedObjects.size());
    for (PersistentObject loadedObject: loadedObjects) {
      PersistentObject cachedPersistentObject = cacheFilter(loadedObject);
      filteredObjects.add(cachedPersistentObject);
    }
    return filteredObjects;
  }

  protected CachedObject cachePut(PersistentObject persistentObject, boolean storeState) {
    Map<String, CachedObject> classCache = cachedObjects.get(persistentObject.getClass());
    if (classCache==null) {
      classCache = new HashMap<String, CachedObject>();
      cachedObjects.put(persistentObject.getClass(), classCache);
    }
    CachedObject cachedObject = new CachedObject(persistentObject, storeState);
    classCache.put(persistentObject.getId(), cachedObject);
    return cachedObject;
  }
  
  /** returns the object in the cache.  if this object was loaded before, 
   * then the original object is returned.  if this is the first time 
   * this object is loaded, then the loadedObject is added to the cache. */
  protected PersistentObject cacheFilter(PersistentObject persistentObject) {
    PersistentObject cachedPersistentObject = cacheGet(persistentObject.getClass(), persistentObject.getId());
    if (cachedPersistentObject!=null) {
      return cachedPersistentObject;
    }
    cachePut(persistentObject, true);
    return persistentObject;
  }

  @SuppressWarnings("unchecked")
  protected <T> T cacheGet(Class<T> entityClass, String id) {
    CachedObject cachedObject = null;
    Map<String, CachedObject> classCache = cachedObjects.get(entityClass);
    if (classCache!=null) {
      cachedObject = classCache.get(id);
    }
    if (cachedObject!=null) {
      return (T) cachedObject.getPersistentObject();
    }
    return null;
  }
  
  protected void cacheRemove(Class<?> persistentObjectClass, String persistentObjectId) {
    Map<String, CachedObject> classCache = cachedObjects.get(persistentObjectClass);
    if (classCache==null) {
      return;
    }
    classCache.remove(persistentObjectId);
  }
  
  @SuppressWarnings("unchecked")
  public <T> List<T> findInCache(Class<T> entityClass) {
    Map<String, CachedObject> classCache = cachedObjects.get(entityClass);
    if (classCache!=null) {
      ArrayList<T> entities = new ArrayList<T>(classCache.size());
      for (CachedObject cachedObject: classCache.values()) {
        entities.add((T) cachedObject.getPersistentObject());
      }
      return entities;
    }
    return Collections.emptyList();
  }

  public static class CachedObject {
    protected PersistentObject persistentObject;
    protected Object persistentObjectState;
    
    public CachedObject(PersistentObject persistentObject, boolean storeState) {
      this.persistentObject = persistentObject;
      if (storeState) {
        this.persistentObjectState = persistentObject.getPersistentState();
      }
    }

    public PersistentObject getPersistentObject() {
      return persistentObject;
    }

    public Object getPersistentObjectState() {
      return persistentObjectState;
    }
  }

  // deserialized objects /////////////////////////////////////////////////////
  
  public void addDeserializedObject(Object deserializedObject, byte[] serializedBytes, VariableInstanceEntity variableInstanceEntity) {
    deserializedObjects.add(new DeserializedObject(deserializedObject, serializedBytes, variableInstanceEntity));
  }

  // flush ////////////////////////////////////////////////////////////////////

  public void flush() {
    removeUnnecessaryOperations();
    flushDeserializedObjects();
    List<PersistentObject> updatedObjects = getUpdatedObjects();
    
    if (log.isLoggable(Level.FINE)) {
      log.fine("flush summary:");
      for (PersistentObject insertedObject: insertedObjects) {
        log.fine("  insert "+toString(insertedObject));
      }
      for (PersistentObject updatedObject: updatedObjects) {
        log.fine("  update "+toString(updatedObject));
      }
      for (Object deleteOperation: deletedObjects) {
        log.fine("  "+deleteOperation);
      }
      log.fine("now executing flush...");
    }

    flushInserts();
    flushUpdates(updatedObjects);
    flushDeletes();
  }

  protected void removeUnnecessaryOperations() {
    List<DeleteOperation> deletedObjectsCopy = new ArrayList<DeleteOperation>(deletedObjects);
    // for all deleted objects
    for (DeleteOperation deleteOperation: deletedObjectsCopy) {
      if (deleteOperation instanceof DeleteById) {
        DeleteById deleteById = (DeleteById) deleteOperation;
        PersistentObject insertedObject = findInsertedObject(deleteById.persistenceObjectClass, deleteById.persistentObjectId);
        // if the deleted object is inserted,
        if (insertedObject!=null) {
          // remove the insert and the delete
          insertedObjects.remove(insertedObject);
          deletedObjects.remove(deleteOperation);
        }
        // in any case, remove the deleted object from the cache
        cacheRemove(deleteById.persistenceObjectClass, deleteById.persistentObjectId);
      }
    }
    for (PersistentObject insertedObject: insertedObjects) {
      cacheRemove(insertedObject.getClass(), insertedObject.getId());
    }
  }

  protected PersistentObject findInsertedObject(Class< ? > persistenceObjectClass, String persistentObjectId) {
    for (PersistentObject insertedObject: insertedObjects) {
      if ( insertedObject.getClass().equals(persistenceObjectClass)
           && insertedObject.getId().equals(persistentObjectId)
         ) {
        return insertedObject;
      }
    }
    return null;
  }

  protected void flushDeserializedObjects() {
    for (DeserializedObject deserializedObject: deserializedObjects) {
      deserializedObject.flush();
    }
  }

  public List<PersistentObject> getUpdatedObjects() {
    List<PersistentObject> updatedObjects = new ArrayList<PersistentObject>();
    for (Class<?> clazz: cachedObjects.keySet()) {
      Map<String, CachedObject> classCache = cachedObjects.get(clazz);
      for (CachedObject cachedObject: classCache.values()) {
        PersistentObject persistentObject = (PersistentObject) cachedObject.getPersistentObject();
        if (!deletedObjects.contains(persistentObject)) {
          Object originalState = cachedObject.getPersistentObjectState();
          if (!originalState.equals(persistentObject.getPersistentState())) {
            updatedObjects.add(persistentObject);
          } else {
            log.finest("loaded object '"+persistentObject+"' was not updated");
          }
        }
      }
    }
    return updatedObjects;
  }

  protected void flushInserts() {
    for (PersistentObject insertedObject: insertedObjects) {
      String insertStatement = dbSqlSessionFactory.getInsertStatement(insertedObject);
      insertStatement = dbSqlSessionFactory.mapStatement(insertStatement);

      if (insertStatement==null) {
        throw new ActivitiException("no insert statement for "+insertedObject.getClass()+" in the ibatis mapping files");
      }
      
      log.fine("inserting: "+toString(insertedObject));
      sqlSession.insert(insertStatement, insertedObject);
    }
    insertedObjects.clear();
  }

  protected void flushUpdates(List<PersistentObject> updatedObjects) {
    for (PersistentObject updatedObject: updatedObjects) {
      String updateStatement = dbSqlSessionFactory.getUpdateStatement(updatedObject);
      updateStatement = dbSqlSessionFactory.mapStatement(updateStatement);
      if (updateStatement==null) {
        throw new ActivitiException("no update statement for "+updatedObject.getClass()+" in the ibatis mapping files");
      }
      log.fine("updating: "+toString(updatedObject)+"]");
      int updatedRecords = sqlSession.update(updateStatement, updatedObject);
      if (updatedRecords!=1) {
        throw new ActivitiOptimisticLockingException(toString(updatedObject)+" was updated by another transaction concurrently");
      }
    }
    updatedObjects.clear();
  }

  protected void flushDeletes() {
    for (DeleteOperation delete: deletedObjects) {
      log.fine("executing: "+delete);
      delete.execute();
    }
    deletedObjects.clear();
  }

  public void close() {
    sqlSession.close();
  }

  public void commit() {
    sqlSession.commit();
  }

  public void rollback() {
    sqlSession.rollback();
  }

  protected String toString(PersistentObject persistentObject) {
    if (persistentObject==null) {
      return "null";
    }
    return ClassNameUtil.getClassNameWithoutPackage(persistentObject)+"["+persistentObject.getId()+"]";
  }
  
  // schema operations ////////////////////////////////////////////////////////
  
  
  public void dbSchemaCheckVersion() {
    try {
      String dbVersion = getDbVersion(sqlSession);
      if (!ProcessEngine.VERSION.equals(dbVersion)) {
        throw new ActivitiWrongDbException(ProcessEngine.VERSION, dbVersion);
      }

    } catch (Exception e) {
      if (isMissingTablesException(e)) {
        throw new ActivitiException(
                "no activiti tables in db.  set <property name=\"databaseSchemaUpdate\" to value=\"true\" or value=\"create-drop\" (use create-drop for testing only!) in bean processEngineConfiguration in activiti.cfg.xml for automatic schema creation", e);
      } else {
        if (e instanceof RuntimeException) {
          throw (RuntimeException) e;
        } else {
          throw new ActivitiException("couldn't get db schema version", e);
        }
      }
    }

    log.fine("activiti db schema check successful");
  }

  protected String getDbVersion(SqlSession sqlSession) {
    String selectSchemaVersionStatement = dbSqlSessionFactory.mapStatement("selectDbSchemaVersion");
    return (String) sqlSession.selectOne(selectSchemaVersionStatement);
  }

  public void dbSchemaCreate() {
    executeSchemaResourceOperation("create", "create");
  }

  public void dbSchemaDrop() {
    executeSchemaResourceOperation("drop", "drop");
  }
  
  public void dbSchemaUpgrade() {
    // the next piece assumes both DB version and library versions are formatted 5.x
    PropertyEntity dbVersionProperty = selectById(PropertyEntity.class, "schema.version");
    String dbVersion = dbVersionProperty.getValue();
    
    if (!ProcessEngine.VERSION.equals(dbVersion)) {
      PropertyEntity dbHistoryProperty;
      if ("5.0".equals(dbVersion)) {
        dbHistoryProperty = new PropertyEntity("schema.history", "create(5.0)");
        insert(dbHistoryProperty);
      } else {
        dbHistoryProperty = selectById(PropertyEntity.class, "schema.history");
      }
      
      String dbHistoryValue = dbHistoryProperty.getValue()+" upgrade("+dbVersion+"->"+ProcessEngine.VERSION+")";
      dbHistoryProperty.setValue(dbHistoryValue);
      
      
      int minorDbVersionNumber = Integer.parseInt(dbVersion.substring(2));
      String libraryVersion = ProcessEngine.VERSION;
      if (ProcessEngine.VERSION.endsWith("-SNAPSHOT")) {
        libraryVersion = ProcessEngine.VERSION.substring(0, ProcessEngine.VERSION.length()-"-SNAPSHOT".length());
      }
      int minorLibraryVersionNumber = Integer.parseInt(libraryVersion.substring(2));
      
      while (minorDbVersionNumber<minorLibraryVersionNumber) {
        upgradeStepStaticResource(minorDbVersionNumber);
        upgradeStepJavaClass(minorDbVersionNumber);
        minorDbVersionNumber++;
      }
    }
  }

  protected void upgradeStepJavaClass(int minorDbVersionNumber) {
    String upgradestepClassName = "org.activiti.engine.impl.db.upgrade.DbUpgradeStep5"+minorDbVersionNumber;
    DbUpgradeStep dbUpgradeStep = null;
    try {
      dbUpgradeStep = (DbUpgradeStep) ReflectUtil.instantiate(upgradestepClassName);
    } catch (ActivitiException e) {
    }
    if (dbUpgradeStep!=null) {
      try {
        dbUpgradeStep.execute(this);
      } catch (Exception e) {
        throw new ActivitiException("Error during "+upgradestepClassName+": "+e, e);
      }
    } else {
      log.fine("no upgrade class "+upgradestepClassName+" for upgrade step from 5."+minorDbVersionNumber+" to 5."+(minorDbVersionNumber+1));
    }
  }

  protected void upgradeStepStaticResource(int minorDbVersionNumber) {
    String resourceName = getResourceForDbOperation("upgrade", "upgradestep.5"+minorDbVersionNumber);
    InputStream inputStream = ReflectUtil.getResourceAsStream(resourceName);
    if (inputStream!=null) {
      try {
        executeSchemaResource("upgrade", resourceName, inputStream);
        
      } finally {
        IoUtil.closeSilently(inputStream);
      }
    } else {
      log.fine("no upgrade script "+resourceName+" for upgrade step from 5."+minorDbVersionNumber+" to 5."+(minorDbVersionNumber+1));
    }
  }

  public void executeSchemaResourceOperation(String directory, String operation) {
    executeSchemaResource(operation, getResourceForDbOperation(directory, operation));
  }

  public String getResourceForDbOperation(String directory, String operation) {
    String databaseType = dbSqlSessionFactory.getDatabaseType();
    return "org/activiti/db/" + directory + "/activiti." + databaseType + "." + operation + ".sql";
  }

  public void executeSchemaResource(String operation, String resourceName) {
    InputStream inputStream = null;
    try {
      inputStream = ReflectUtil.getResourceAsStream(resourceName);
      if (inputStream == null) {
        throw new ActivitiException("resource '" + resourceName + "' is not available");
      }

      executeSchemaResource(operation, resourceName, inputStream);

    } finally {
      IoUtil.closeSilently(inputStream);
    }
  }

  private void executeSchemaResource(String operation, String resourceName, InputStream inputStream) {
    String sqlStatement = null;
    try {
      Connection connection = sqlSession.getConnection();
      Exception exception = null;
      byte[] bytes = IoUtil.readInputStream(inputStream, resourceName);
      String ddlStatements = new String(bytes);
      StringTokenizer tokenizer = new StringTokenizer(ddlStatements, ";");
      while (tokenizer.hasMoreTokens()) {
        sqlStatement = tokenizer.nextToken().trim();
        if (!sqlStatement.startsWith("#") && !"".equals(sqlStatement)) {
          Statement jdbcStatement = connection.createStatement();
          try {
            jdbcStatement.execute(sqlStatement);
            jdbcStatement.close();
          } catch (Exception e) {
            if (exception == null) {
              exception = e;
            }
            log.log(Level.SEVERE, "problem during schema " + operation + ", statement '" + sqlStatement, e);
          }
        }
      }

      if (exception != null) {
        throw exception;
      }
      
      log.fine("activiti db schema " + operation + " successful");
      
    } catch (Exception e) {
      throw new ActivitiException("couldn't "+operation+" db schema: "+sqlStatement, e);
    }
  }
  
  protected boolean isMissingTablesException(Exception e) {
    String exceptionMessage = e.getMessage();
    if(e.getMessage() != null) {      
      // Matches message returned from H2
      if ((exceptionMessage.indexOf("Table") != -1) && (exceptionMessage.indexOf("not found") != -1)) {
        return true;
      }
      
      // Message returned from MySQL and Oracle
      if (((exceptionMessage.indexOf("Table") != -1 || exceptionMessage.indexOf("table") != -1)) && (exceptionMessage.indexOf("doesn't exist") != -1)) {
        return true;
      }
      
      // Message returned from Postgres
      if (((exceptionMessage.indexOf("relation") != -1 || exceptionMessage.indexOf("table") != -1)) && (exceptionMessage.indexOf("does not exist") != -1)) {
        return true;
      }
    }
    return false;
  }
  
  public void performSchemaOperationsProcessEngineBuild() {
    String databaseSchemaUpdate = CommandContext.getCurrent().getProcessEngineConfiguration().getDatabaseSchemaUpdate();
    if (ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_DROP_CREATE.equals(databaseSchemaUpdate)) {
      try {
        dbSchemaDrop();
      } catch (RuntimeException e) {
        // ignore
      }
    }
    if ( org.activiti.engine.ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP.equals(databaseSchemaUpdate) 
         || ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_DROP_CREATE.equals(databaseSchemaUpdate)
         || ProcessEngineConfigurationImpl.DB_SCHEMA_UPDATE_CREATE.equals(databaseSchemaUpdate)
       ) {
      dbSchemaCreate();
      
    } else if (org.activiti.engine.ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE.equals(databaseSchemaUpdate)) {
      dbSchemaCheckVersion();
      
    } else if (ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE.equals(databaseSchemaUpdate)) {
      try {
        dbSchemaCheckVersion();
      } catch (Exception e) {
        if (e.getMessage().indexOf("no activiti tables in db")!=-1) {
          dbSchemaCreate();
        } else {
          dbSchemaUpgrade();
        }
      }
    }
  }

  public void performSchemaOperationsProcessEngineClose() {
    String databaseSchemaUpdate = CommandContext.getCurrent().getProcessEngineConfiguration().getDatabaseSchemaUpdate();
    if (org.activiti.engine.ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP.equals(databaseSchemaUpdate)) {
      dbSchemaDrop();
    }
  }

  

  // getters and setters //////////////////////////////////////////////////////
  
  public SqlSession getSqlSession() {
    return sqlSession;
  }
  public DbSqlSessionFactory getDbSqlSessionFactory() {
    return dbSqlSessionFactory;
  }
}
