/**
 * <h1>DBServiceImpl.java</h1>
 * Service Implementations for the DBService
 */

package iudx.vocserver.database;

import io.vertx.core.*;
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
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.core.VertxException;
import io.vertx.core.Promise;


class DBServiceImpl implements DBService {
    /**
     * Implementation of the DBService Interface
     * @param dbClient MongoDB Client
     * @param readyHandler Async query result handler. Returns query results as JSONArray
     */

    private static final Logger LOGGER = LoggerFactory.getLogger(DBServiceImpl.class);
    private final MongoClient dbClient;
    
    //Create Web client
    private Vertx vertx = Vertx.vertx();
    WebClientOptions searchClientOptions = new WebClientOptions()
                                            .setSsl(false);
    private WebClient indexClient = WebClient.create(vertx, searchClientOptions);


    /** Queries */ 

    // Find all class
    private static final String QUERY_FIND_ALL_CLASSES = "{\"type\": \"class\"}";
    private static final String QUERY_FIND_ALL_PROPERTIES = "{\"type\": \"property\"}";


    // Find a class or property
    private static final String QUERY_MATCH_ID = 
        "{\"_id\": \"$1\"}";


    private static final String QUERY_SUMMARIZE = 
        "[ { \"$match\": { \"_id\": \"iudx:$1\" } },"
        +    "{ \"$unwind\": \"$@graph\" },"
        +    "{ \"$match\": { \"@graph.rdfs:label\": \"$1\" } },"
        +    "{ \"$project\": { \"_id\": \"$@graph.rdfs:label\","
        +            "\"type\": \"$2\","
        +            "\"label\": \"$@graph.rdfs:label\","
        +            "\"comment\": \"$@graph.rdfs:comment\","
        +            "\"subClassOf\": \"$@graph.rdfs:subClassOf.@id\","
        +            "\"dataModelDomain\": \"$@graph.iudx:dataModelDomain.@id\""
        +     "}}, {\"$merge\": \"summary\"}]";

 



    private static final String QUERY_SIMPLE_SEARCH =
        "{\"$or\": [{\"comment\": {\"$regex\": \"(?i).*$1.*\"}},"
                 + "{\"label\": {\"$regex\": \"(?i).*$1.*\"}}]}";
        
    private static final String QUERY_RELATIONSHIP_SEARCH =
        "{\"$1\": \"$2\"}";


    DBServiceImpl(MongoClient dbClient, Handler<AsyncResult<DBService>> readyHandler) {
        this.dbClient = dbClient;
        readyHandler.handle(Future.succeededFuture(this));
    }

    /**
     * @{@inheritDoc}
     * @TODO: Batch size issue. Need to iterate cursor or find alternative solution later.
     */
    @Override
    public DBService makeSummary(String name, Handler<AsyncResult<JsonObject>> resultHandler) {
        boolean isClass = Character.isUpperCase(name.charAt(0));
        String collectionName = isClass?"classes":"properties";
        String type = isClass?"class":"property";
        JsonObject command = new JsonObject()
                                .put("aggregate", collectionName)
                                .put("pipeline",
                                        new JsonArray(QUERY_SUMMARIZE
                                                        .replace("$1", name)
                                                        .replace("$2", type)))
                                .put("cursor",  new JsonObject().put("batchSize", 1000));
        dbClient.runCommand("aggregate", command, res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            } else {
                LOGGER.info(res.cause());
                resultHandler.handle(Future.failedFuture(res.cause()));
            }
        });
        return this;
    }

    public void createIndex(Handler<AsyncResult<JsonObject>> resultHandler) {
        indexClient
        .post(7700, "localhost", "/indexes")
        .sendJsonObject(new JsonObject()
        .put("uid", "summary")
        .put("primaryKey", "_id"), ar -> {
        if (ar.succeeded() && ar.result().statusCode()==201) {
            LOGGER.info("Index Created");
        }
        else {
            LOGGER.info(ar.cause());
        } 
        });
    }

    @Override
    public DBService insertIndex(Handler<AsyncResult<JsonObject>> resultHandler) {
               
        //check if index exists
        JsonObject query = new JsonObject();
        indexClient
        .get(7700,"localhost","indexes/summary")
        .send(ar -> {
            if (ar.succeeded() && ar.result().statusCode()==200){
                LOGGER.info("Index exists!");
            }
            else {
                LOGGER.info("Index not found");
                LOGGER.info("Creating Index");
                createIndex(resultHandler);
            }
        });

        dbClient.find("summary", query, res -> {
        if (res.succeeded()) {
            JsonArray body = new JsonArray();
            for (JsonObject json : res.result()) {
                body.add(json);
                break;
            }
            LOGGER.info(body);
            indexClient
            .post(7700, "localhost", "indexes/summary/documents")
            .putHeader("content-type","application/json")
            .sendJson(body.encode(),ar->{
                if (ar.succeeded() && ar.result().statusCode()==202){
                    LOGGER.info("Successful"); 
                }
                else{
                    LOGGER.info("Error inserting");
                    LOGGER.info(ar.result().statusCode());
                    LOGGER.info(ar.result().body());
                }
            });
        }
        else {
            LOGGER.info("Couldn't read");
            res.cause().printStackTrace();
        }
        });
        return this;
    }

    public DBService deleteFromIndex(String uid, Handler<AsyncResult<JsonObject>> resultHandler) {
        JsonObject request = new JsonObject();
        String uri = "indexes/summary/documents/" + uid;
        LOGGER.info(uri);
        indexClient
        .delete(7700,"localhost",uri)
        .send(ar->{
            if(ar.succeeded() && ar.result().statusCode()==202){
                LOGGER.info("Successfully deleted");
            }
            else{
                LOGGER.info(ar.cause());
                LOGGER.info(ar.result().statusCode());
            }
        });
        return this;
    }
    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService getMasterContext(Handler<AsyncResult<JsonObject>> resultHandler) {
        dbClient.findWithOptions("master",
                new JsonObject(),
                new FindOptions().setFields(new JsonObject().put("_id", false)
                    .put("@id", false))
                .setLimit(1),
                res -> {
                    if (res.succeeded()) {
                        JsonObject obj = res.result().get(0);
                        resultHandler.handle(Future.succeededFuture(obj));
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
        dbClient.findWithOptions("summary",
                new JsonObject(QUERY_FIND_ALL_CLASSES),
                new FindOptions().setFields(new JsonObject().put("_id", false)
                    .put("@id", false)),
            res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture(new JsonArray(res.result())));
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
        dbClient.findWithOptions("summary",
                new JsonObject(QUERY_FIND_ALL_PROPERTIES),
                new FindOptions().setFields(new JsonObject().put("_id", false)
                    .put("@id", false)),
            res -> {
            if (res.succeeded()) {
                resultHandler.handle(Future.succeededFuture(new JsonArray(res.result())));
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
    public DBService search(String pattern, Handler<AsyncResult<JsonArray>> resultHandler) {
        dbClient.findWithOptions("summary",
                new JsonObject(QUERY_SIMPLE_SEARCH.replace("$1", pattern)),
                new FindOptions().setFields(new JsonObject().put("_id", false)),
                res -> {
                    if (res.succeeded()) {
                        resultHandler.handle(Future.succeededFuture(new JsonArray(res.result())));
                    } else {
                        LOGGER.info("Simple search for " + pattern + " failed");
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService relationshipSearch(String key, String value, Handler<AsyncResult<JsonArray>> resultHandler) {
        dbClient.findWithOptions("summary",
                new JsonObject(QUERY_RELATIONSHIP_SEARCH.replace("$1", key).replace("$2", "iudx:" + value)),
                new FindOptions().setFields(new JsonObject().put("_id", false)),
                res -> {
                    if (res.succeeded()) {
                        resultHandler.handle(Future.succeededFuture(new JsonArray(res.result())));
                    } else {
                        LOGGER.info("Relationship search for " + key + " and value " + value);
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService insertMasterContext(JsonObject context,
            Handler<AsyncResult<Boolean>> resultHandler) {
        dbClient.dropCollection("master",
                res -> {
                    if (!res.succeeded()) {
                        LOGGER.error("Failed inserting master schema");
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        dbClient.insert("master", context,
                res -> {
                    if (res.succeeded()) {
                        resultHandler.handle(Future.succeededFuture());
                    } else {
                        LOGGER.error("Failed inserting master schema");
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
            Handler<AsyncResult<Boolean>> resultHandler) {
        prop = prop.put("_id", "iudx:"+name);
        dbClient.save("properties",
                prop,
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
            Handler<AsyncResult<Boolean>> resultHandler) {
        cls = cls.put("_id", "iudx:"+name);
        dbClient.save("classes",
                cls,
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
    public DBService deleteMaster(Handler<AsyncResult<Void>> resultHandler) {
        dbClient.dropCollection("master",
                res -> {
                    if (res.succeeded()) {
                        resultHandler.handle(Future.succeededFuture());
                    } else {
                        LOGGER.error("Failed deleting class, may not exist");
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
            Handler<AsyncResult<Boolean>> resultHandler) {
        LOGGER.info("Deleteing class " + name);
        dbClient.findOneAndDelete("classes",
                new JsonObject(QUERY_MATCH_ID.replace("$1", "iudx:"+name)),
                res -> {
                    if (res.succeeded()) {
                        resultHandler.handle(Future.succeededFuture(true));
                    } else {
                        LOGGER.error("Failed deleting class, may not exist");
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }

                });
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService deleteFromSummary(String name,
            Handler<AsyncResult<Boolean>> resultHandler) {
        LOGGER.info("Deleteing class " + name);
        dbClient.findOneAndDelete("summary",
                new JsonObject(QUERY_MATCH_ID.replace("$1", name)),
                res -> {
                    if (res.succeeded()) {
                        resultHandler.handle(Future.succeededFuture(true));
                    } else {
                        LOGGER.error("Failed deleting from summary");
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }

                });
        return this;
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService deleteProperty(String name,
            Handler<AsyncResult<Boolean>> resultHandler) {
        dbClient.findOneAndDelete("properties",
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

    /**
     * @{@inheritDoc}
     */
    @Override
    public DBService clearDB(Handler<AsyncResult<Boolean>> resultHandler) {
        dbClient.removeDocuments("classes",
                new JsonObject(),
                res -> {
                    if (res.succeeded()) {
                    } else {
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        dbClient.removeDocuments("properties",
                new JsonObject(),
                res -> {
                    if (res.succeeded()) {
                    } else {
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        dbClient.removeDocuments("master",
                new JsonObject(),
                res -> {
                    if (res.succeeded()) {
                    } else {
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        dbClient.removeDocuments("summary",
                new JsonObject(),
                res -> {
                    if (res.succeeded()) {
                    } else {
                        resultHandler.handle(Future.failedFuture(res.cause()));
                    }
                });
        return this;
    }

}
