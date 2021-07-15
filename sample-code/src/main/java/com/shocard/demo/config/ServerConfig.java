package com.shocard.demo.config;

import org.aeonbits.owner.Config;

/**
 * Created by gaurav on 3/31/17.
 */

public interface ServerConfig extends Config {

  @Key("SC_ENTITY_FILE")
  @DefaultValue("./shocard-entity.properties")
  String getShoCardEntityFileLocation();

  @Key("SC_CALLBACK_URL")
  String getShoCardEntityCallbackURL();
}