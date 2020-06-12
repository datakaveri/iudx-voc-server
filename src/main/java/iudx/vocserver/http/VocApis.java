package iudx.vocserver.http;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.VertxException;
import io.vertx.core.json.JsonObject;

import iudx.vocserver.database.DBService;
import iudx.vocserver.auth.AuthService;
import iudx.vocserver.utils.Validator;
import iudx.vocserver.utils.Proc;


interface VocApisInterface {
    void getClassesHandler(RoutingContext context);
    void getPropertiesHandler(RoutingContext context);
    void getMasterHandler(RoutingContext context);
    void searchHandler(RoutingContext context);
    void getSchemaHandler(RoutingContext context);
    void insertMasterHandler(RoutingContext context);
    void insertSchemaHandler(RoutingContext context);
    void deleteMasterHandler(RoutingContext context);
    void deleteSchemaHandler(RoutingContext context);
    void webhookHandler(RoutingContext context);
    
}


public final class VocApis implements VocApisInterface {

    // iudx-voc-server DBService
    private DBService dbService;
    // iudx-voc-server AuthService

    // Validator objects
    private boolean isValidSchema;
    private Validator masterValidator;
    private Validator classValidator;
    private Validator propertyValidator;

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

    private static String VOC_REPO = "iudx-voc/";
    private static String UPDATE_REPO_CMD = "nohup git pull origin master &";
    private static String PUSH_SCHEMAS_CMD = "nohup python3 utils/push/hookTriggeredInsert.py &";

    /**
     * VocApis constructor
     *
     * @param DBService DataBase Service class
     * @return void
     * @TODO Throw error if load failed
     */
    public VocApis(DBService dbService) {
        this.dbService = dbService;

        try {
            // Loads from resources folder
            masterValidator = new Validator("/masterSchema.json");
            classValidator = new Validator("/classSchema.json");
            propertyValidator = new Validator("/propertySchema.json");
        } catch (Exception e) {
        }
    }

    /**
     * Webhook trigger to reload classes
     *
     * @param context {@link RoutingContext}
     * @return void
     * @TODO Throw error if load failed
     */
    // tag::db-service-calls[]
    public void webhookHandler(RoutingContext context) {
        LOGGER.info("Received webhook trigger ");
        dbService.clearDB(reply -> {
            if (reply.failed()) {
                context.response()
                    .putHeader("content-type", "application/json")
                    .setStatusCode(404)
                    .end();
            }
        });
        Proc.execCommand("cd " + VOC_REPO + " && " + UPDATE_REPO_CMD);
        Proc.execCommand("cd " + VOC_REPO + " && " + PUSH_SCHEMAS_CMD);
        context.response()
            .putHeader("content-type", "application/json")
            .setStatusCode(200)
            .end();
    }

    /**
     * Get all classes
     *
     * @param context {@link RoutingContext}
     * @return void
     * @TODO Throw error if load failed
     */
    // tag::db-service-calls[]
    public void getClassesHandler(RoutingContext context) {
            dbService.getAllClasses(reply -> {
                if (reply.succeeded()) {
                    context.response()
                    .putHeader("content-type", "application/json")
                    .setStatusCode(200)
                    .end(reply.result().encode());
                }
                else {
                    context.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(404)
                        .end();
                }
            });
    }

    /**
     * Get all properties
     *
     * @param context {@link RoutingContext}
     * @return void
     * @TODO Throw error if load failed
     */
    // tag::db-service-calls[]
    public void getPropertiesHandler(RoutingContext context) {
            dbService.getAllProperties(reply -> {
                if (reply.succeeded()) {
                    context.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(reply.result().encode());
                }
                else {
                    context.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(404)
                        .end();
                }
            });
    }


    /**
     * Get the master context
     *
     * @param context {@link RoutingContext}
     * @return void
     * @TODO Throw error if load failed
     */
    // tag::db-service-calls[]
    public void getMasterHandler(RoutingContext context) {
        dbService.getMasterContext(reply -> {
            if (reply.succeeded()) {
                context.response().putHeader("content-type", "application/json")
                .setStatusCode(200)
                .end(reply.result().encode());
            } else {
                LOGGER.info("Failed getting master context");
                context.response()
                    .putHeader("content-type", "application/json")
                    .setStatusCode(404)
                    .end();
            }
        });
    }

    /**
     * Search for schemas
     *
     * @param context {@link RoutingContext}
     * @return void
     * @TODO Throw error if load failed
     */
    // tag::db-service-calls[]
    public void searchHandler(RoutingContext context) {
        String pattern = "";
        String subClassOf = "";
        String dataModelDomain = "";
        try {
            if (context.queryParams().contains("q")) {
                pattern = context.queryParams().get("q");
            }
            if (context.queryParams().contains("subClassOf")) {
                subClassOf = context.queryParams().get("subClassOf");
            }
            if (context.queryParams().contains("dataModelDomain")) {
                dataModelDomain = context.queryParams().get("dataModelDomain");
            }
            if ((pattern.length() == 0)
                    && (subClassOf.length() == 0)
                    && (dataModelDomain.length() == 0) ) {
                context.response().setStatusCode(200).end();
                return;
            }
        } catch (Exception e) {
            context.response().setStatusCode(404).end();
            return;
        }
        JsonObject queryObj = new JsonObject().put("pattern", pattern)
                                                .put("subClassOf", subClassOf)
                                                .put("dataModelDomain", dataModelDomain);
        dbService.fuzzySearch(pattern, reply -> {
            if (reply.succeeded()) {
                context.response().putHeader("content-type", "application/json")
                .setStatusCode(200)
                .end(reply.result().encode());
            } else {
                LOGGER.info("Failed searching, query params not found");
                context.response()
                    .putHeader("content-type", "application/json")
                    .setStatusCode(404)
                    .end();
            }
        });
    }

    
    /**
     * Get a particular schema from a name
     *
     * @param context {@link RoutingContext}
     * @return void
     * @TODO Throw error if load failed
     */
    // tag::db-service-calls[]
    public void getSchemaHandler(RoutingContext context) {
        String name = context.request().getParam("name").replace(".jsonld", "");
        /** Check if provided param is class or property */
        boolean isClass = Character.isUpperCase(name.charAt(0));
        /** This can be simplified by setting a flag, leaving it expanded for future use. */
        if (isClass == true) {
            dbService.getClass(name, reply -> {
                if (reply.succeeded()) {
                    context.response().putHeader("content-type", "application/json")
                    .setStatusCode(200)
                                    .end(reply.result().encode());
                } else {
                    LOGGER.info("Failed getting class " + name);
                    context.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(404)
                        .end();
                }
            });
        } else if (isClass == false) {
            dbService.getProperty(name, reply -> {
                if (reply.succeeded()) {
                    context.response().putHeader("content-type", "application/json")
                    .setStatusCode(200)
                    .end(reply.result().encode());
                } else {
                    context.response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(404)
                        .end();
                }
            });
        }
    }

    /**
     * Insert the master schema
     *
     * @param context {@link RoutingContext}
     * @return void
     * @TODO Throw error if load failed
     * @TODO Automatically generate master context
     */
    // tag::db-service-calls[]
    public void insertMasterHandler(RoutingContext context) {
        String body = context.getBodyAsString();
        /** This can be simplified by setting a flag, leaving it expanded for future use. */
        context.response().putHeader("content-type", "application/json");
        try {
            isValidSchema = masterValidator.validate(body);
        }
        catch (Exception e) {
            isValidSchema = false;
            LOGGER.info(e);
        }
        if (isValidSchema == false) {
            LOGGER.info("Failed inserting master context, invalid schema ");
            context.response().setStatusCode(404).end();
        } else {
            dbService.insertMasterContext(context.getBodyAsJson(), reply -> {
                if (reply.succeeded()) {
                    LOGGER.info("Inserted master");
                    context.response().setStatusCode(201).end();
                } else {
                    context.response().setStatusCode(404).end();
                }
            });
        }
    }

    /**
     * Insert a property or a class
     *
     * @param context {@link RoutingContext}
     * @return void
     * @TODO Throw error if load failed
     * @TODO Automatically generate master context
     */
    // tag::db-service-calls[]
    public void insertSchemaHandler(RoutingContext context) {
        String name = context.request().getParam("name");
        String body = context.getBodyAsString();
        /** Check if provided param is class or property */
        boolean isClass = Character.isUpperCase(name.charAt(0));
        /** This can be simplified by setting a flag, leaving it expanded for future use. */
        context.response().putHeader("content-type", "application/json");
        if (isClass == true) {
            try {
                isValidSchema = classValidator.validate(body);
            }
            catch (Exception e) {
                isValidSchema = false;
            }
            if (isValidSchema == false) {
                LOGGER.info("Failed inserting, invalid schema " + name);
                context.response().setStatusCode(404).end();
            } else {
                dbService.insertClass(name, context.getBodyAsJson(), reply -> {
                    if (reply.succeeded()) {
                        LOGGER.info("Inserted " + name);
                        // TODO: Very inefficient. Consider making a service that 
                        //          inserts label and summary
                        dbService.makeSummary( res -> {} );
                        context.response().setStatusCode(201).end();
                    } else {
                        context.response().setStatusCode(404).end();
                    }
                });
            }
        } else if (isClass == false) {
            try {
                isValidSchema = propertyValidator.validate(body);
            }
            catch (Exception e) {
                isValidSchema = false;
            }
            if (isValidSchema == false) {
                LOGGER.info("Failed inserting, invalid schema " + name);
                context.response().setStatusCode(404).end();
            } else {
                dbService.insertProperty(name, context.getBodyAsJson(), reply -> {
                    if (reply.succeeded()) {
                        LOGGER.info("Inserted " + name);
                        context.response().setStatusCode(201).end();
                    } else {
                        context.response().setStatusCode(404).end();
                    }
                });
            }
        }
    }

    /**
     * Delete the master schema
     *
     * @param context {@link RoutingContext}
     * @return void
     * @TODO Throw error if load failed
     * @TODO Automatically generate master context
     */
    // tag::db-service-calls[]
    public void deleteMasterHandler(RoutingContext context) {
        context.response().putHeader("content-type", "application/json");
        dbService.deleteMaster(reply -> {
            if (reply.succeeded()) {
                LOGGER.info("Deleted master");
                context.response().setStatusCode(204).end();
            } else {
                context.response().setStatusCode(404).end();
            }
        });
    }


    /**
     * Delete a class or property
     *
     * @param context {@link RoutingContext}
     * @return void
     * @TODO Throw error if load failed
     * @TODO Automatically generate master context
     */
    // tag::db-service-calls[]
    public void deleteSchemaHandler(RoutingContext context) {
        String name = context.request().getParam("name");
        /** Check if provided param is class or property */
        boolean isClass = Character.isUpperCase(name.charAt(0));
        /** This can be simplified by setting a flag, leaving it expanded for future use. */
        context.response().putHeader("content-type", "application/json");
        if (isClass == true) {
            dbService.deleteClass(name, reply -> {
                if (reply.succeeded()) {
                    LOGGER.info("Deleted " + name);
                    // TODO: Very inefficient. Consider making a service that 
                    //          inserts label and summary
                    dbService.makeSummary( res -> {} );
                    context.response().setStatusCode(204).end();
                } else {
                    context.response().setStatusCode(404).end();
                }
            });
        } else if (isClass == false) {
            dbService.deleteProperty(name, reply -> {
                if (reply.succeeded()) {
                    LOGGER.info("Deleted " + name);
                    context.response().setStatusCode(204).end();
                } else {
                    context.response().setStatusCode(404).end();
                }
            });
        }
    }
}
