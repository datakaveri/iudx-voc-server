package iudx.vocserver.utils;

import iudx.vocserver.utils.Proc;

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
public class ProcTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcTest.class.getName());

    @Before
    public void setUp(TestContext tc) {
    }

    @After
    public void tearDown(TestContext tc) {
    }


    @Test
    public void testProc(TestContext tc) {
        int statusCode = Proc.execCommand("cd iudx-voc && git pull origin master");
        LOGGER.info(statusCode);

    }
}
