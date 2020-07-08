package iudx.vocserver;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


public class MainVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> promise) throws Exception {
    
    /** Deploy DBVerticle */
    Promise<String> dbVerticleDeployment = Promise.promise();
    vertx.deployVerticle("iudx.vocserver.database.DBVerticle",
                new DeploymentOptions()
                  .setConfig(config()),
              dbVerticleDeployment);
    
    /**Deploy SearchVerticle */
    Promise<String> searchVerticleDeployment = Promise.promise();
    vertx.deployVerticle("iudx.vocserver.search.SearchVerticle",
                new DeploymentOptions()
                  .setConfig(config()),
              searchVerticleDeployment);
    

    /** Compose-deploy HTTP Verticle */
    dbVerticleDeployment.future()
      .compose(id -> {
        Promise<String> httpVerticleDeployment = Promise.promise();
        vertx.deployVerticle(
            "iudx.vocserver.http.HttpServerVerticle",
            new DeploymentOptions()
              .setInstances(config().getInteger("vocserver.http.instances"))
              .setConfig(config()),
            httpVerticleDeployment);
        return httpVerticleDeployment.future();
      })
      .compose(id -> {
        Promise<String> authVerticleDeployment = Promise.promise();
        vertx.deployVerticle(
            "iudx.vocserver.auth.AuthVerticle",
            new DeploymentOptions()
              .setInstances(config().getInteger("vocserver.auth.instances"))
              .setConfig(config()),
            authVerticleDeployment);
        return authVerticleDeployment.future();
      })
      .setHandler(ar -> {
        if (ar.succeeded()) {
          promise.complete();
        } else {
          promise.fail(ar.cause());
        }
      });
  }
}
