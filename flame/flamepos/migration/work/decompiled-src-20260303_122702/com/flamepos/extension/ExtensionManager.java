/*
 * Decompiled with CFR 0.151.
 * 
 * Could not load the following classes:
 *  com.floreantpos.extension.FloreantPlugin
 *  net.xeoh.plugins.base.Plugin
 *  net.xeoh.plugins.base.PluginManager
 *  net.xeoh.plugins.base.impl.PluginManagerFactory
 *  net.xeoh.plugins.base.options.AddPluginsFromOption
 *  net.xeoh.plugins.base.util.PluginManagerUtil
 *  org.apache.commons.lang.StringUtils
 */
package com.floreantpos.extension;

import com.floreantpos.extension.FloreantPlugin;
import com.floreantpos.main.Application;
import com.floreantpos.util.JarUtil;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.AddPluginsFromOption;
import net.xeoh.plugins.base.util.PluginManagerUtil;
import org.apache.commons.lang.StringUtils;

public class ExtensionManager {
    private List<FloreantPlugin> plugins;
    private static ExtensionManager instance;

    private synchronized void initialize() {
        PluginManager pluginManager = PluginManagerFactory.createPluginManager();
        String jarLocation = JarUtil.getJarLocation(Application.class);
        URI uri = new File(jarLocation).toURI();
        pluginManager.addPluginsFrom(uri, new AddPluginsFromOption[0]);
        String pluginsPath = System.getProperty("pluginsPath");
        if (StringUtils.isNotEmpty((String)pluginsPath)) {
            String[] paths;
            for (String string : paths = pluginsPath.split(",")) {
                pluginManager.addPluginsFrom(new File(string).toURI(), new AddPluginsFromOption[0]);
            }
        } else {
            pluginManager.addPluginsFrom(new File("plugins/").toURI(), new AddPluginsFromOption[0]);
        }
        PluginManagerUtil pmUtil = new PluginManagerUtil(pluginManager);
        List allPlugins = (List)pmUtil.getPlugins();
        Collections.sort(allPlugins, new Comparator<Plugin>(){

            @Override
            public int compare(Plugin o1, Plugin o2) {
                return o1.getClass().getName().compareToIgnoreCase(o2.getClass().getName());
            }
        });
        ArrayList<FloreantPlugin> floreantPlugins = new ArrayList<FloreantPlugin>();
        for (Plugin plugin : allPlugins) {
            if (!(plugin instanceof FloreantPlugin)) continue;
            FloreantPlugin floreantPlugin = (FloreantPlugin)plugin;
            if (floreantPlugin.requireLicense()) {
                floreantPlugin.initLicense();
                if (!floreantPlugin.hasValidLicense()) continue;
                floreantPlugins.add(floreantPlugin);
                continue;
            }
            floreantPlugins.add(floreantPlugin);
        }
        this.plugins = Collections.unmodifiableList(floreantPlugins);
    }

    public static List<FloreantPlugin> getPlugins() {
        return ExtensionManager.getInstance().plugins;
    }

    public static List<FloreantPlugin> getPlugins(Class pluginClass) {
        ArrayList<FloreantPlugin> list = new ArrayList<FloreantPlugin>();
        for (FloreantPlugin floreantPlugin : ExtensionManager.getInstance().plugins) {
            if (!pluginClass.isAssignableFrom(floreantPlugin.getClass())) continue;
            list.add(floreantPlugin);
        }
        return list;
    }

    public static FloreantPlugin getPlugin(Class pluginClass) {
        for (FloreantPlugin floreantPlugin : ExtensionManager.getInstance().plugins) {
            if (!pluginClass.isAssignableFrom(floreantPlugin.getClass())) continue;
            return floreantPlugin;
        }
        return null;
    }

    public static synchronized ExtensionManager getInstance() {
        if (instance == null) {
            instance = new ExtensionManager();
            instance.initialize();
        }
        return instance;
    }
}

