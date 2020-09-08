package iudx.vocserver.auth;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.core.net.JksOptions;


import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

// tag::authverticle[]
public class AuthVerticle extends AbstractVerticle {
  public static final String CONFIG_AUTH_QUEUE = "vocserver.auth.queue";
  private static final String AUTH_KEYSTORE_PATH = "authserver.jksfile";
  private static final String AUTH_TYPE = "authserver.type";
  private static final String AUTH_KEYSTORE_PASSWORD = "authserver.jkspasswd";
  private static final String AUTH_LOCAL_USERNAME = "authserver.localuser";
  private static final String AUTH_LOCAL_PASSWORD = "authserver.localpassword";
  private static final String AUTH_URL = "authserver.url";
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthVerticle.class);


  @Override
  public void start(Promise<Void> promise) throws Exception {


    WebClientOptions options = new WebClientOptions()
                     .setSsl(true)
                     .setKeyStoreOptions(new JksOptions()
                         .setPath(config().getString(AUTH_KEYSTORE_PATH))
                         .setPassword(config()
                           .getString(AUTH_KEYSTORE_PASSWORD)));

    WebClient client = WebClient.create(vertx, options);

    JsonObject authConfig = new JsonObject();

    String authType = config().getString(AUTH_TYPE);
    if (authType.equals("localauth")) {
      authConfig.put("authType", "localauth");
      authConfig.put("password", config().getString(AUTH_KEYSTORE_PASSWORD));
    } else {
      authConfig.put("authType", "iudxauth");
      authConfig.put("url", config().getString(AUTH_URL));
    }


    AuthService.create(client, authConfig,
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
