/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.plugins;

import org.apache.log4j.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;

public class PluginManager {
    private static Logger logger = Logger.getLogger(PluginManager.class);

    public GamePlugin getPlugin(final String name) {
        return this.plugins.containsKey(name) ? this.plugins.get(name) : null;
    }

    public PluginManager() {
    }

    public boolean addPlugin(final String filename, final Object content, final boolean showLog) {
        String extension = "";

        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i + 1);
        }
        String name = filename.substring(0, filename.length() - extension.length() - 1);
        if (extension.isEmpty()) {
            extension = "js";
            name = filename;
        }
        final ScriptEngine engine = factory.getEngineByExtension(extension);
        if (engine == null) {
            logger.error("No script engine found for plugin " + name);
            return false;
        }
        try {
            if (engine.getFactory().getLanguageName().equals("ECMAScript")) {
                // needed to make the imports work
                if (engine.getFactory().getEngineName().equals("Oracle Nashorn")) {
                    engine.eval("load(\"nashorn:mozilla_compat.js\");");
                }
                engine.eval("importClass(org.stevewinfield.suja.idk.Bootloader);"
                        + "importPackage(org.stevewinfield.suja.idk.game.miscellaneous);"
                        + "importPackage(org.stevewinfield.suja.idk.game.players);"
                        + "importPackage(org.stevewinfield.suja.idk.game.rooms);"
                        + "importClass(org.stevewinfield.suja.idk.game.plugins.PluginManager);");
            }
            engine.put("IDK", new GamePlugin(name, engine));
            engine.put("logger", Logger.getLogger(name));
            if (content instanceof FileReader) {
                engine.eval((FileReader) content);
            } else {
                engine.eval((String) content);
            }
            PluginInterfaces.ScriptPlugin scriptPlugin = ((Invocable) engine).getInterface(PluginInterfaces.ScriptPlugin.class);
            if (scriptPlugin == null) {
                logger.warn("No method initializePlugin() found for plugin " + name);
            } else {
                try {
                    scriptPlugin.initializePlugin();
                } catch (final Throwable t) {
                    logger.error("Plugin Initialization Error", t);
                }
            }
        } catch (final ScriptException e) {
            logger.error("ScriptException for plugin " + name, e);
        }
        if (plugins.containsKey(name)) {
            logger.error("A plugin by the name " + name + " already exists. Replacing it by " + filename);
        }
        this.plugins.put(name, (GamePlugin) engine.get("IDK"));
        if (showLog)
            logger.info("Loaded the plugin \"" + name + "\".");
        return true;
    }

    public void load(final File pluginDir) {
        this.plugins = new ConcurrentHashMap<String, GamePlugin>();
        this.factory = new ScriptEngineManager();

        final File[] jars = pluginDir.listFiles();

        try {
            for (final File f : jars) {
                final String name = f.getName();
                final FileReader reader = new FileReader(f);
                this.addPlugin(name, reader, true);
                reader.close();
            }
        } catch (final Exception e) {
            logger.error("Loading plugins failed.", e);
        }

        logger.info(plugins.size() + " plugin(s) local loaded from /" + pluginDir.getPath());
    }

    public static void log(final String txt) {
        logger.info("PLUGIN: " + txt);
    }

    private ConcurrentHashMap<String, GamePlugin> plugins;
    private ScriptEngineManager factory;
}
