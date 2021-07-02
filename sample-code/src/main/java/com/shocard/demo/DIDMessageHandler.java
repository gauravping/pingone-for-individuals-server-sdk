package com.shocard.demo;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.pingidentity.did.sdk.client.ApplicationInstanceCreator;
import com.pingidentity.did.sdk.client.DistributedIdClient;
import com.pingidentity.did.sdk.client.MessageHandler;
import com.pingidentity.did.sdk.client.service.model.ApplicationInstance;
import com.pingidentity.did.sdk.client.service.model.Challenge;
import com.pingidentity.did.sdk.exception.DidException;
import com.pingidentity.did.sdk.jose.JwksGenerator;
import com.pingidentity.did.sdk.types.Claim;
import com.pingidentity.did.sdk.types.ClaimReference;
import com.pingidentity.did.sdk.types.Share;
import com.shocard.demo.config.ServerConfig;
import com.shocard.demo.utils.SessionManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

/**
 * Created by gaurav on 3/30/17.
 */
public class DIDMessageHandler implements MessageHandler {

  private static String APPLICATIONINSTANCE_KEY = "_APPLICATION_INSTANCE_ID_";
  private static String SERVER_CERTIFICATION_ID = "_SERVER_CERTIFICATION_ID_";

  private static final Logger logger = LoggerFactory.getLogger(DIDMessageHandler.class);

  private DistributedIdClient distributedIdClient;
  private ApplicationInstance applicationInstance;

  private SessionManager sessionManager;
  private ServerConfig serverConfig;
  private static final Gson gson = new Gson();

  private static final Set<String> requestedKeys =
      new ImmutableSet.Builder<String>().add("First Name")
          .add("Last Name")
          .add("Birth Date")
          .add("CardImage")
          .build();

  public DIDMessageHandler(SessionManager sessionManager, ServerConfig serverConfig) {
    this.sessionManager = sessionManager;
    this.serverConfig = serverConfig;
    initializeApplicationInstance();
  }

  /*
  This method initializes the distributedIdClient (https://git.shocard.io/ShoCard/ShoCard_Docs/wikis/home#distributedIdClient) which
  is needed to communicate with the ShoCard API. The distributedIdClient is used to make share and certify calls
  (https://git.shocard.io/ShoCard/ShoCard_Docs/wikis/home#certify-data) but is also used to register to receive notifications
  from the ShoCard API.
   */
  private void initializeApplicationInstance() {
    try {
      logger.trace(this.serverConfig.getShoCardEntityFileLocation());
      readFromFileInClasspath();
      if (applicationInstance == null)
        readFromFileInAbsolutePath();
      else {
          logger.trace("File does not exist ... creating a new ShoCard entity");
          applicationInstance = createApplicationInstance(
              this.serverConfig.getShoCardEntityFileLocation());
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }

    initDistributedIdClient();
  }

  private void readFromFileInClasspath() throws Exception {
    InputStream entityFile = getClass()
        .getResourceAsStream(this.serverConfig.getShoCardEntityFileLocation());
    if (entityFile != null) {
      logger.trace("File exists in classpath ... reading from file");
      readEntity(entityFile);
    }
  }

  private void readFromFileInAbsolutePath() throws Exception {
    if (new File(this.serverConfig.getShoCardEntityFileLocation()).exists()) {
      logger.trace("File exists on file system ... reading from file");
      InputStream entityFile =
          new FileInputStream(this.serverConfig.getShoCardEntityFileLocation());
      this.readEntity(entityFile);
    }
  }

  private void readEntity(InputStream entityFile) throws Exception {
    BufferedReader br = new BufferedReader(
        new InputStreamReader(entityFile, Charset.defaultCharset()));
    StringBuilder sb = new StringBuilder();

    String line = br.readLine();
    while (line != null) {
      sb.append(line);
      sb.append(System.lineSeparator());
      line = br.readLine();
    }
    br.close();

    applicationInstance = ApplicationInstance.fromJson(sb.toString());
  }

  private void initDistributedIdClient() {
    distributedIdClient = new DistributedIdClient.Builder(applicationInstance).build();
    logger.info("ApplicationInstanceId: {}", applicationInstance.getId().toString());
    logger.trace("Initializing DID Client");
    this.distributedIdClient.setMessageHandler(this);
    if (serverConfig.getShoCardEntityCallbackURL() != null) {
      distributedIdClient.updatePushToken(serverConfig.getShoCardEntityCallbackURL());
    } else {
      ProcessMessagesTask processMessagesTask = new ProcessMessagesTask(distributedIdClient);
      Timer timer = new Timer();
      timer.scheduleAtFixedRate(processMessagesTask, 2000, 2000);
    }
  }

  class ProcessMessagesTask extends TimerTask {

    private final DistributedIdClient distributedIdClient;

    public ProcessMessagesTask(DistributedIdClient distributedIdClient) {
      this.distributedIdClient = distributedIdClient;
    }

    @Override
    public void run() {
      logger.trace("Checking for messages");
      this.distributedIdClient.processMessages();
    }
  }

  private ApplicationInstance createApplicationInstance(String fileLocation) throws Exception {
    ApplicationInstanceCreator applicationInstanceCreator =
        new ApplicationInstanceCreator();
    JwksGenerator jwksGenerator = new JwksGenerator();
    ApplicationInstance applicationInstance =
        applicationInstanceCreator.createForWeb(jwksGenerator.generate());

    PrintWriter out = new PrintWriter(fileLocation, Charset.defaultCharset().name());
    out.println(applicationInstance.toJson(true));
    out.close();
    return applicationInstance;
  }

  public String getApplicationInstanceId() {
    return applicationInstance.getId().toString();
  }

  public String getShareQRURL(Map<String, Object> data) {
    com.pingidentity.did.sdk.types.Request request = distributedIdClient.createRequest(data,
        "shocard.pingone.com",
        Instant.now().plusSeconds(3600)
    );

    return request.getSelfUrl();
  }

  public String handleAPICallback(Request req, spark.Response res) {
    distributedIdClient.processMessages(this);
    return "{}";
  }

  public String certifyData(Request req, Response res) {
    String sessionId = req.params("sessionid");
    logger.trace("Retrieving for key: " + sessionId);
    Share share = ServerMain.shares.get(sessionId);
    if (share != null) {
      String holderApplicationInstanceId = req.queryParams("applicationInstanceId");
      Claim claim = this.distributedIdClient
          .createClaim(UUID.fromString(holderApplicationInstanceId),
              getDeveloperPortalCertificationMessage(), null,
              share.getClaim().getData(), share.getClaim());
    }
    String url = getServerName(req.url());
    String scheme = req.scheme();

    res.redirect(scheme + url + "/");
    return null;
  }

  private String getDeveloperPortalCertificationMessage() {
    Map<String, String> messageData = new HashMap<>();
    messageData.put("name", "ShoCard Certify Demo");
    messageData.put("logourl", "https://developer.shocard.io/images/shocard_logo.png");
    messageData.put("siteurl", "https://certifyserver.herokuapp.com");
    return gson.toJson(messageData);
  }

  public void sendRequest(String message, String applicationInstanceId) {
    this.distributedIdClient.requestShare(UUID.fromString(applicationInstanceId),
        message,
        Challenge.createWithExpiration(Instant.now().plusSeconds(86_400)),
        Arrays.asList(new String[]{"First Name"}));
  }

  public String cancelCertification(Request req, Response res) {
    String certificationId = req.params("certificationid");
    try {
      Claim claim = ServerMain.claims.get(certificationId);
      ClaimReference claimReference = this.distributedIdClient
          .expireClaim(claim, "Demo server canceled claim", null);
    } catch (Exception e) {
      logger.error("There was an error canceling certification with id: {}", certificationId, e);
      return "There was an error canceling certification with id: " + certificationId;
    }
    String url = getServerName(req.url());
    String scheme = req.scheme();

    res.redirect(scheme + url + "/");
    return null;
  }

  private String getServerName(String URL) {
    String pattern = ":/?/?([^:/\\s]+)(:\\d+){0,1}";

    // Create a Pattern object
    Pattern r = Pattern.compile(pattern);

    // Now create matcher object.
    Matcher m = r.matcher(URL);
    if (m.find()) {
      logger.trace("Server: " + m.group(0));
      return m.group(0);
    } else {
      logger.trace("NO MATCH");
      return "";
    }
  }

  @Override
  public void handleShare(UUID applicationInstanceId, String message, Challenge challenge,
      List<Share> shares,
      List<DidException> exceptions) {
    String action = this.sessionManager.getSessionAction(challenge.getId());
    if (action.equalsIgnoreCase("certification_data")) {
      Map<String, String> storeData = new HashMap<>();
      for (Object shareObject : shares.toArray()) {
        Share share = (Share) shareObject;
        // if a Claim was created store the data for creating
        // a new certification using the "Certify" button on the page.
        if (share.getClaim() == null // check to see if a Claim was shared
            // make sure the passed claim is a self-attestation of the data from the sender
            || share.getClaim().getIssuer().getData()
            .equalsIgnoreCase(share.getClaim().getHolder().getData())) {
          storeData.put(APPLICATIONINSTANCE_KEY, applicationInstanceId.toString());
          share.getKeys().stream().forEach($0 -> {
            if (requestedKeys.contains($0)) {
              storeData.put($0, share.getClaim().getData().get($0));
            }
          });
          ServerMain.shares.put(challenge.getId(), share);
          logger.trace("Storing to cache for key: " + challenge.getId());
        }

        // if this is a third party certification and it was created using this server
        // let the JS on the page know that the certification can be canceled.
        if (share.getClaim() != null
            && !share.getClaim().getIssuer().getData()
            .equalsIgnoreCase(share.getClaim().getHolder().getData())
            && share.getClaim().getIssuer().getData()
            .equalsIgnoreCase(this.applicationInstance.getId().toString())) {
          storeData
              .put(SERVER_CERTIFICATION_ID, share.getClaim().getId().toString());
          ServerMain.claims
              .put(share.getClaim().getId().toString(), share.getClaim());

        }
      }
      sessionManager.storeData(challenge.getId(), gson.toJson(storeData));
    }
  }

  @Override
  public void handleShareRequest(UUID uuid, String s, Challenge challenge, List<String> list) {

  }

  @Override
  public void handleClaim(UUID uuid, String s, Challenge challenge, Claim claim,
      List<DidException> list) {

  }

  @Override
  public void handleExpiredClaim(UUID uuid, String s, Challenge challenge,
      ClaimReference claimReference, List<DidException> list) {

  }

  @Override
  public void handleException(DidException e) {

  }
}
