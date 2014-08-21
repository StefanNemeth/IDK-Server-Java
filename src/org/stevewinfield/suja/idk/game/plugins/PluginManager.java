/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.plugins;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.*;

import org.apache.log4j.Logger;

public class PluginManager {
    private static Logger logger = Logger.getLogger(PluginManager.class);

    public GamePlugin getPlugin(final String name) {
        return this.plugins.containsKey(name) ? this.plugins.get(name) : null;
    }

    public PluginManager() {
    }

    public boolean addPlugin(final String name, final Object content, final boolean showLog) {
        final ScriptEngine engine = factory.getEngineByName("JavaScript");
        this.plugins.put(name.toLowerCase(), new GamePlugin(name, engine));
        try {
            engine.eval("importClass(org.stevewinfield.suja.idk.Bootloader);"
            + "importPackage(org.stevewinfield.suja.idk.game.miscellaneous);"
            + "importPackage(org.stevewinfield.suja.idk.game.players);"
            + "importPackage(org.stevewinfield.suja.idk.game.rooms);"
            + "importClass(org.stevewinfield.suja.idk.game.plugins.PluginManager);"
            + "var IDK = Bootloader.getPluginManager().getPlugin('" + name.toLowerCase() + "');");
            if (content instanceof FileReader) {
                engine.eval((FileReader) content);
            } else {
                engine.eval((String) content);
            }
            try {
                ((Invocable) engine).invokeFunction("initializePlugin");
            } catch (final NoSuchMethodException ex) {
            }
        } catch (final ScriptException e) {
            logger.error("ScriptException", e);
        }
        this.plugins.put(name.toLowerCase(), (GamePlugin) engine.get("IDK"));
        if (showLog)
            logger.info("Loaded the plugin \"" + name + "\".");
        return true;
    }

    public void load() {
        this.plugins = new ConcurrentHashMap<String, GamePlugin>();
        this.factory = new ScriptEngineManager();

        final File pluginDir = new File("plugins");
        final File[] jars = pluginDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File pathname) {
                return pathname.getName().endsWith(".js");
            }
        });

        try {
            for (final File f : jars) {
                final String name = f.getName().substring(0, f.getName().length() - 3);
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
