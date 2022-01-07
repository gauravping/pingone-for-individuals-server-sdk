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

  private static final String APPLICATIONINSTANCE_KEY = "_APPLICATION_INSTANCE_ID_";
  private static final String SERVER_CERTIFICATION_ID = "_SERVER_CERTIFICATION_ID_";

  private static final Logger logger = LoggerFactory.getLogger(DIDMessageHandler.class);
  private static final Gson gson = new Gson();
  private static final Set<String> requestedKeys =
      new ImmutableSet.Builder<String>().add("First Name")
          .add("Last Name")
          .add("Birth Date")
          .add("CardImage")
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
        // if a Claim was created store the data for creating
        // a new certification using the "Certify" button on the page.
        if (share.getClaim() == null // check to see if a Claim was shared
            // make sure the passed claim is a self-attestation of the data from the sender
            || share.getClaim().getIssuer().getData()
            .equalsIgnoreCase(share.getClaim().getHolder().getData())) {
          storeData.put(APPLICATIONINSTANCE_KEY, applicationInstanceId.toString());
          share.getKeys().forEach(key -> {
            if (requestedKeys.contains(key)) {
              storeData.put(key, share.getClaim().getData().get(key));
            }
          });
          logger.trace("Storing to cache for key: {}", challenge.getId());
          ServerMain.SHARE_MAP.put(challenge.getId(), share);
        }

        // if this is a third party certification, and it was created using this server,
        // let the JS on the page know that the certification can be canceled.
        if (share.getClaim() != null
            && !share.getClaim().getIssuer().getData()
            .equalsIgnoreCase(share.getClaim().getHolder().getData())
            && share.getClaim().getIssuer().getData()
            .equalsIgnoreCase(this.applicationInstance.getId().toString())) {
          storeData
              .put(SERVER_CERTIFICATION_ID, share.getClaim().getId().toString());
          ServerMain.CLAIM_MAP
              .put(share.getClaim().getId().toString(), share.getClaim());
        }
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
