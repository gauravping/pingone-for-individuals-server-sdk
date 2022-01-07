package com.pingidentity.shocard.demo.config;

import org.aeonbits.owner.Config;

public interface ServerConfig extends Config {

  @Key("APPLICATION_INSTANCE_FILE")
  @DefaultValue("./application-instance.json")
  String getApplicationInstanceFileLocation();

  @Key("CALLBACK_URL")
  String getCallbackURL();
}