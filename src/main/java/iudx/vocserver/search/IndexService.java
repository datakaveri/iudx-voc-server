/**
 * <h1>IndexService.java</h1>
 * Index Service interface
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
public interface IndexService {
    @GenIgnore
    static IndexService create(WebClient indexClient, Handler<AsyncResult<IndexService>> readyHandler) {
        return new IndexServiceImpl(indexClient, readyHandler);
    }

    static IndexService createProxy(Vertx vertx, String address) {
         return new IndexServiceVertxEBProxy(vertx, address);
    }

    void createIndex(Handler<AsyncResult<JsonObject>> resultHandler);
    void insertIndex(JsonArray body, Handler<AsyncResult<JsonObject>> resultHandler);
    void deleteFromIndex(String uid, Handler<AsyncResult<Boolean>> resultHandler);
    void deleteIndex(Handler<AsyncResult<Boolean>> resultHandler);

}