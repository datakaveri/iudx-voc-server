package io.vertx.vocserver.database;

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
import java.util.HashMap;

@ProxyGen
@VertxGen
public interface DBService {
    /**
     * DBService interface
     */

    /**
     * getMasterContext - Gets json-ld iudx master context
     * @param name Property/Class Name
     * @return {@link DBServiceImpl}
     */
    @Fluent
    DBService getMasterContext(Handler<AsyncResult<JsonArray>> resultHandler);

    /**
     * getSchema - Gets json-ld for the given class or property
     * @param name Property/Class Name
     * @return {@link DBServiceImpl}
     */
    @Fluent
    DBService getSchema(String name, Handler<AsyncResult<JsonArray>> resultHandler);

    /**
     * insertProperty - Insert a property
     * @param prop property schema validated JsonObject
     * @return {@link DBServiceImpl}
     */
    @Fluent
    DBService insertProperty(JsonObject prop, Handler<AsyncResult<JsonArray>> resultHandler);

    /**
     * insertClass - Insert a class
     * @param cls class schema validated JsonObject
     * @return {@link DBServiceImpl}
     */
    @Fluent
    DBService insertClass(JsonObject cls, Handler<AsyncResult<JsonArray>> resultHandler);


    @GenIgnore
    static DBService create(MongoClient dbClient,  Handler<AsyncResult<DBService>> readyHandler) {
        return new DBServiceImpl(dbClient, readyHandler);
    }

    @GenIgnore
    static DBService createProxy(Vertx vertx, String address) {
        return new DBServiceVertxEBProxy(vertx, address);
    }
}
