/*
* Copyright 2014 Red Hat, Inc.
*
* Red Hat licenses this file to you under the Apache License, version 2.0
* (the "License"); you may not use this file except in compliance with the
* License. You may obtain a copy of the License at:
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/

package iudx.vocserver.database;

import iudx.vocserver.database.DBService;
import io.vertx.core.Vertx;
import io.vertx.core.Handler;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ProxyHandler;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.serviceproxy.ServiceExceptionMessageCodec;
import io.vertx.serviceproxy.HelperUtils;

import iudx.vocserver.database.DBService;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
/*
  Generated Proxy code - DO NOT EDIT
  @author Roger the Robot
*/

@SuppressWarnings({"unchecked", "rawtypes"})
public class DBServiceVertxProxyHandler extends ProxyHandler {

  public static final long DEFAULT_CONNECTION_TIMEOUT = 5 * 60; // 5 minutes 
  private final Vertx vertx;
  private final DBService service;
  private final long timerID;
  private long lastAccessed;
  private final long timeoutSeconds;

  public DBServiceVertxProxyHandler(Vertx vertx, DBService service){
    this(vertx, service, DEFAULT_CONNECTION_TIMEOUT);
  }

  public DBServiceVertxProxyHandler(Vertx vertx, DBService service, long timeoutInSecond){
    this(vertx, service, true, timeoutInSecond);
  }

  public DBServiceVertxProxyHandler(Vertx vertx, DBService service, boolean topLevel, long timeoutSeconds) {
      this.vertx = vertx;
      this.service = service;
      this.timeoutSeconds = timeoutSeconds;
      try {
        this.vertx.eventBus().registerDefaultCodec(ServiceException.class,
            new ServiceExceptionMessageCodec());
      } catch (IllegalStateException ex) {}
      if (timeoutSeconds != -1 && !topLevel) {
        long period = timeoutSeconds * 1000 / 2;
        if (period > 10000) {
          period = 10000;
        }
        this.timerID = vertx.setPeriodic(period, this::checkTimedOut);
      } else {
        this.timerID = -1;
      }
      accessed();
    }


  private void checkTimedOut(long id) {
    long now = System.nanoTime();
    if (now - lastAccessed > timeoutSeconds * 1000000000) {
      close();
    }
  }

    @Override
    public void close() {
      if (timerID != -1) {
        vertx.cancelTimer(timerID);
      }
      super.close();
    }

    private void accessed() {
      this.lastAccessed = System.nanoTime();
    }

  public void handle(Message<JsonObject> msg) {
    try{
      JsonObject json = msg.body();
      String action = msg.headers().get("action");
      if (action == null) throw new IllegalStateException("action not specified");
      accessed();
      switch (action) {
        case "getMasterContext": {
          service.getMasterContext(HelperUtils.createHandler(msg));
          break;
        }
        case "insertMasterContext": {
          service.insertMasterContext((io.vertx.core.json.JsonObject)json.getValue("context"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "getAllProperties": {
          service.getAllProperties(HelperUtils.createHandler(msg));
          break;
        }
        case "getAllClasses": {
          service.getAllClasses(HelperUtils.createHandler(msg));
          break;
        }
        case "getProperty": {
          service.getProperty((java.lang.String)json.getValue("name"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "getClass": {
          service.getClass((java.lang.String)json.getValue("name"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "getExamples": {
          service.getExamples((java.lang.String)json.getValue("name"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "search": {
          service.search((java.lang.String)json.getValue("pattern"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "relationshipSearch": {
          service.relationshipSearch((java.lang.String)json.getValue("key"),
                        (java.lang.String)json.getValue("value"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "makeSummary": {
          service.makeSummary((java.lang.String)json.getValue("name"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "deleteFromSummary": {
          service.deleteFromSummary((java.lang.String)json.getValue("name"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "insertProperty": {
          service.insertProperty((java.lang.String)json.getValue("name"),
                        (io.vertx.core.json.JsonObject)json.getValue("prop"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "insertClass": {
          service.insertClass((java.lang.String)json.getValue("name"),
                        (io.vertx.core.json.JsonObject)json.getValue("cls"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "insertExamples": {
          service.insertExamples((io.vertx.core.json.JsonObject)json.getValue("example"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "deleteMaster": {
          service.deleteMaster(HelperUtils.createHandler(msg));
          break;
        }
        case "deleteProperty": {
          service.deleteProperty((java.lang.String)json.getValue("name"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "deleteClass": {
          service.deleteClass((java.lang.String)json.getValue("name"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "deleteExamples": {
          service.deleteExamples((java.lang.String)json.getValue("name"),
                        HelperUtils.createHandler(msg));
          break;
        }
        case "clearDB": {
          service.clearDB(HelperUtils.createHandler(msg));
          break;
        }
        default: throw new IllegalStateException("Invalid action: " + action);
      }
    } catch (Throwable t) {
      msg.reply(new ServiceException(500, t.getMessage()));
      throw t;
    }
  }
}