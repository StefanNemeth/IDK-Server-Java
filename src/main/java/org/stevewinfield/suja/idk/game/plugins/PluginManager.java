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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PluginManager {
    private static final Logger logger = Logger.getLogger(PluginManager.class);

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
        if (showLog) {
            logger.info("Loading plugin \"" + name + "\"");
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
                engine.eval(
                        "importClass(org.stevewinfield.suja.idk.Bootloader);" +
                                "importPackage(org.stevewinfield.suja.idk.game.miscellaneous);" +
                                "importPackage(org.stevewinfield.suja.idk.game.players);" +
                                "importPackage(org.stevewinfield.suja.idk.game.rooms);" +
                                "importClass(org.stevewinfield.suja.idk.game.plugins.PluginManager);"
                );
            }
            GamePlugin plugin = new GamePlugin(name, engine);
            engine.put("IDK", plugin);
            engine.put("gamePlugin", plugin);
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
        if (plugins.containsKey(name) && showLog) {
            logger.warn("A plugin by the name " + name + " already exists. Replacing it by " + filename);
        }
        this.plugins.put(name, (GamePlugin) engine.get("IDK"));
        if (showLog) {
            logger.info("Loaded the plugin \"" + name + "\".");
        }
        return true;
    }

    public void load(final File pluginDir) {
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(urlClassLoader);

            this.plugins = new ConcurrentHashMap<>();
            this.factory = new ScriptEngineManager();

            final File[] jars = pluginDir.listFiles();

            try {
                if (jars != null) {
                    for (final File f : jars) {
                        final String name = f.getName();
                        final FileReader reader = new FileReader(f);
                        this.addPlugin(name, reader, true);
                        reader.close();
                    }
                }
            } catch (final Exception e) {
                logger.error("Loading plugins failed.", e);
            }

            logger.info(plugins.size() + " plugin(s) local loaded from /" + pluginDir.getPath());
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }

    public static void log(final String txt) {
        logger.info("PLUGIN: " + txt);
    }

    public void initClassLoader(String... directories) {
        urlClassLoader = new URLClassLoader(buildClassPath(directories));
    }

    private static URL[] buildClassPath(String... directories) {
        try {
            final List<URL> classPath = new ArrayList<>();
            for (String directory : directories) {
                File directoryFile = new File(directory);
                if (!directoryFile.exists()) {
                    directoryFile.mkdirs();
                }
                if (!directoryFile.isDirectory()) {
                    continue;
                }
                for (File pathname : directoryFile.listFiles()) {
                    if (pathname.isFile() && pathname.toString().toLowerCase().endsWith(".jar")) {
                        URL url = pathname.toURI().toURL();
                        classPath.add(url);
                    }
                }
            }
            return classPath.toArray(new URL[classPath.size()]);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    private ConcurrentHashMap<String, GamePlugin> plugins;
    private ScriptEngineManager factory;
    private URLClassLoader urlClassLoader;
}
