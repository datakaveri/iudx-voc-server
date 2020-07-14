package iudx.vocserver.auth;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class AuthCache {

    public String token;
    public int statusCode;
    public JsonObject body;

    private Vertx vertx = Vertx.vertx();

    public void startTimer() {
       long timerID = vertx.setTimer(600000, id -> {
            token = "";
            statusCode = -1;
            body.clear();
        });
    }
}