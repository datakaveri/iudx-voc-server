/**
 * <h1>DBServiceImpl.java</h1>
 * Service Implementations for the DBService
 */

package iudx.vocserver.database;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.UpdateOptions;



class DBServiceImpl implements DBService {
    /**
     * Implementation of the DBService Interface
     * @param dbClient MongoDB Client
     * @param readyHandler Async query result handler. Returns query results as JSONArray
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(DBServiceImpl.class);
    private final MongoClient dbClient;

    /** Queries */ 

    // Find all class
    private static final String QUERY_FIND_ALL_CLASS = 
        "[ {\"$unwind\": \"$@graph\"}," 
        + " { \"$match\": {\"@graph.@type\": { \"$in\": [\"rdfs:Class\"] }}}," 
        + " { \"$project\": {\"_id\": 0, \"rdfs:label\": \"$@graph.rdfs:label\", \"rdfs:comment\": \"$@graph.rdfs:comment\" } } ])";

    // Find all properties
    private static final String QUERY_FIND_ALL_PROPERTIES = 
        "[ {\"$unwind\": \"$@graph\"}," 
        + " { \"$match\": {\"@graph.@type\": { \"$in\": [\"rdf:Property\"] }}}," 
        + " { \"$project\": {\"_id\": 0, \"rdfs:label\": \"$@graph.rdfs:label\", \"rdfs:comment\": \"$@graph.rdfs:comment\" } } ])";

    // Find a class
    private static final String QUERY_MATCH_ID = 
        "{\"@graph\": {\"$elemMatch\": {\"@id\": \"$1\"}}}";


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
                    } else {
                        LOGGER.error("Failed Getting Master Context");
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        return this;
    }

    /**
     * @{@inheritDoc}
     * @TODO: Batch size issue. Need to iterate cursor or find alternative solution later.
     */
    @Override
    public DBService getAllClasses(Handler<AsyncResult<JsonArray>> resultHandler) {
        JsonObject command = new JsonObject().put("aggregate", "classes")
            .put("pipeline", new JsonArray(QUERY_FIND_ALL_CLASS))
            .put("cursor",  new JsonObject().put("batchSize", 1000));
        dbClient.runCommand("aggregate", command, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture(res.result()
                            .getJsonObject("cursor")
                            .getJsonArray("firstBatch")));
            } else {
                LOGGER.info(res.cause());
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
        return this;
    }

    /**
     * @{@inheritDoc}
     * @TODO: Batch size issue. Need to iterate cursor or find alternative solution later.
     */
    @Override
    public DBService getAllProperties(Handler<AsyncResult<JsonArray>> resultHandler) {
        JsonObject command = new JsonObject().put("aggregate", "properties")
            .put("pipeline", new JsonArray(QUERY_FIND_ALL_PROPERTIES))
            .put("cursor",  new JsonObject().put("batchSize", 10000));
        dbClient.runCommand("aggregate", command, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture(res.result()
                            .getJsonObject("cursor")
                            .getJsonArray("firstBatch")));
            } else {
                LOGGER.info(res.cause());
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
        return this;
    }



    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService getProperty(String name, Handler<AsyncResult<JsonObject>> resultHandler) {
        dbClient.findWithOptions("properties",
                new JsonObject(QUERY_MATCH_ID.replace("$1", "iudx:"+name)),
                new FindOptions().setFields(new JsonObject().put("_id", false)
                    .put("@id", false))
                .setLimit(1),
                res -> {
                    if (res.succeeded()) {
                        try {
                            resultHandler.handle(Future.succeededFuture(res.result().get(0)));
                        } catch (Exception e) {
                            LOGGER.error("Failed Getting Properties " + name);
                            resultHandler.handle(Future.failedFuture(res.cause()));
                        }
                    } else {
                        LOGGER.error("Failed Getting Properties " + name);
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService getClass(String name, Handler<AsyncResult<JsonObject>> resultHandler) {
        dbClient.findWithOptions("classes",
                new JsonObject(QUERY_MATCH_ID.replace("$1", "iudx:"+name)),
                new FindOptions().setFields(new JsonObject().put("_id", false)
                    .put("@id", false))
                .setLimit(1),
                res -> {
                    if (res.succeeded()) {
                        try {
                            resultHandler.handle(Future.succeededFuture(res.result().get(0)));
                        } catch (Exception e) {
                            LOGGER.info("Failed getting property " + name);
                            resultHandler.handle(Future.failedFuture(res.cause()));
                        }
                    } else {
                        LOGGER.info("Failed getting property " + name);
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService insertProperty(String name, JsonObject prop,
            Handler<AsyncResult<JsonObject>> resultHandler) {
        dbClient.updateCollectionWithOptions("properties",
                new JsonObject(QUERY_MATCH_ID.replace("$1", "iudx:"+name)),
                new JsonObject().put("$set", prop),
                new UpdateOptions().setUpsert(true),
                res -> {
                    if (res.succeeded()) {
                        resultHandler.handle(Future.succeededFuture());
                    } else {
                        /** @TODO: Report name */
                        LOGGER.error("Failed inserting property " + name);
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService insertClass(String name, JsonObject cls,
            Handler<AsyncResult<JsonObject>> resultHandler) {
        dbClient.updateCollectionWithOptions("classes",
                new JsonObject(QUERY_MATCH_ID.replace("$1", "iudx:"+name)),
                new JsonObject().put("$set", cls),
                new UpdateOptions().setUpsert(true),
                res -> {
                    if (res.succeeded()) {
                        resultHandler.handle(Future.succeededFuture());
                    } else {
                        /** @TODO: Report name */
                        LOGGER.error("Failed inserting class");
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        return this;
    }


    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService deleteClass(String name,
            Handler<AsyncResult<JsonObject>> resultHandler) {
        dbClient.findOneAndDelete("classes",
                new JsonObject(QUERY_MATCH_ID.replace("$1", "iudx:"+name)),
                res -> {
                    if (res.succeeded()) {
                        resultHandler.handle(Future.succeededFuture());
                    } else {
                        LOGGER.error("Failed deleting class, may not exist");
                    }

                });
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService deleteProperty(String name,
            Handler<AsyncResult<JsonObject>> resultHandler) {
        dbClient.findOneAndDelete("property",
                new JsonObject(QUERY_MATCH_ID.replace("$1", "iudx:"+name)),
                res -> {
                    if (res.succeeded()) {
                        resultHandler.handle(Future.succeededFuture());
                    } else {
                        LOGGER.error("Failed deleting property, may not exist");
                    }

                });
        return this;
    }

}
