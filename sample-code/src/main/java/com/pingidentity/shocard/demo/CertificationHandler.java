package com.pingidentity.shocard.demo;

import com.pingidentity.did.sdk.client.DistributedIdClient;
import com.pingidentity.did.sdk.client.service.model.Challenge;
import com.pingidentity.did.sdk.types.Claim;
import com.pingidentity.did.sdk.types.ClaimReference;
import com.pingidentity.did.sdk.types.Share;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

public class CertificationHandler {
  private static final Logger logger = LoggerFactory.getLogger(CertificationHandler.class);

  private final DistributedIdClient distributedIdClient;

  public CertificationHandler(
      DistributedIdClient distributedIdClient) {
    this.distributedIdClient = distributedIdClient;
  }

  public Object certifyData(Request req, Response res) {
    String sessionId = req.params("sessionid");
    logger.trace("Retrieving for key: {}", sessionId);
    Share share = ServerMain.SHARE_MAP.get(sessionId);
    if (share != null) {
      String holderApplicationInstanceId = req.queryParams("applicationInstanceId");
      Claim claim = distributedIdClient
          .createClaim(UUID.fromString(holderApplicationInstanceId),
              "Claim from demo site", null,
              share.getClaim().getData(), share.getClaim());
      logger.info("Created claim {}", claim);
    }

    res.redirect("/");

    return null;
  }

  public Object sendRequest(Request req, Response res) {
    UUID applicationInstanceId = UUID.fromString(req.queryParams("scid"));

    distributedIdClient.requestShare(applicationInstanceId,
        req.queryParams("message"),
        Challenge.createWithExpiration(Instant.now().plusSeconds(86_400)),
        Collections.singletonList("First Name"));

    res.redirect("/send");

    return null;
  }

  public Object cancelCertification(Request req, Response res) {
    String certificationId = req.params("certificationid");
    try {
      Claim claim = ServerMain.CLAIM_MAP.get(certificationId);
      ClaimReference claimReference = distributedIdClient
          .expireClaim(claim, "Demo server canceled claim", null);
      logger.info("Cancelled claim {}", claimReference);
    } catch (Exception e) {
      logger.error("There was an error canceling certification with id: {}", certificationId, e);
      return "There was an error canceling certification with id: " + certificationId;
    }

    res.redirect("/");

    return null;
  }

}
