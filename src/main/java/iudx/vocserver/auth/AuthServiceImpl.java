/**
 * <h1>AuthServiceImpl.java</h1>
 * Service Implementations for the AuthService
 */

package iudx.vocserver.auth;
import java.util.regex.Pattern;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.codec.BodyCodec;
import java.lang.Throwable;



class AuthServiceImpl implements AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);
    private WebClient client;
    private String url;


    AuthServiceImpl(WebClient client, String url, Handler<AsyncResult<AuthService>> readyHandler) {
        this.client = client;
        this.url = url;
        readyHandler.handle(Future.succeededFuture(this));
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public AuthService validateToken(String token, String serverId,
            Handler<AsyncResult<Boolean>> resultHandler) {

        client
            .post(443, url, "/auth/v1/token/introspect")
            .ssl(true)
            .putHeader("content-type", "application/json")
            .sendJsonObject(new JsonObject().put("token", token),
                    ar -> {
                        if (ar.succeeded()) {
                            if (ar.result().statusCode() == 200) {
                                JsonArray validPatterns = ar.result().bodyAsJsonObject()
                                                            .getJsonArray("request");
                                LOGGER.info("Got valid ids " + validPatterns.encode());
                                int validToken = 0;
                                for (int i = 0; i<validPatterns.size(); i++) {
                                    Pattern patObj = Pattern.compile(validPatterns
                                                                    .getJsonObject(i)
                                                                    .getString("id")
                                                                    .split("/")[2]
                                                                    .replace("/", "\\/")
                                                                    .replace(".", "\\.")
                                                                    .replace("*", ".*"));

                                    if (patObj.matcher(serverId).matches()) validToken = 1;
                                }
                                if (validToken == 1 ){
                                    LOGGER.info("Obtained valid token");
                                    resultHandler.handle(Future.succeededFuture(true));
                                } else {
                                    resultHandler.handle(Future.failedFuture(new Throwable("Invalid token")));
                                }
                            } else {
                                resultHandler.handle(Future.failedFuture(ar.cause()));
                            }
                        }
                    });
        return this;
            }
}
