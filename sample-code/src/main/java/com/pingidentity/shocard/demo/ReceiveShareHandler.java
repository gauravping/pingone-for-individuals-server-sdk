package com.pingidentity.shocard.demo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.pingidentity.did.sdk.client.DistributedIdClient;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateViewRoute;

public class ReceiveShareHandler implements TemplateViewRoute {

  private final SessionHandler sessionManager;
  private final UUID applicationInstanceId;
  private final DistributedIdClient distributedIdClient;

  public ReceiveShareHandler(SessionHandler sessionManager, UUID applicationInstanceId,
      DistributedIdClient distributedIdClient) {
    this.sessionManager = sessionManager;
    this.applicationInstanceId = applicationInstanceId;
    this.distributedIdClient = distributedIdClient;
  }

  @Override
  public ModelAndView handle(Request req, Response res) {
    SessionHandler.SessionPair sessionPair = sessionManager.startNewSession("certification_data");

    Map<String, Object> requestData = new HashMap<>();
    requestData.put("shocardid", applicationInstanceId);
    requestData.put("message", "Share your data with the site");
    requestData.put("ss_id", sessionPair.getServerSessionId());
    requestData.put("action", "request_share");
    requestData.put("requested_keys", ImmutableList.of(
        "U.S. Driver License->First Name",
        "U.S. Driver License->Last Name",
        "U.S. Driver License->Birth Date",
        "Profile ShoCard->CardImage"));

    Map<String, Object> request = ImmutableMap.of("shocard", requestData);

    String qrURL = getShareQRURL(request);

    Map<String, Object> attributes = ImmutableMap.of(
        "shocard_id", applicationInstanceId,
        "qr_url", qrURL,
        "server_session_id", sessionPair.getServerSessionId(),
        "web_session_id", sessionPair.getWebSessionId());

    return new ModelAndView(attributes, "home.ftl");
  }

  private String getShareQRURL(Map<String, Object> data) {
    return distributedIdClient.createRequest(data, "shocard.pingone.com",
        Instant.now().plusSeconds(3600)).getSelfUrl();
  }
}
