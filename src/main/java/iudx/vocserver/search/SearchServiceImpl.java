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
    
    private JsonArray stopWords = new JsonArray();

    stopWords = ["i", "me", "my", "myself", "we", "our", "ours", "ourselves", 
    "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself",
    "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their",
    "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these",
    "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had",
    "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", 
    "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against",
    "between", "into", "through", "during", "before", "after", "above", "below", "to", "from",
    "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once",
    "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more",
    "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
    "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"]

    SearchServiceImpl(WebClient searchClient, Handler<AsyncResult<SearchService>> readyHandler) {
        this.searchClient = searchClient;
        readyHandler.handle(Future.succeededFuture(this));
    }

    /** 
     *  Call the search service to create summary index
     */
    @Override
    public void createIndex(Handler<AsyncResult<JsonObject>> resultHandler) {
        searchClient
        .post(7700, "search", "/indexes")
        .sendJsonObject(new JsonObject()
        .put("uid", "summary")
        .put("primaryKey", "_id"), ar -> {
        if (ar.succeeded() && ar.result().statusCode()==200) {
            LOGGER.info("Index Created");
            addStopWords(resultHandler);
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

    /** 
     *  Add list of stopwords to the index
     *  
     */
     public void addStopWords(Handler<AsyncResult<JsonArray>> resultHandler) {
         searchClient
         .post(7700, "search", "/indexes/summary/settings/stop-words")
         .putHeader("content-type", "application/json")
         .sendJson(stopWords, reply -> {
             if(reply.succeeded()) {
                 LOGGER.info("Updated stop words for the index");
                 resultHandler.handle(Future.succeededFuture());
             }
             else {
                 LOGGER.info("Failed to update stop words");
                 resultHandler.handle(Future.failedFuture(ar.cause());
             }
         });
     }

} 
