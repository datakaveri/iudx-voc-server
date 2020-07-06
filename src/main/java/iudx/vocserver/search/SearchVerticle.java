package iudx.vocserver.search;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class SearchVerticle extends AbstractVerticle {
    
    //@TODO: pass from config
    public static final String CONFIG_SEARCH_QUEUE = "vocserver.search.queue";
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchVerticle.class);

    @Override
    public void start(Promise<Void> promise) throws Exception {
        WebClient searchClient = WebClient.create(vertx, new WebClientOptions()
                                                            .setSsl(false));

        SearchService.create(searchClient, ready->{
                if(ready.succeeded()) {
                    ServiceBinder binder = new ServiceBinder(vertx);
                    binder
                        .setAddress(CONFIG_SEARCH_QUEUE)
                        .register(SearchService.class, ready.result());
                    LOGGER.info("Search service deployed");
                    promise.complete();
                }
                else {
                    LOGGER.info("Promise Failed");
                    promise.fail(ready.cause());
                }
            });
        }
}

