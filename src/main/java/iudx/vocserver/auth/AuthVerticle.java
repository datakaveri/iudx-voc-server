package iudx.vocserver.auth;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

// tag::authverticle[]
public class AuthVerticle extends AbstractVerticle {

    public static final String CONFIG_AUTH_QUEUE = "vocserver.auth.queue";
    private static final String SERVER_UNAME = "vocserver.auth.username";
    private static final String SERVER_PASSWD = "vocserver.auth.password";
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthVerticle.class);

    @Override
    public void start(Promise<Void> promise) throws Exception {

        JsonObject credentials = new JsonObject()
                                        .put("username", config().getString(SERVER_UNAME))
                                        .put("password", config().getString(SERVER_PASSWD));

        AuthService.create(credentials,
            ready -> {
                if (ready.succeeded()) {
                    ServiceBinder binder = new ServiceBinder(vertx);
                    binder
                        .setAddress(CONFIG_AUTH_QUEUE)
                        .register(AuthService.class, ready.result());
                    promise.complete();
                } else {
                    LOGGER.info("Promise Failed");
                    promise.fail(ready.cause());
                }
            });
    }
}
// end::authverticle[]
