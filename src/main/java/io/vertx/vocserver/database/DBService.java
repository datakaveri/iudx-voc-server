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

    @Fluent
    DBService fetch(Handler<AsyncResult<JsonArray>> resultHandler);

    @GenIgnore
    static DBService create(MongoClient dbClient,  Handler<AsyncResult<DBService>> readyHandler) {
        return new DBServiceImpl(dbClient, readyHandler);
    }

    @GenIgnore
    static DBService createProxy(Vertx vertx, String address) {
        return new DBServiceVertxEBProxy(vertx, address);
    }
}
