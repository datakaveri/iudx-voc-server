package iudx.vocserver.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.ext.mongo.MongoClient;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

// tag::dbverticle[]
public class DBVerticle extends AbstractVerticle {

    public static final String CONFIG_DB_MONGO_URL = "vocserver.mongo.url";
    public static final String CONFIG_DB_MONGO_NAME = "vocserver.mongo.dbname";
    public static final String CONFIG_DB_QUEUE = "vocserver.queue";
    public static final String CONFIG_DB_MONGO_POOLNAME = "vocserver.mongo.poolname";
    private static final Logger LOGGER = LoggerFactory.getLogger(DBVerticle.class);

    @Override
    public void start(Promise<Void> promise) throws Exception {

        /* Load default mongo client config if none specified*/
        JsonObject mongoconfig = new JsonObject()
            .put("connection_string", config().getString(CONFIG_DB_MONGO_URL, "mongodb://localhost:27017"))
            .put("db_name", config().getString(CONFIG_DB_MONGO_NAME, "voc"));
        MongoClient dbClient = MongoClient.createShared(vertx, mongoconfig);
        DBService.create(dbClient, ready -> {
            if (ready.succeeded()) {
                ServiceBinder binder = new ServiceBinder(vertx);
                binder
                    .setAddress(CONFIG_DB_QUEUE)
                    .register(DBService.class, ready.result());
                promise.complete();
            } else {
                LOGGER.info("Promise Failed");
                promise.fail(ready.cause());
            }
        });
    }
}
// end::dbverticle[]
