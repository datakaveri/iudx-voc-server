/**
 * <h1>SearchService.java</h1>
 * Search Service interface
 */
package iudx.vocserver.search;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.codegen.annotations.ProxyClose;
import io.vertx.core.json.JsonArray;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import java.util.List;

@ProxyGen
@VertxGen
public interface SearchService {
  /**
   * Search Service Interface
   */

  /**
   * insertIndex - Insert the document into the search index
   * @param body JsonArray of document to be inserted
   * @return void
   */
  void insertIndex(JsonArray body, Handler<AsyncResult<JsonObject>> resultHandler);

  /**
   * deleteFromIndex - Delete the document from the search index
   * @param uid unique identifier for document
   * @return void
   */
  void deleteFromIndex(String uid, Handler<AsyncResult<Boolean>> resultHandler);

  /**
   * deleteIndex - Delete the whole index
   * @return void
   */
  void deleteIndex(Handler<AsyncResult<Boolean>> resultHandler);

  /**
   * searchIndex - search for the document in the index
   * @param pattern search query
   * @return void
   */
  void searchIndex(String pattern, Handler<AsyncResult<JsonArray>> resultHandler);

  @GenIgnore
  static SearchService create(WebClient searchClient, Handler<AsyncResult<SearchService>> readyHandler) {
    return new SearchServiceImpl(searchClient, readyHandler);
  }

  @GenIgnore
  static SearchService createProxy(Vertx vertx, String address) {
     return new SearchServiceVertxEBProxy(vertx, address);
  }

}