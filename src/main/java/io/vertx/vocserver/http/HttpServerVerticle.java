/**
* <h1>HttpServerVerticle.java</h1>
* HTTP Server Verticle
* 
*
* @author  Rakshit Ramesh
* @version 1.0
* @since   2020-04-03
*/

package io.vertx.vocserver.http;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Date;

import io.vertx.vocserver.database.DBService;

public class HttpServerVerticle extends AbstractVerticle {
    /**
     * HttpServerVerticle Class
     * @param dbClient MongoDB Client
     * @param readyHandler Async query result handler. Returns query results as JSONArray
     */

    // Config variables
    public static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
    public static final String CONFIG_DB_QUEUE = "vocserver.queue";

    // Default logger
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

    // iudx-voc-server DBService
    private DBService dbService;

    /**
     * AbstractVerticle start
     * */
    @Override
    public void start(Promise<Void> promise) throws Exception {

        String dbQueue = config().getString(CONFIG_DB_QUEUE, "vocserver.queue");
        dbService = DBService.createProxy(vertx, dbQueue);
        HttpServer server = vertx.createHttpServer();

        /** ROUTES */
        Router router = Router.router(vertx);
        /** Get classes or properties by name */
        router.get("/:name").handler(this::getSchemaHandler);

        /* Defaults to 8080 */
        /** @TODO: Make port configureable */
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

    /**
     * getSchemaHandler - get classes or properties by name
     */
    // tag::db-service-calls[]
    private void getSchemaHandler(RoutingContext context) {
        String name = context.request().getParam("name");
        dbService.getSchema(name, reply -> {
            if (reply.succeeded()) {
                context.response().putHeader("Content-Type", "application/json");
                context.response().end(reply.result().encode());
            } else {
                context.response().setStatusCode(404);
                context.response().putHeader("Content-Type", "application/json");
                context.response().end(reply.result().encode());
            }
        });
    }

    // tag::db-service-calls[]
    private void createPropHandler(RoutingContext context) {
        LOGGER.debug("Hit create Prop Handler");
        JsonObject body = context.getBodyAsJson();
        LOGGER.debug("Creating property");
        dbService.newProperty(body, reply -> {
            if (reply.succeeded()) {
                context.put("title", "Success");
                context.put("pages", reply.result().getList());
            } else {
                context.fail(reply.cause());
            }
        });
    }
}
