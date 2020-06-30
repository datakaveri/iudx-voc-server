package iudx.vocserver.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

// tag::dbverticle[]
public class DBVerticle extends AbstractVerticle {

    public static final String CONFIG_DB_URL = "vocserver.database.url";
    public static final String CONFIG_DB_NAME = "vocserver.database.name";
    public static final String CONFIG_DB_QUEUE = "vocserver.database.queue";
    public static final String CONFIG_DB_POOLNAME = "vocserver.database.poolname";
    public static final String CONFIG_DB_UNAME = "vocserver.database.username";
    public static final String CONFIG_DB_PASSWORD = "vocserver.database.password";
    public static final String CONFIG_SEARCH_QUEUE = "vocserver.search.queue";
    private static final Logger LOGGER = LoggerFactory.getLogger(DBVerticle.class);

    @Override
    public void start(Promise<Void> promise) throws Exception {

        /* Load default mongo client config if none specified*/
        JsonObject mongoconfig = new JsonObject()
            .put("connection_string", config().getString(CONFIG_DB_URL))
            .put("username", config().getString(CONFIG_DB_UNAME))
            .put("password", config().getString(CONFIG_DB_PASSWORD))
            .put("db_name", config().getString(CONFIG_DB_NAME));
        MongoClient dbClient = MongoClient.createShared(vertx,
                                                        mongoconfig,
                                                        config().getString(CONFIG_DB_POOLNAME));
        
        WebClient indexClient = WebClient.create(vertx, new WebClientOptions()
                                                            .setSsl(false));
        
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

        IndexService.create(indexClient, dbClient, ready->{
            if(ready.succeeded()) {
                ServiceBinder binder = new ServiceBinder(vertx);
                binder
                    .setAddress(CONFIG_SEARCH_QUEUE)
                    .register(IndexService.class, ready.result());
                LOGGER.info("Index service deployed");
                promise.complete();
            }
            else {
                LOGGER.info("Promise Failed");
                promise.fail(ready.cause());
            }
        });
    }
}
// end::dbverticle[]
