package com.pingidentity.shocard.demo;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import com.pingidentity.did.sdk.client.DistributedIdClient;
import com.pingidentity.did.sdk.client.service.model.ApplicationInstance;
import com.pingidentity.did.sdk.types.Claim;
import com.pingidentity.did.sdk.types.Share;
import com.pingidentity.shocard.demo.config.ServerConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.TemplateEngine;
import spark.template.freemarker.FreeMarkerEngine;


public class ServerMain {

  public static final String APPLICATION_JSON = "application/json";
  protected static final Map<String, Share> SHARE_MAP = new HashMap<>();
  protected static final Map<String, Claim> CLAIM_MAP = new HashMap<>();
  private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
  private static final int PORT = 4567;
  private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(1);

  public static void main(String[] args) {
    staticFiles.location("public");

    port(PORT);

    ServerConfig serverConfig = ConfigFactory.create(ServerConfig.class, System.getenv());

    ApplicationInstance applicationInstance = new ApplicationInstanceUtil(
        serverConfig).getApplicationInstance();

    DistributedIdClient distributedIdClient = new DistributedIdClient.Builder(
        applicationInstance).build();

    applicationInstance.setPushToken(serverConfig.getCallbackURL());
    distributedIdClient.updateApplicationInstance(applicationInstance);

    logger.info("Using ApplicationInstance {}", applicationInstance.getId());

    SessionHandler sessionManager = new SessionHandler();

    DIDMessageHandler didMessageHandler = new DIDMessageHandler(sessionManager,
        applicationInstance);

    // if no callback URL is specified, poll for messages every 2 seconds
    if (serverConfig.getCallbackURL() == null) {
      EXECUTOR.scheduleAtFixedRate(() -> distributedIdClient.processMessages(didMessageHandler), 2,
          2, TimeUnit.SECONDS);
    }

    ReceiveShareHandler receiveShareHandler = new ReceiveShareHandler(sessionManager,
        applicationInstance.getId(), distributedIdClient);

    VerifyShareHandler verifyShareHandler = new VerifyShareHandler(sessionManager,
        applicationInstance.getId(), distributedIdClient);

    TemplateEngine templateEngine = new FreeMarkerEngine();

    get("/status", (request, response) -> "OK");
    get("/", receiveShareHandler, templateEngine);
    get("/sessions/:sessionid", APPLICATION_JSON, sessionManager);
    post("/callback/notify", APPLICATION_JSON,
        (request, response) -> processMessages(distributedIdClient, didMessageHandler));
    get("/verify", verifyShareHandler, templateEngine);
    post("/issue_credential", receiveShareHandler::issueCredential);

  }

  private static Object processMessages(DistributedIdClient client, DIDMessageHandler handler) {
    client.processMessages(handler);
    return null;
  }
}
