package iudx.vocserver.http;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.IOException;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.WebClientOptions;
import java.util.concurrent.CountDownLatch;
import io.vertx.core.json.JsonArray;
import java.util.concurrent.TimeUnit;
import io.vertx.ext.web.codec.BodyCodec;

@RunWith(VertxUnitRunner.class)
public class HttpServerTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerTest.class.getName());

  private Vertx vertx;
  private WebClient client;

  
  // tag::setUp[]
  @Before
  public void setUp(TestContext context) throws IOException {
    vertx = Vertx.vertx();

    JsonObject conf = new JsonObject()
      .put("authserver.jksfile", "config/authkeystore_example.jks")
      .put("authserver.type","localauth")
      .put("authserver.jkspasswd", "1!Rbccps-voc@123")
      .put("authserver.url", "auth.iudx.org.in")
      .put("vocserver.jksfile","config/iudxkeystore.jks")
      .put("vocserver.http.instances",1)
      .put("vocserver.jkspasswd","1!Rbccps-voc@123")
      .put("vocserver.http.port",8080)
      .put("vocserver.id","org/sha1/example.com/")
      .put("vocserver.database.queue","vocserver.database.queue")
      .put("vocserver.database.url", "mongodb://localhost:27017")
      .put("vocserver.database.username","abc")
      .put("vocserver.database.password","123")
      .put("vocserver.database.name", "voc")
      .put("vocserver.database.poolname", "mongo_pool")
      .put("vocserver.auth.instances", 2)
      .put("vocserver.auth.queue","vocserver.auth.queue")
      .put("vocserver.testing",true);


    CountDownLatch latch = new CountDownLatch(1);
    WebClientOptions searchClientOptions = new WebClientOptions()
                                              .setSsl(false);

    client = WebClient.create(vertx, searchClientOptions);

    DeploymentOptions options = new DeploymentOptions()
                                    .setConfig(conf)
                                    .setInstances(conf.getInteger("vocserver.http.instances"));
                                    
    vertx.deployVerticle(HttpServerVerticle.class.getName(), options, ar -> {
      if (ar.succeeded()) {
            LOGGER.info("Http Server Verticle Launched");
            vertx.setTimer(5000, id -> {
              context.async().countDown();
              latch.countDown();
              context.async().complete();
            });
        }
    });
    try {
        latch.await();
    } catch (Exception e) {
        LOGGER.info("Failed");
    }  
  }
  // end::setUp[]

  // tag::tearDown[]
  @After
  public void finish(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }
  // end::tearDown[]

  //tag::testFuzzySearchOk[]
  @Test
  public void testFuzzySearchOk(TestContext context) {
    Async async = context.async();
    client.get(8080, "localhost", "/fuzzysearch?q=resource")
          .putHeader("content-type", "application/json")
          .putHeader("Accept", "application/json")
          .as(BodyCodec.jsonObject())
          .send(ar -> {
            HttpResponse<JsonObject> response = ar.result();
            context.assertEquals(response.statusCode(), 200);
            context.assertEquals(response.headers().get("content-type"),"application/json");
            context.assertNotEquals(response.body().getValue("nbHits"),0);
          async.complete(); 
      });
    }
  //end:testFuzzySearchOk[]

  //tag::testFuzzySearchErrors[]
  @Test
  public void testFuzzySearchEmpty(TestContext context) {
    Async async = context.async();
    WebClient client = WebClient.create(vertx);
    client.get(8080, "localhost", "/fuzzysearch?q=")
          .putHeader("content-type", "application/json")
          .putHeader("Accept", "application/json")
          .as(BodyCodec.jsonObject())
          .send(ar -> {
            LOGGER.info(ar.result().statusCode());
            context.assertEquals(ar.result().statusCode(), 404);
          async.complete(); 
    });
  }

  @Test
  public void testFuzzySearchBadQuery(TestContext context) {
    Async async = context.async();
    WebClient client = WebClient.create(vertx);
    client.get(8080, "localhost", "/fuzzysearch?q=wfq")
          .putHeader("content-type", "application/json")
          .putHeader("Accept", "application/json")
          .as(BodyCodec.jsonObject())
          .send(ar -> {
            HttpResponse<JsonObject> response = ar.result();
            context.assertEquals(response.statusCode(), 200);
            context.assertEquals(response.headers().get("content-type"),"application/json");
            context.assertEquals(response.body().getValue("nbHits"),0);
          async.complete(); 
    });
  }
  //end::testFuzzySearchErrors[]
}