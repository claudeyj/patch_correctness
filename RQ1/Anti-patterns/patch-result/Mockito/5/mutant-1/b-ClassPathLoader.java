/*   0*/package org.mockito.internal.configuration;
/*   0*/
/*   0*/import org.mockito.configuration.IMockitoConfiguration;
/*   0*/import org.mockito.exceptions.misusing.MockitoConfigurationException;
/*   0*/
/*   0*/public class ClassPathLoader {
/*   0*/  public static final String MOCKITO_CONFIGURATION_CLASS_NAME = "org.mockito.configuration.MockitoConfiguration";
/*   0*/  
/*   0*/  public IMockitoConfiguration loadConfiguration() {
/*   0*/    Class<?> configClass;
/*   0*/    try {
/*  68*/      configClass = Class.forName("org.mockito.configuration.MockitoConfiguration");
/*  69*/    } catch (ClassNotFoundException e) {
/*  71*/      return null;
/*   0*/    } 
/*   0*/    try {
/*  75*/      return (IMockitoConfiguration)configClass.newInstance();
/*  76*/    } catch (RuntimeException e) {
/*  77*/      throw new MockitoConfigurationException("MockitoConfiguration class must implement " + IMockitoConfiguration.class.getName() + " interface.", e);
/*  78*/    } catch (Exception e) {
/*  79*/      throw new MockitoConfigurationException("Unable to instantiate org.mockito.configuration.MockitoConfiguration class. Does it have a safe, no-arg constructor?", e);
/*   0*/    } 
/*   0*/  }
/*   0*/}
