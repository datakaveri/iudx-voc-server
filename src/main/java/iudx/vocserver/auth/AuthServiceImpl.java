/**
* <h1>AuthServiceImpl.java</h1>
* Service Implementations for the AuthService
*/

package iudx.vocserver.auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.VertxException;





class AuthServiceImpl implements AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);
    private String username;
    private String password;

    AuthServiceImpl(JsonObject credentials, Handler<AsyncResult<AuthService>> readyHandler) {
        this.username = credentials.getString("username");
        this.password = credentials.getString("password");
        readyHandler.handle(Future.succeededFuture(this));
    }

    /**
     * @{@inheritDoc}
     */
    @Override
    public AuthService validateToken(String username, String password,
                                        Handler<AsyncResult<Boolean>> resultHandler) {

        if (username == null || password == null) {
            resultHandler.handle(Future.failedFuture(new VertxException("Invalid credentials")));
            return this;
        }
        if ((username.equals(this.username))
                && (password.equals(this.password))) {
            resultHandler.handle(Future.succeededFuture(true));
        }
        else {
            resultHandler.handle(Future.failedFuture(new VertxException("Invalid credentials")));
        }
        return this;
    }

}
