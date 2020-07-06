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

    void insertIndex(JsonArray body, Handler<AsyncResult<JsonObject>> resultHandler);
    void deleteFromIndex(String uid, Handler<AsyncResult<Boolean>> resultHandler);
    void deleteIndex(Handler<AsyncResult<Boolean>> resultHandler);
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