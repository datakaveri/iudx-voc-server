package iudx.vocserver.auth;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Timer; 
import java.util.TimerTask; 

public class AuthCache {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthCache.class);

  public String token;
  public int statusCode;
  public JsonObject body;

  Timer t = new Timer();  
  TimerTask task = new TimerTask() {  
    @Override  
    public void run() {  
        LOGGER.info("Timer ran");
        token = "";
        statusCode = -1;
        body.clear();  
    };  
  };  
  public void startTimer() {
    long delay = 600000L;
    t.schedule(task, delay);
  }
}