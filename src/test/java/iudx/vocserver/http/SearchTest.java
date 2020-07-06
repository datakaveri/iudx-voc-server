package iudx.vocserver.http;

//@TODO: Remove unused packages

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

//@TODO: Shift to junit5, add more tests.

@RunWith(VertxUnitRunner.class)
public class SearchTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(SearchTest.class.getName());

  private Vertx vertx;
  private WebClient client;


  
  // tag::setUp[]
  @Before
  public void setUp(TestContext context) throws IOException {
    vertx = Vertx.vertx();


    CountDownLatch latch = new CountDownLatch(1);
    WebClientOptions searchClientOptions = new WebClientOptions().setSsl(false);

    client = WebClient.create(vertx, searchClientOptions);
    context.async().complete();
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
  public void testInsertIndex(TestContext context) {
    Async async = context.async();

    String payload = "[{\"_id\": \"asdfasdfa\",\"comment\":\"Test property ha\",\"label\":\"TestLabel\",\"subClassOf\":\"iudx:StructuredProperty\",\"type\":\"class\"}]";

    // client.get(7700, "localhost", "/indexes/summary/search")
    //         .addQueryParam("q", "geo")
    //         .putHeader("content-type","application/json")
    //         .putHeader("Accept","*/*")
    //         .send( ar -> {
    //           if (ar.succeeded()) {
    //             LOGGER.info(ar.result().body());
    //           } else {
    //           }
    //         });

    client.post(7700, "localhost", "/indexes/summary/documents")
            .putHeader("content-type","application/json")
            .sendJson(new JsonArray(payload), ar -> {
              if (ar.succeeded()) {
                LOGGER.info(ar.result().statusCode());
                LOGGER.info(ar.result().bodyAsString());
              } else {
                LOGGER.info("Failed");
              }
            });


    }
  //end:testFuzzySearchOk[]

}
