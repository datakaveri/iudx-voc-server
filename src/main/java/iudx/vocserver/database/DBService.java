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
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.mongo.MongoClient;
import java.util.List;


@ProxyGen
@VertxGen
public interface DBService {
    /**
     * DBService interface
     */

    /**
     * getMasterContext - Gets json-ld iudx master context
     * @return {@link DBServiceImpl}
     */
    @Fluent
    DBService getMasterContext(Handler<AsyncResult<JsonObject>> resultHandler);

    /**
     * insertMasterContext - insert json-ld iudx master context
     * @param contex JsonObject Master Context
     * @return {@link DBServiceImpl}
     */
    @Fluent
    DBService insertMasterContext(JsonObject context, Handler<AsyncResult<Boolean>> resultHandler);

    /**
     * getAllProperties - Gets all vocabulary properties
     * @param name Property Name
     * @return {@link DBServiceImpl}
     */
    @Fluent
    DBService getAllProperties(Handler<AsyncResult<JsonArray>> resultHandler);

    /**
     * getAllClasses - Gets all vocabulary classes
     * @param name Property Name
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
     * incSearch - Incrementaly searches for a given pattern
     * @param pattern pattern
     * @return {@link DBServiceImpl}
     */
    @Fluent
    DBService fuzzySearch(String pattern, Handler<AsyncResult<JsonArray>> resultHandler);

    /**
     * makeSummary - Make summary of class labels and comments 
     * @return {@link DBServiceImpl}
     */
    @Fluent
    DBService makeSummary(Handler<AsyncResult<JsonObject>> resultHandler);

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
     * deleteProperty - Delete a property
     * @param name name of the property
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
     * clearDB - Clear all classees, properties, master and summaries
     * @param name name of the class
     * @return {@link DBServiceImpl}
     */
    @Fluent
    DBService clearDB(Handler<AsyncResult<Boolean>> resultHandler);


    @GenIgnore
    static DBService create(MongoClient dbClient,  Handler<AsyncResult<DBService>> readyHandler) {
        return new DBServiceImpl(dbClient, readyHandler);
    }

    @GenIgnore
    static DBService createProxy(Vertx vertx, String address) {
        return new DBServiceVertxEBProxy(vertx, address);
    }
}
