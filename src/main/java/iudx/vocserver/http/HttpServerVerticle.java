/**
* <h1>HttpServerVerticle.java</h1>
* HTTP Server Verticle
*/

package iudx.vocserver.http;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import java.security.MessageDigest;

import org.apache.commons.codec.digest.HmacUtils;

import java.util.Set;
import java.util.HashSet;

import iudx.vocserver.database.DBService;
import iudx.vocserver.auth.AuthService;
import iudx.vocserver.search.SearchService;

public class HttpServerVerticle extends AbstractVerticle {
  /**
   * HttpServerVerticle Class
   * @param dbClient MongoDB Client
   * @param readyHandler Async query result handler. Returns query results as JSONArray
   */

  // Config variables
  //@TODO: Config variables should be passed from config file
  public static final String CONFIG_SERVER_ID = "vocserver.id";
  public static final String CONFIG_HTTP_SERVER_PORT = "vocserver.http.port";
  public static final String CONFIG_DB_QUEUE = "vocserver.database.queue";
  public static final String CONFIG_AUTH_QUEUE = "vocserver.auth.queue";
  public static final String WEBHOOK_PASSWD = "vocserver.webhookpasswd";
  public static final String CONFIG_SEARCH_QUEUE = "vocserver.search.queue";
  public static final String VOC_REPO_NAME = "vocserver.reponame";
  // Default logger
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  // iudx-voc-server DBService
  private DBService dbService;
  // iudx-voc-server AuthService
  private AuthService authService;
  private String serverId ;
  private SearchService searchService;
  private String vocRepo;
  // APIS
  private VocApisInterface vocApis;

  /**
   * AbstractVerticle start
   * */
  @Override
  public void start(Promise<Void> promise) throws Exception {

    String dbQueue = config().getString(CONFIG_DB_QUEUE);
    String authQueue = config().getString(CONFIG_AUTH_QUEUE);
    serverId = config().getString(CONFIG_SERVER_ID);
    vocRepo = config().getString(VOC_REPO_NAME);

    searchService = SearchService.createProxy(vertx, CONFIG_SEARCH_QUEUE);
    dbService = DBService.createProxy(vertx, dbQueue);
    authService = AuthService.createProxy(vertx, authQueue);

    String webhookPasswd = config().getString(WEBHOOK_PASSWD);

    HttpServerOptions options = new HttpServerOptions()
            .setCompressionSupported(true)
            .setCompressionLevel(5)
            .setSsl(false);
    HttpServer server = vertx.createHttpServer(options);

    /** Load the APIs class */
    vocApis = new VocApis(dbService, searchService, vocRepo);

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
    router.route()
      .handler(CorsHandler.create("*")
        .allowedHeaders(allowedHeaders)
        .allowedMethods(allowedMethods));

    /** UI
    *  Notes: This is the first registered route to prevent conflict with json-ld response
    *  @TODO: Handle failures while sending file, spacing needs to be fixed.
    * */
    router.getWithRegex("^\\/(?!assets\\/)(?!static\\/)[A-Za-z0-9_]+")
      .produces("text/html")
      .handler(
        routingContext -> {
        HttpServerResponse response = routingContext.response();
        response.sendFile("iudx-voc-ui/dist/dk-voc-ui/index.html");
    });

    router.route("/static/*").produces("text/html")
      .handler(StaticHandler.create("iudx-voc-ui/dist/dk-voc-ui/"));

    router.route("/assets/*").produces("*/*")
      .handler(StaticHandler.create("iudx-voc-ui/dist/dk-voc-ui/assets/"));

    router.route("/").produces("text/html")
      .handler(routingContext -> {
      HttpServerResponse response = routingContext.response();
      response.sendFile("iudx-voc-ui/dist/dk-voc-ui/index.html");
    });

    /** Get/Post master context 
    */
    router.get("/").produces("application/ld+json")
      .handler( routingContext -> {
      vocApis.getMasterHandler(routingContext);
    });

    router.getWithRegex("\\/master.jsonld")
      .handler( routingContext -> {
      vocApis.getMasterHandler(routingContext);
    });

    router.route("/").consumes("application/ld+json").produces("application/ld+json")
      .handler(BodyHandler.create());

    //@TODO: Log Failure
    router.post("/").consumes("application/ld+json").produces("application/ld+json")
      .handler( routingContext -> {
      String token = routingContext.request().getHeader("token");
      authService.validateToken(token, serverId, authReply -> {
        if (authReply.succeeded()) {
        vocApis.insertMasterHandler(routingContext);
        } else {
          routingContext.response()
          .putHeader("content-type", "application/json")
          .setStatusCode(401)
          .end();
        }
      });
    });

    //@TODO: Log Failure
    router.delete("/").consumes("application/ld+json").produces("application/ld+json")
      .handler( routingContext -> {
      String token = routingContext.request().getHeader("token");
      authService.validateToken(token, serverId, authReply -> {
        if (authReply.succeeded()) {
          vocApis.deleteMasterHandler(routingContext);
        } else {
          routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(401)
            .end();
        }

      });
    });

    /** Simple Search 
    */
    router.get("/search").consumes("application/json")
      .produces("application/json")
      .handler( routingContext -> {
      vocApis.searchHandler(routingContext);
    });

    /**Fuzzy Search
    */
    router.get("/fuzzysearch").consumes("application/json")
      .produces("application/json")
      .handler( routingContext -> {
      vocApis.fuzzySearchHandler(routingContext);
    });

    /** Relationship Search 
    */
    router.get("/relationship").consumes("application/json")
      .produces("application/json")
      .handler( routingContext -> {
      vocApis.relationshipSearchHandler(routingContext);
    });
    
    /**
    * GET/POST examples by type
    * @TODO: Auth failure needs to be logged
    */

    router.route("/examples/:name").consumes("application/ld+json")
      .handler(BodyHandler.create());

    router.get("/examples/:name").consumes("application/ld+json")
      .produces("application/ld+json")
      .handler( routingContext -> {
      vocApis.getExampleHandler(routingContext);
    });

    router.post("/examples/:name").consumes("application/ld+json")
      .handler( routingContext -> {
      String token = routingContext.request().getHeader("token");
      authService.validateToken(token, serverId, authReply -> {
        if (authReply.succeeded()) {
        vocApis.insertExampleHandler(routingContext);
        } else {
          routingContext.response()
          .putHeader("content-type", "application/json")
          .setStatusCode(401)
          .end();
        }

      });
    });

    router.delete("/examples/:name").consumes("application/ld+json")
      .handler( routingContext -> {
      String token = routingContext.request().getHeader("token");
      authService.validateToken(token, serverId, authReply -> {
        if (authReply.succeeded()) {
        vocApis.deleteExampleHandler(routingContext);
        } else {
          routingContext.response()
          .putHeader("content-type", "application/json")
          .setStatusCode(401)
          .end();
        }

      });
    });

    /** GET/POST for Data Descriptor
    */
    router.route("/descriptor/:name").consumes("application/ld+json")
      .handler(BodyHandler.create());

    router.get("/descriptor/:name").consumes("application/ld+json")
      .produces("application/ld+json")
      .handler( routingContext -> {
        vocApis.getDescriptorHandler(routingContext);
      });
    
    router.post("/descriptor/:name").consumes("application/ld+json")
    .handler( routingContext -> {
      String token = routingContext.request().getHeader("token");
      authService.validateToken(token, serverId, authReply -> {
        if (authReply.succeeded()) {
          vocApis.insertDescriptorHandler(routingContext);
        } else {
          routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(401)
            .end();
        }

      });
    });

    router.get("/list/descriptors").consumes("application/json")
    .produces("application/json")
    .handler(routingContext -> {
      vocApis.listDescriptorHandler(routingContext);
    });

    /** Get/Post classes or properties by name (JSON-LD API) 
      * @TODO: Auth failure needs to be logged
    **/
    router.get("/:name").consumes("application/ld+json")
      .produces("application/ld+json")
      .handler( routingContext -> {
      vocApis.getSchemaHandler(routingContext);
    });


    router.route("/:name").consumes("application/ld+json")
      .handler(BodyHandler.create());

    router.post("/:name").consumes("application/ld+json")
      .handler( routingContext -> {
      String token = routingContext.request().getHeader("token");
      authService.validateToken(token, serverId, authReply -> {
        if (authReply.succeeded()) {
          vocApis.insertSchemaHandler(routingContext);
        } else {
          routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(401)
            .end();
        }
      });
    });

    router.delete("/:name").consumes("application/ld+json")
      .handler( routingContext -> {
      String token = routingContext.request().getHeader("token");
      authService.validateToken(token, serverId, authReply -> {
        if (authReply.succeeded()) {
          vocApis.deleteSchemaHandler(routingContext);
        }  else {
          routingContext.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(401)
            .end();
        }
      });
    });
    
    /** Get jsonld from browser */
    router.getWithRegex("\\/(?<name>[^\\/]+)\\.jsonld")
      .handler( routingContext -> {
      vocApis.getSchemaHandler(routingContext);
    });

    /** Get all classes  and properties
    */
    router.get("/classes").consumes("application/json")
      .produces("application/json")
      .handler( routingContext -> {
      vocApis.getClassesHandler(routingContext);
    });

    router.get("/properties").consumes("application/json")
      .produces("application/json")
      .handler( routingContext -> {
      vocApis.getPropertiesHandler(routingContext);
    });

    /**  Webhook trigger
    */
    router.route("/webhook").consumes("application/json")
      .handler(BodyHandler.create());
    router.post("/webhook").consumes("application/json")
      .handler( routingContext -> {
      String gitHmac = routingContext.request().headers().get("X-Hub-Signature");
      String computedHmac =  String.format("sha1=%s",
            HmacUtils.hmacSha1Hex(webhookPasswd,
            routingContext.getBodyAsString()));
      if (!MessageDigest.isEqual(gitHmac.getBytes(), computedHmac.getBytes())) {
        routingContext.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(401)
        .end();
      } else {
        vocApis.webhookHandler(routingContext);
      }
    });

    /**  Descriptor webhook trigger
    */
    router.route("/descriptorhook").consumes("application/json")
      .handler(BodyHandler.create());
    router.post("/descriptorhook").consumes("application/json")
      .handler( routingContext -> {
      String gitHmac = routingContext.request().headers().get("X-Hub-Signature");
      String computedHmac =  String.format("sha1=%s",
            HmacUtils.hmacSha1Hex(webhookPasswd,
            routingContext.getBodyAsString()));
      if (!MessageDigest.isEqual(gitHmac.getBytes(), computedHmac.getBytes())) {
        routingContext.response()
        .putHeader("content-type", "application/json")
        .setStatusCode(401)
        .end();
      } else {
        vocApis.descriptorHookHandler(routingContext);
      }
    });

    //@TODO: Pass port number from config
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
}
