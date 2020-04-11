package iudx.vocserver;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import iudx.vocserver.database.DBVerticle;

public class MainVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Promise<Void> promise) throws Exception {

        Promise<String> dbVerticleDeployment = Promise.promise();
        vertx.deployVerticle(new DBVerticle(), dbVerticleDeployment);

        dbVerticleDeployment.future().compose(id -> {

            Promise<String> httpVerticleDeployment = Promise.promise();
            vertx.deployVerticle(
                    "iudx.vocserver.http.HttpServerVerticle",
                    new DeploymentOptions().setInstances(2),
                    httpVerticleDeployment);

            LOGGER.info("Started main and http vericles");
            return httpVerticleDeployment.future();

        }).setHandler(ar -> {
            if (ar.succeeded()) {
                promise.complete();
            } else {
                promise.fail(ar.cause());
            }
        });
    }

}
