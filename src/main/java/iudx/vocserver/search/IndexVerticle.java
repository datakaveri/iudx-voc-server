package iudx.vocserver.search;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class IndexVerticle extends AbstractVerticle {
    
    public static final String CONFIG_SEARCH_QUEUE = "vocserver.search.queue";
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexVerticle.class);

    @Override
    public void start(Promise<Void> promise) throws Exception {
        WebClient indexClient = WebClient.create(vertx, new WebClientOptions()
                                                            .setSsl(false));

        IndexService.create(indexClient, ready->{
                if(ready.succeeded()) {
                    ServiceBinder binder = new ServiceBinder(vertx);
                    binder
                        .setAddress(CONFIG_SEARCH_QUEUE)
                        .register(IndexService.class, ready.result());
                    LOGGER.info("Index service deployed");
                    promise.complete();
                }
                else {
                    LOGGER.info("Promise Failed");
                    promise.fail(ready.cause());
                }
            });
        }
}

