/**
 * <h1>SearchServiceImpl.java</h1>
 * Search Service implementation
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

class SearchServiceImpl implements SearchService{

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
    private WebClient searchClient;

    SearchServiceImpl(WebClient searchClient, Handler<AsyncResult<SearchService>> readyHandler) {
        this.searchClient = searchClient;
        readyHandler.handle(Future.succeededFuture(this));
    }

    /** 
     *  Insert documents into summary index
     */
    @Override
    public void insertIndex(JsonArray body, Handler<AsyncResult<JsonObject>> resultHandler) {

        //check if index exists 
        searchClient
        .get(7700,"search", "/indexes/summary")
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

        searchClient
        .post(7700, "search", "/indexes/summary/documents")
        .putHeader("content-type", "application/json")
        .sendJson(body, ar->{
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
        searchClient
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
        searchClient
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

    /** 
     *  Search the summary index 
     *  
     */
    public void searchIndex(String pattern, Handler<AsyncResult<JsonArray>> resultHandler) {
        searchClient
            .get(7700, "search", "/indexes/summary/search") 
            .addQueryParam("q", pattern)
            .putHeader("Accept", "application/json").send(ar -> {
            if (ar.succeeded()) {
                resultHandler.handle(Future.succeededFuture(ar.result().body().toJsonObject().getJsonArray("hits")));
            }
            else {
                LOGGER.info("Failed searching, query params not found");
                resultHandler.handle(Future.failedFuture(ar.cause()));
            }
        });
    }
} 
