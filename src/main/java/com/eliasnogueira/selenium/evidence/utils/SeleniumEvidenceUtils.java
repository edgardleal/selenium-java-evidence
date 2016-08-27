package com.eliasnogueira.selenium.evidence.utils;

import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class with shared methods and action to Selenium Evidence
 *
 * @author Elias Nogueira <elias.nogueira@gmail.com>
 */
public class SeleniumEvidenceUtils {
private static final CombinedConfiguration config;
  static{
    config = new CombinedConfiguration();
    try {
      config.addConfiguration(new PropertiesConfiguration("test.properties"));
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
  }

  /**
   * Properties that loads report parameterization
   */
  static Properties properties;


  /**
   * Load Selenium Evidence property file
   *
   * @return a property
   * @throws IOException if file do not exists
   */
  public static Properties loadProperties() throws IOException {
    properties = new Properties();
    properties.load(new FileInputStream("init.properties"));
    return properties;
  }

  public static String get(final String key){
    return get(key, "");
  }

  public static String get(final String key, final String defaultValue){
    return config.getString(key, defaultValue);
  }

}
