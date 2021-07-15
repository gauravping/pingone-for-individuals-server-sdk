package com.shocard.demo.utils;

import com.shocard.demo.ServerMain;
import spark.Request;
import spark.Response;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gaurav on 3/31/17.
 */
public class SessionManager {

  private Map<String, String> sessionData = new HashMap<>();

  public SessionManager() {
  }

  public String getSessionData(Request req, Response res) {
    String sessionId = req.params("sessionid");
    String sessionData = getWebSessionData(sessionId);
    res.type(ServerMain.APPLICATION_JSON);
    return sessionData;
  }

  public String getWebSessionData(String webSessionId) {
    return sessionData.get("data." + webSessionId);
  }

  public String getSessionAction(String serverSessionId) {
    return sessionData.get("action." + serverSessionId);
  }

  public void storeData(String serverSessionId, String data) {
    String webSessionId = sessionData.get("map." + serverSessionId);
    if (webSessionId != null) {
      sessionData.put("data." + webSessionId, data);
    } else {
      throw new RuntimeException("Mapping WebSession ID not found for serverSessionID: " + serverSessionId);
    }
  }

  private void storeMap(SessionPair sessionPair, String action) {
    sessionData.put("map." + sessionPair.webSessionId, sessionPair.serverSessionId);
    sessionData.put("map." + sessionPair.serverSessionId, sessionPair.webSessionId);
    sessionData.put("data." + sessionPair.webSessionId, "{ \"state\" : \"initial\" }");
    sessionData.put("action." + sessionPair.serverSessionId, action);
  }

  public SessionPair startNewSession(String sessionAction) {
    SessionPair sessionPair = new SessionPair();
    storeMap(sessionPair, sessionAction);
    return sessionPair;
  }

  public static class SessionPair {
    public String serverSessionId;
    public String webSessionId;

    public SessionPair() {
      serverSessionId = nextSessionId();
      webSessionId = nextSessionId();
    }

    private static final SecureRandom random = new SecureRandom();

    public String nextSessionId() {
      return new BigInteger(80, random).toString(32);
    }
  }
}
