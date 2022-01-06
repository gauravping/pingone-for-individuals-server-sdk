package com.pingidentity.shocard.demo;

import com.pingidentity.did.sdk.client.ApplicationInstanceCreator;
import com.pingidentity.did.sdk.client.service.model.ApplicationInstance;
import com.pingidentity.did.sdk.jose.JwksGenerator;
import com.pingidentity.shocard.demo.config.ServerConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationInstanceUtil {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationInstanceUtil.class);

  private final ApplicationInstance applicationInstance;

  public ApplicationInstanceUtil(ServerConfig serverConfig) {
    this.applicationInstance = readFromFile(serverConfig.getApplicationInstanceFileLocation())
        .orElseGet(() -> createApplicationInstance(serverConfig.getApplicationInstanceFileLocation()));
  }

  public ApplicationInstance getApplicationInstance() {
    return applicationInstance;
  }

  private Optional<ApplicationInstance> readFromFile(String filepath) {
    Path path = Paths.get(filepath);

    if (!Files.exists(path)) {
      return Optional.empty();
    }

    try {
      List<String> lines = Files.readAllLines(path);
      String json = String.join("\n", lines);

      logger.info("Read ApplicationInstance from {}", path);

      return Optional.of(ApplicationInstance.fromJson(json));
    } catch (IOException e) {
      throw new RuntimeException("Could not read ApplicationInstance from " + path, e);
    }

  }

  private ApplicationInstance createApplicationInstance(String fileLocation) {
    ApplicationInstanceCreator applicationInstanceCreator = new ApplicationInstanceCreator();
    JwksGenerator jwksGenerator = new JwksGenerator();
    ApplicationInstance newApplicationInstance =
        applicationInstanceCreator.createForWeb(jwksGenerator.generate());

    Path path = Paths.get(fileLocation);
    try {
      Files.write(path, Collections.singletonList(newApplicationInstance.toJson(true)));
    } catch (IOException e) {
      throw new RuntimeException("Could not save ApplicationInstance to " + path, e);
    }

    logger.info("Saved new ApplicationInstance to {}", path);

    return newApplicationInstance;
  }
}
