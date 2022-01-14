package com.pingidentity.shocard.demo;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.pingidentity.did.sdk.client.MessageHandler;
import com.pingidentity.did.sdk.client.service.model.ApplicationInstance;
import com.pingidentity.did.sdk.client.service.model.Challenge;
import com.pingidentity.did.sdk.exception.DidException;
import com.pingidentity.did.sdk.types.Claim;
import com.pingidentity.did.sdk.types.ClaimReference;
import com.pingidentity.did.sdk.types.Share;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DIDMessageHandler implements MessageHandler {
  private static final Logger logger = LoggerFactory.getLogger(DIDMessageHandler.class);
  private static final Gson gson = new Gson();
  private static final Set<String> requestedKeys =
      new ImmutableSet.Builder<String>().add("CardType")
          .add("cardId")
          .build();
  private final SessionHandler sessionManager;
  private final ApplicationInstance applicationInstance;

  public DIDMessageHandler(SessionHandler sessionManager,
      ApplicationInstance applicationInstance) {
    this.sessionManager = sessionManager;
    this.applicationInstance = applicationInstance;
  }

  @Override
  public void handleShare(UUID applicationInstanceId, String message, Challenge challenge,
      List<Share> shares, List<DidException> exceptions) {

    String action = this.sessionManager.getSessionAction(challenge.getId());

    if (action.equalsIgnoreCase("certification_data")) {
      Map<String, String> storeData = new HashMap<>();
      for (Share share : shares) {
        final String cardType = share.getKeys().contains("CardType") ?
            share.getClaim().getData().get("CardType") + "->" :
            "";
        share.getKeys().forEach(key -> {
          if (!requestedKeys.contains(key)) {
            storeData.put(cardType + key, share.getClaim().getData().get(key));
          }
        });
        logger.trace("Storing to cache for key: {}", challenge.getId());
        ServerMain.SHARE_MAP.put(challenge.getId(), share);

      }
      sessionManager.storeData(challenge.getId(), gson.toJson(storeData));
    }
  }

  @Override
  public void handleShareRequest(UUID uuid, String s, Challenge challenge, List<String> list) {
    // not implemented
  }

  @Override
  public void handleClaim(UUID uuid, String s, Challenge challenge, Claim claim,
      List<DidException> list) {
    // not implemented
  }

  @Override
  public void handleExpiredClaim(UUID uuid, String s, Challenge challenge,
      ClaimReference claimReference, List<DidException> list) {
    // not implemented
  }

  @Override
  public void handleException(DidException e) {
    // not implemented
  }
}
