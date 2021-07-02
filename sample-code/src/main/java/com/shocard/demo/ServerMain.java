package com.shocard.demo;

import com.google.common.collect.ImmutableList;
import com.pingidentity.did.sdk.types.Claim;
import com.pingidentity.did.sdk.types.Share;
import com.shocard.demo.config.ServerConfig;
import com.shocard.demo.utils.SessionManager;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

/**
 * Created by gaurav on 3/31/17.
 */
public class ServerMain {

  ServerConfig serverConfig;
  protected SessionManager sessionManager = new SessionManager();
  protected DIDMessageHandler didMessageHandler;

  public static Map<String, Share> shares;
  public static Map<String, Claim> claims;

  public static final String APPLICATION_JSON = "application/json";
//  public static final Mochi = new GsonBuilder()
//      .registerTypeAdapterFactory(new StringValueTypeAdapterFactory())
//      .create();

  private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);

  public static void main(String[] args) {
    try {
      staticFiles.location("public");

      port(getHerokuAssignedPort());

      ServerMain main = new ServerMain();

      setupCache();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public ServerMain() {
    init();
  }

  private void init() {
    serverConfig = ConfigFactory.create(ServerConfig.class, System.getenv());
    didMessageHandler = new DIDMessageHandler(sessionManager, serverConfig);

    get("/status", this::healthCheck);
    get("/", this::receiveSharePage, new FreeMarkerEngine());
    get("/sessions/:sessionid", APPLICATION_JSON, sessionManager::getSessionData);
    post("/callback/notify", APPLICATION_JSON, didMessageHandler::handleAPICallback);
    post("/certifications/certify/:sessionid", didMessageHandler::certifyData);
    get("/certifications/cancel/:certificationid", didMessageHandler::cancelCertification);
    get("/send", (req, res) -> {
      Map<String, Object> model = new HashMap<>();
      return new ModelAndView(null, "send_request.ftl");
    }, new FreeMarkerEngine());
    post("/send", (req, res) -> {
      didMessageHandler.sendRequest(req.queryParams("message"), req.queryParams("scid"));
      res.redirect("/send");
      return null;
    });
  }

  public String healthCheck(Request req, Response res) {
    return "OK";
  }

  public ModelAndView receiveSharePage(Request req, Response res) {
    SessionManager.SessionPair sessionPair = sessionManager.startNewSession("certification_data");
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("shocard_id", didMessageHandler.getApplicationInstanceId());

    Map<String, Object> request = new HashMap<>();
    Map<String, Object> requestData = new HashMap<>();
    requestData.put("shocardid", didMessageHandler.getApplicationInstanceId());
    requestData.put("message", "Share your data with the site");
    requestData.put("ss_id", sessionPair.serverSessionId);
    requestData.put("action", "request_share");
    requestData.put("requested_keys", ImmutableList.<String>builder()
        .add("U.S. Driver License->First Name",
            "U.S. Driver License->Last Name",
            "U.S. Driver License->Birth Date",
            "Profile ShoCard->CardImage")
        .build());
    request.put("shocard", requestData);

    String qrURL = didMessageHandler.getShareQRURL(request);
    logger.trace("QR URL: {}", qrURL);
    attributes.put("qr_url", qrURL);

    attributes.put("server_session_id", sessionPair.serverSessionId);
    attributes.put("web_session_id", sessionPair.webSessionId);

    // The home.ftl file is located in directory:
    // src/test/resources/spark/template/freemarker
    return new ModelAndView(attributes, "home.ftl");
  }

  private static int getHerokuAssignedPort() {
    return Integer.parseInt(System.getenv().getOrDefault("PORT", "4567"));
  }

  public static void setupCache() {
    shares = new HashMap<>();
    claims = new HashMap<>();
  }
}
