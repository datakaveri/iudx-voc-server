package io.vertx.vocserver.database;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import java.util.stream.Collectors;



class DBServiceImpl implements DBService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBServiceImpl.class);

    DBServiceImpl(MongoClient dbClient, Handler<AsyncResult<DBService>> readyHandler) {
    }

    @Override
    public DBService fetch(Handler<AsyncResult<JsonArray>> resultHandler) {
        return this;
    }

}
