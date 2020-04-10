/**
* <h1>DBServiceImpl.java</h1>
* Service Implementations for the DBService
* 
*
* @author  Rakshit Ramesh
* @version 1.0
* @since   2020-04-01
*/

package io.vertx.vocserver.database;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;



class DBServiceImpl implements DBService {
    /**
     * Implementation of the DBService Interface
     * @param dbClient MongoDB Client
     * @param readyHandler Async query result handler. Returns query results as JSONArray
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(DBServiceImpl.class);
    private final MongoClient dbClient;

    DBServiceImpl(MongoClient dbClient, Handler<AsyncResult<DBService>> readyHandler) {
        this.dbClient = dbClient;
        readyHandler.handle(Future.succeededFuture(this));
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService getMasterContext(Handler<AsyncResult<JsonArray>> resultHandler) {
        dbClient.find("master",
                new JsonObject(),
                res -> {
                    if (res.succeeded()) {
                        JsonArray arr = new JsonArray(res.result());
                        resultHandler.handle(Future.succeededFuture(arr));
                    }
                    else {
                        LOGGER.error("Failed Getting Master Context");
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService getSchema(String name, Handler<AsyncResult<JsonArray>> resultHandler) {
        dbClient.find("properties",
                        new JsonObject().put("@id", "iudx:" + name),
                        res -> {
                            if (res.succeeded()) {
                                JsonArray arr = new JsonArray(res.result());
                                resultHandler.handle(Future.succeededFuture(arr));
                            }
                            else {
                                LOGGER.error("Failed Getting Properties/Class \t" + name);
                                resultHandler.handle(Future.failedFuture(res.cause()));
                            }
                        });
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService insertProperty(JsonObject prop,
                                    Handler<AsyncResult<JsonArray>> resultHandler) {
        dbClient.find("properties",
                prop,
                res -> {
                    if (res.succeeded()) {
                        JsonArray arr = new JsonArray(res.result());
                        resultHandler.handle(Future.succeededFuture(arr));
                    }
                    else {
                        /** @TODO: Report name */
                        LOGGER.error("Failed inserting property");
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService insertClass(JsonObject cls,
                                    Handler<AsyncResult<JsonArray>> resultHandler) {
        dbClient.find("classes",
                cls,
                res -> {
                    if (res.succeeded()) {
                        JsonArray arr = new JsonArray(res.result());
                        resultHandler.handle(Future.succeededFuture(arr));
                    }
                    else {
                        /** @TODO: Report name */
                        LOGGER.error("Failed inserting class");
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        return this;
    }
    
}
