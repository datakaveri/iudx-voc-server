package iudx.vocserver.http;

import iudx.vocserver.http.VocApis;
import iudx.vocserver.database.*;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.vertx.core.json.JsonObject;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.concurrent.CountDownLatch;
import io.vertx.core.file.FileSystem;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.http.HttpServerResponse;


import static org.mockito.Mockito.*;
import org.mockito.stubbing.Answer;


@RunWith(VertxUnitRunner.class)
public class HttpTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class.getName());


    // Mock object paths
    private static final String ALL_CLASSES_PATH = "src/main/resources/iudx/vocserver/http/allClassesMockResponse.json";

    private Vertx vertx;

    private VocApisInterface vocApis;
    DBService dbService = mock(DBService.class);


    @Before
    public void setUp(TestContext tc) {
        vertx = Vertx.vertx();

        // Initialize mock objects
        initializeMock();

    }

    public void initializeMock() {

        FileSystem fileSystem = vertx.fileSystem();


        // All Classes
        JsonArray respBody = new JsonArray(fileSystem.readFileBlocking(ALL_CLASSES_PATH));
        AsyncResult<JsonArray> asyncResult = mock(AsyncResult.class);

        when(asyncResult.succeeded()).thenReturn(true);
        when(asyncResult.result()).thenReturn(respBody);

        doAnswer((Answer<AsyncResult<JsonArray>>) arguments -> {
            ((Handler<AsyncResult<JsonArray>>) arguments.getArgument(0))
                .handle(asyncResult);
            return null;
        }).when(dbService).getAllClasses(any());

        vocApis = new VocApis(dbService);
                

    }

    @After
    public void tearDown(TestContext tc) {
        vertx.close(tc.asyncAssertSuccess());
    }


    @Test
    public void testGetAllClasses(TestContext tc) {

        // dbService.getAllClasses( res -> {
        //     LOGGER.info(res.result());
        // });


        RoutingContext context = mock(RoutingContext.class);
        HttpServerResponse response = mock(HttpServerResponse.class);

        vocApis.getClassesHandler(context);

        LOGGER.info(context.response());

    }
}
