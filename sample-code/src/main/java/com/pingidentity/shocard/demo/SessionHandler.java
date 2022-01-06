package com.pingidentity.shocard.demo;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SessionHandler implements Route {

  private final Map<String, String> sessionMap = new HashMap<>();

  @Override
  public String handle(Request request, Response response) {
    String sessionId = request.params("sessionid");
    String sessionData = getWebSessionData(sessionId);
    response.type(ServerMain.APPLICATION_JSON);
    return sessionData;
  }

  public String getWebSessionData(String webSessionId) {
    return sessionMap.get("data." + webSessionId);
  }

  public String getSessionAction(String serverSessionId) {
    return sessionMap.get("action." + serverSessionId);
  }

  public void storeData(String serverSessionId, String data) {
    String webSessionId = sessionMap.get("map." + serverSessionId);
    if (webSessionId != null) {
      sessionMap.put("data." + webSessionId, data);
    } else {
      throw new RuntimeException(
          "Mapping WebSession ID not found for serverSessionID: " + serverSessionId);
    }
  }

  private void storeMap(SessionPair sessionPair, String action) {
    sessionMap.put("map." + sessionPair.webSessionId, sessionPair.serverSessionId);
    sessionMap.put("map." + sessionPair.serverSessionId, sessionPair.webSessionId);
    sessionMap.put("data." + sessionPair.webSessionId, "{ \"state\" : \"initial\" }");
    sessionMap.put("action." + sessionPair.serverSessionId, action);
  }

  public SessionPair startNewSession(String sessionAction) {
    SessionPair sessionPair = new SessionPair();
    storeMap(sessionPair, sessionAction);
    return sessionPair;
  }

  public static class SessionPair {

    private static final SecureRandom random = new SecureRandom();
    private final String serverSessionId;
    private final String webSessionId;

    public SessionPair() {
      serverSessionId = nextSessionId();
      webSessionId = nextSessionId();
    }

    public String getServerSessionId() {
      return serverSessionId;
    }

    public String getWebSessionId() {
      return webSessionId;
    }

    private String nextSessionId() {
      return new BigInteger(80, random).toString(32);
    }
  }
}
