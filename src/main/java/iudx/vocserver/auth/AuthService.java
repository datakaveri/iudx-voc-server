/**
* <h1>AuthService.java</h1>
* AuthService interface
*/
package iudx.vocserver.auth;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.mongo.MongoClient;
import java.util.HashMap;

@ProxyGen
@VertxGen
public interface AuthService {
    /**
     * AuthService interface
     */

    /**
     * validateToken - Gets json-ld iudx master context
     * @param token IUDX token
     * @return {@link DBServiceImpl}
     */
    @Fluent
    AuthService validateToken(String uname, String token, Handler<AsyncResult<Boolean>> resultHandler);

    @GenIgnore
    static AuthService create(JsonObject credentials, Handler<AsyncResult<AuthService>> readyHandler) {
        return new AuthServiceImpl(credentials, readyHandler);
    }

    @GenIgnore
    static AuthService createProxy(Vertx vertx, String address) {
        return new AuthServiceVertxEBProxy(vertx, address);
    }
}
