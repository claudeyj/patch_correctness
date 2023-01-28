/*   0*/package org.mockito.internal.configuration.plugins;
/*   0*/
/*   0*/import java.io.InputStream;
/*   0*/import java.net.URL;
/*   0*/import org.mockito.exceptions.base.MockitoException;
/*   0*/import org.mockito.internal.util.io.IOUtil;
/*   0*/import org.mockito.plugins.PluginSwitch;
/*   0*/
/*   0*/class PluginFinder {
/*   0*/  private final PluginSwitch pluginSwitch;
/*   0*/  
/*   0*/  public PluginFinder(PluginSwitch pluginSwitch) {
/*  15*/    this.pluginSwitch = pluginSwitch;
/*   0*/  }
/*   0*/  
/*   0*/  String findPluginClass(Iterable<URL> resources) {
/*  19*/    for (URL resource : resources) {
/*  20*/      InputStream s = null;
/*   0*/      try {
/*  22*/        s = resource.openStream();
/*  23*/        String pluginClassName = new PluginFileReader().readPluginClass(s);
/*  24*/        if (pluginClassName == null) {
/*  36*/          IOUtil.closeQuietly(s);
/*   0*/          continue;
/*   0*/        } 
/*   0*/        if (!this.pluginSwitch.isEnabled(pluginClassName)) {
/*  36*/          IOUtil.closeQuietly(s);
/*   0*/          continue;
/*   0*/        } 
/*   0*/        return pluginClassName;
/*   0*/      } catch (Exception e) {
/*   0*/        throw new MockitoException("Problems reading plugin implementation from: " + resource, e);
/*   0*/      } finally {
/*  36*/        IOUtil.closeQuietly(s);
/*   0*/      } 
/*   0*/    } 
/*  39*/    return null;
/*   0*/  }
/*   0*/}
