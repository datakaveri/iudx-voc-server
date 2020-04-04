package io.vertx.vocserver.http;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import io.vertx.vocserver.database.DBService;


public class HttpServerVerticle extends AbstractVerticle {

    public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
    public static final String CONFIG_DB_QUEUE = "vocserver.queue";

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

    private DBService dbService;

    @Override
    public void start(Promise<Void> promise) throws Exception {
        String dbQueue = config().getString(CONFIG_DB_QUEUE, "vocserver.queue");
        dbService = DBService.createProxy(vertx, dbQueue);
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.get("/").handler(this::indexHandler);

        int portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
        server
            .requestHandler(router)
            .listen(portNumber, ar -> {
                if (ar.succeeded()) {
                    LOGGER.info("HTTP server running on port " + portNumber);
                    promise.complete();
                } else {
                    LOGGER.error("Could not start a HTTP server", ar.cause());
                    promise.fail(ar.cause());
                }
            });
    }

    // tag::db-service-calls[]
    private void indexHandler(RoutingContext context) {
        LOGGER.info("Hit Index!");
        dbService.fetch(reply -> {
            if (reply.succeeded()) {
                context.put("title", "IUDX Voc Home");
                context.put("pages", reply.result().getList());
            } else {
                context.fail(reply.cause());
            }
        });
    }
}
