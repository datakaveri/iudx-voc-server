/**
 * <h1>IndexServiceImpl.java</h1>
 * Index Service implementation
 */

package iudx.vocserver.search;

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
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.core.VertxException;

class IndexServiceImpl implements IndexService{

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexService.class);
    private WebClient indexClient;
    private MongoClient dbClient;
    

    IndexServiceImpl(WebClient indexClient, Handler<AsyncResult<IndexService>> readyHandler) {
        this.indexClient = indexClient;
        readyHandler.handle(Future.succeededFuture(this));
    }

    /** 
     *  Call the search service to create summary index
     */
    @Override
    public void createIndex(Handler<AsyncResult<JsonObject>> resultHandler) {
        indexClient
        .post(7700, "search", "/indexes")
        .sendJsonObject(new JsonObject()
        .put("uid", "summary")
        .put("primaryKey", "_id"), ar -> {
        if (ar.succeeded() && ar.result().statusCode()==200) {
            LOGGER.info("Index Created");
            resultHandler.handle(Future.succeededFuture());
        }
        else {
            LOGGER.info(ar.cause());
            resultHandler.handle(Future.failedFuture(ar.cause()));
        } 
        });
    }

    /** 
     *  Insert documents into summary index
     */
    @Override
    public void insertIndex(JsonArray body, Handler<AsyncResult<JsonObject>> resultHandler) {

        //check if index exists 
        indexClient
        .get(7700,"search","/indexes/summary")
        .send(ar -> {
            if (ar.succeeded() && ar.result().statusCode()==200){
                LOGGER.info(ar.result());
                LOGGER.info(ar.result().statusCode());
                LOGGER.info("Index exists!");
            }
            else {
                LOGGER.info(ar.result().statusCode());
                LOGGER.info("Index not found");
                LOGGER.info("Creating Index");
                createIndex(resultHandler);
            }
        });

        indexClient
        .post(7700, "search", "/indexes/summary/documents")
        .putHeader("content-type", "application/json")
        .sendJson(body,ar->{
            if (ar.succeeded() && ar.result().statusCode()==202){
                LOGGER.info("Successful"); 
                resultHandler.handle(Future.succeededFuture());
            }
            else {
                LOGGER.info("Error inserting");
                LOGGER.info(ar.result().statusCode());
                resultHandler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }

    /** 
     *  Delete document from summary index
     *   @param uid String
     */
    @Override
    public void deleteFromIndex(String uid, Handler<AsyncResult<Boolean>> resultHandler) {
        JsonObject request = new JsonObject();
        String uri = "/indexes/summary/documents/" + uid;
        LOGGER.info(uri);
        indexClient
        .delete(7700,"search", uri)
        .send(ar->{
            if(ar.succeeded() && ar.result().statusCode()==202){
                LOGGER.info("Successfully deleted");
                resultHandler.handle(Future.succeededFuture(true));
            }
            else {
                LOGGER.info(ar.cause());
                LOGGER.info(ar.result().statusCode());
                resultHandler.handle(Future.succeededFuture(false));
            }
        });
    }

    /** 
     *  Delete the summary index 
     *  
     */
    public void deleteIndex(Handler<AsyncResult<Boolean>> resultHandler) {
        JsonObject request = new JsonObject();
        indexClient
        .delete(7700,"search", "/indexes/summary")
        .send(ar->{
            if(ar.succeeded() && ar.result().statusCode()==204){
                LOGGER.info("Successfully deleted");
                resultHandler.handle(Future.succeededFuture(true));
            }
            else {
                LOGGER.info(ar.cause());
                LOGGER.info(ar.result().statusCode());
                resultHandler.handle(Future.succeededFuture(false));
            }
        });
    }
} 