/**
* <h1>DBService.java</h1>
* DBService interface
*/

package iudx.vocserver.database;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import iudx.vocserver.search.SearchService;

@ProxyGen
@VertxGen
public interface DBService {
  /**
   * DBService interface
   */

  /**
   * makeSummary - Make summary of class labels and comments 
   * @param name name of the property or class
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService makeSummary(String name, Handler<AsyncResult<JsonObject>> resultHandler);

   /**
   * relationshipSearch - Search for a schema either through a relationship
   * @param key key
   * @param value value
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService relationshipSearch(String key, String value, Handler<AsyncResult<JsonArray>> resultHandler);

  /**
   * search - Search for a schema either through name or description
   * @param pattern pattern
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService search(String pattern, Handler<AsyncResult<JsonArray>> resultHandler);

  /**
   * getMasterContext - Gets json-ld iudx master context
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService getMasterContext(Handler<AsyncResult<JsonObject>> resultHandler);

  /**
   * getAllProperties - Gets all vocabulary properties
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService getAllProperties(Handler<AsyncResult<JsonArray>> resultHandler);

  /**
   * getAllClasses - Gets all vocabulary classes
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService getAllClasses(Handler<AsyncResult<JsonArray>> resultHandler);

  /**
   * getProperties - Gets json-ld for the given property
   * @param name Property Name
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService getProperty(String name, Handler<AsyncResult<JsonObject>> resultHandler);

  /**
   * getClass - Gets json-ld for the given class
   * @param name Class Name
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService getClass(String name, Handler<AsyncResult<JsonObject>> resultHandler);
  
  /**
   * getExample - Gets json object for the given type
   * @param name Type name
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService getExamples(String name, Handler<AsyncResult<JsonArray>> resultHandler);
  
  /**
   * getDescriptor - Gets descriptor for the given type
   * @param name Type name
   * @return {@link DBServiceImpl}
   */ 
  @Fluent
  DBService getDescriptor(String name, Handler<AsyncResult<JsonArray>> resultHandler);

  /**
   * insertMasterContext - insert json-ld iudx master context
   * @param contex JsonObject Master Context
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService insertMasterContext(JsonObject context, Handler<AsyncResult<Boolean>> resultHandler);

  /**
   * insertProperty - Insert a property
   * @param name name of the property
   * @param prop property schema validated JsonObject
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService insertProperty(String name, JsonObject prop, Handler<AsyncResult<Boolean>> resultHandler);

  /**
   * insertClass - Insert a class
   * @param name name of the class
   * @param cls class schema validated JsonObject
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService insertClass(String name, JsonObject cls, Handler<AsyncResult<Boolean>> resultHandler);

  /**
   * insertExample - Insert an example
   * @param example example JsonObject
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService insertExamples(String name, JsonObject example, Handler<AsyncResult<Boolean>> resultHandler);

  /**
   * insertDescriptor - Insert a descriptor
   * @param descriptor descriptor JsonObject
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService insertDescriptor(String name, JsonObject descriptor, Handler<AsyncResult<Boolean>> resultHandler);

   /**
   * deleteFromSummary - Delete from summary a class or property 
   * @param name name of the property or class
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService deleteFromSummary(String name, Handler<AsyncResult<Boolean>> resultHandler);

  /**
   * deleteMaster - Delete master
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService deleteMaster(Handler<AsyncResult<Void>> resultHandler);

  /**
   * deleteProperty - Delete a property
   * @param name name of the property
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService deleteProperty(String name, Handler<AsyncResult<Boolean>> resultHandler);

  /**
   * deleteClass - Delete a class
   * @param name name of the class
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService deleteClass(String name, Handler<AsyncResult<Boolean>> resultHandler);

  /**
   * deleteClass - Delete examples
   * @param name type of example
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService deleteExamples(String name, Handler<AsyncResult<Boolean>> resultHandler);

  /**
   * clearDB - Clear all classes, properties, master and summaries
   * @return {@link DBServiceImpl}
   */
  @Fluent
  DBService clearDB(Handler<AsyncResult<Boolean>> resultHandler);


  @GenIgnore
  static DBService create(MongoClient dbClient, SearchService searchClient,
      Handler<AsyncResult<DBService>> readyHandler) {
    return new DBServiceImpl(dbClient, searchClient, readyHandler);
  }

  @GenIgnore
  static DBService createProxy(Vertx vertx, String address) {
    return new DBServiceVertxEBProxy(vertx, address);
  }
}
