/**
* <h1>HttpServerVerticle.java</h1>
* HTTP Server Verticle
*/

package iudx.vocserver.http;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.core.VertxException;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.core.http.HttpMethod;

import java.util.Set;
import java.util.HashSet;


import iudx.vocserver.database.DBService;
import iudx.vocserver.auth.AuthService;
import iudx.vocserver.utils.Validator;

public class HttpServerVerticle extends AbstractVerticle {
    /**
     * HttpServerVerticle Class
     * @param dbClient MongoDB Client
     * @param readyHandler Async query result handler. Returns query results as JSONArray
     */

    // Config variables
    public static final String CONFIG_HTTP_CNAME = "vocserver.http.cname";
    public static final String CONFIG_HTTP_SERVER_PORT = "vocserver.http.port";
    public static final String CONFIG_DB_QUEUE = "vocserver.database.queue";
    public static final String CONFIG_AUTH_QUEUE = "vocserver.auth.queue";
    public static final String JKS_FILE = "vocserver.jksfile";
    public static final String JKS_PASSWD = "vocserver.jkspasswd";

    // Default logger
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

    // iudx-voc-server DBService
    private DBService dbService;
    // iudx-voc-server AuthService
    private AuthService authService;

    // Validator objects
    private boolean isValidSchema;
    private Validator classValidator;
    private Validator propertyValidator;

    /**
     * AbstractVerticle start
     * */
    @Override
    public void start(Promise<Void> promise) throws Exception {

        String dbQueue = config().getString(CONFIG_DB_QUEUE);
        String authQueue = config().getString(CONFIG_AUTH_QUEUE);

        dbService = DBService.createProxy(vertx, dbQueue);
        authService = AuthService.createProxy(vertx, authQueue);

        propertyValidator = new Validator("/propertySchema.json");
        classValidator = new Validator("/classSchema.json");

        HttpServerOptions options = new HttpServerOptions()
                                    .setSsl(true)
                                    .setKeyStoreOptions(new JksOptions()
                                        .setPath(config().getString(JKS_FILE))
                                        .setPassword(config().getString(JKS_PASSWD)));
        HttpServer server = vertx.createHttpServer(options);

        /** ROUTES */
        Router router = Router.router(vertx);
        
        /** CORS Related */
        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("accept");
        allowedHeaders.add("token");
        allowedHeaders.add("content-length");
        allowedHeaders.add("content-type");
        allowedHeaders.add("host");
        allowedHeaders.add("origin");
        allowedHeaders.add("referer");
        allowedHeaders.add("access-control-allow-origin");

        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        allowedMethods.add(HttpMethod.DELETE);
        allowedMethods.add(HttpMethod.PATCH);
        allowedMethods.add(HttpMethod.PUT);
        router.route().handler(CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods));
        
        /** Get classes or properties by name (JSON-LD API) */
        router.get("/:name").consumes("application/json+ld").handler(this::getSchemaHandler);
        router.route("/:name").consumes("application/json+ld").handler(BodyHandler.create());
        router.post("/:name").consumes("application/json+ld").handler(this::insertSchemaHandler);

        router.getWithRegex("\\/(?<name>[^\\/]+)\\.jsonld").handler(this::getSchemaHandler);


        /** Get all classes  and properties*/
        router.get("/classes").consumes("application/json").handler(this::getClassesHandler);
        router.get("/properties").consumes("application/json").handler(this::getPropertiesHandler);


        router.route("/").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			response.sendFile("ui/pages/schema_home/index.html");
		});

        router.route("/assets/*").handler(StaticHandler.create("ui/assets"));

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
     * getClassesHandler - handler to get all classes 
     */
    // tag::db-service-calls[]
    private void getClassesHandler(RoutingContext context) {
            dbService.getAllClasses(reply -> {
                if (reply.succeeded()) {
                    context.response().putHeader("content-type", "application/json");
                    context.response().setStatusCode(200)
                                        .end(reply.result().encode());
                }
                else {
                    context.response().putHeader("content-type", "application/json");
                    context.response().setStatusCode(404).end();
                }
            });
    }

    /**
     * getPropertiesHandler - handler to get all properties 
     */
    // tag::db-service-calls[]
    private void getPropertiesHandler(RoutingContext context) {
            dbService.getAllProperties(reply -> {
                if (reply.succeeded()) {
                    context.response().putHeader("content-type", "application/json");
                    context.response().setStatusCode(200)
                                        .end(reply.result().encode());
                }
                else {
                    context.response().putHeader("content-type", "application/json");
                    context.response().setStatusCode(404).end();
                }
            });
    }

    /**
     * getSchemaHandler - handler to get classes or properties by name
     */
    // tag::db-service-calls[]
    private void getSchemaHandler(RoutingContext context) {
        String name = context.request().getParam("name").replace(".jsonld", "");
        /** Check if provided param is class or property */
        boolean isClass = Character.isUpperCase(name.charAt(0));
        /** This can be simplified by setting a flag, leaving it expanded for future use. */
        if (isClass == true) {
            dbService.getClass(name, reply -> {
                if (reply.succeeded()) {
                    context.response().putHeader("content-type", "application/json");
                    context.response().setStatusCode(200)
                                        .end(reply.result().encode());
                }
                else {
                    LOGGER.info("Failed getting class " + name);
                    context.response().putHeader("content-type", "application/json");
                    context.response().setStatusCode(404).end();
                }
            });
        }
        else if (isClass == false) {
            dbService.getProperty(name, reply -> {
                if (reply.succeeded()) {
                    context.response().putHeader("content-type", "application/json");
                    context.response().setStatusCode(200)
                                        .end(reply.result().encode());
                }
                else {
                    context.response().putHeader("content-type", "application/json");
                    context.response().setStatusCode(404).end();
                }
            });
        }
    }


    /**
     * insertSchemaHandler - handler to insert a class or property
     * @TODO: Check duplicates
     */
    // tag::db-service-calls[]
    private void insertSchemaHandler(RoutingContext context) {
        String name = context.request().getParam("name");
        String body = context.getBodyAsString();
        /** Check if provided param is class or property */
        boolean isClass = Character.isUpperCase(name.charAt(0));
        /** This can be simplified by setting a flag, leaving it expanded for future use. */
        context.response().putHeader("content-type", "application/json");
        /** Validate token */
        String token = context.request().getHeader("token");
        /** Sever ID is the vocab server domain name*/
        String serverId = config().getString(CONFIG_HTTP_CNAME);
        authService.validateToken(token, serverId,
            authreply -> {
                if (authreply.succeeded()) {
                    if (isClass == true) {
                        try {
                            isValidSchema = classValidator.validate(body);
                        }
                        catch (Exception e) {
                            isValidSchema = false;
                        }
                        if (isValidSchema == false) {
                            LOGGER.info("Failed inserting, invalid schema " + name);
                            context.response().setStatusCode(404).end();
                        }
                        else {
                            dbService.insertClass(name, context.getBodyAsJson(), reply -> {
                                if (reply.succeeded()) {
                                    LOGGER.info("Inserted " + name);
                                    context.response().setStatusCode(201).end();
                                }
                                else {
                                    context.response().setStatusCode(404).end();
                                }
                            });
                        }
                    }
                    else if (isClass == false) {
                        try {
                            isValidSchema = propertyValidator.validate(body);
                        }
                        catch (Exception e) {
                            isValidSchema = false;
                        }
                        if (isValidSchema == false) {
                            LOGGER.info("Failed inserting, invalid schema " + name);
                            context.response().setStatusCode(404).end();
                        }
                        else {
                            dbService.insertProperty(name, context.getBodyAsJson(), reply -> {
                                if (reply.succeeded()) {
                                    LOGGER.info("Insertion success");
                                    context.response().setStatusCode(201).end();
                                }
                                else {
                                    context.response().setStatusCode(404).end();
                                }
                            });
                        }
                    }
                }
                if (authreply.failed()) {
                    LOGGER.info("Got invalid usename and password");
                    context.response().setStatusCode(401).end();
                }
        });
    }
}
