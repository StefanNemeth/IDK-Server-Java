/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.placeholder.services.PlaceholderNetwork;
import org.stevewinfield.suja.idk.communication.MessageHandler;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.Game;
import org.stevewinfield.suja.idk.game.plugins.PluginManager;
import org.stevewinfield.suja.idk.network.ConnectionListener;
import org.stevewinfield.suja.idk.network.sessions.SessionManager;
import org.stevewinfield.suja.idk.storage.Storage;
import org.stevewinfield.suja.idk.threadpools.ServerTask;
import org.stevewinfield.suja.idk.threadpools.WorkerTasks;

import com.google.common.io.BaseEncoding;

public class Bootloader {
    private static Logger logger = Logger.getLogger(Bootloader.class);

    public static Settings getSettings() {
        return settings;
    }

    public static SessionManager getSessionManager() {
        return sessionManager;
    }

    public static Game getGame() {
        return game;
    }

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    public static Storage getStorage() {
        return storage;
    }

    public static void main(final String[] args) {
        Properties buildProperties = new Properties();
        try {
            buildProperties.load(Bootloader.class.getResourceAsStream("/build.properties"));
        } catch (Exception ignored) {
        }
        IDK.NAME = buildProperties.getProperty("name", "IDK");
        IDK.BUILD_NUMBER = buildProperties.getProperty("build", "UNKNOWN");
        IDK.VERSION = buildProperties.getProperty("version", "0.0.0");

        PropertyConfigurator.configure("log4j.properties");
        logger.info(IDK.NAME + " (Version " + IDK.VERSION + ", Build " + IDK.BUILD_NUMBER + ") is starting.");

        final Properties pFile = new Properties();

        try {
            pFile.load(new FileInputStream(".properties"));
        } catch (final FileNotFoundException e) {
            logger.error("File \".properties\" not found.", e);
            return;
        } catch (final IOException e) {
            logger.error("Couldn't read file \".properties\".", e);
            return;
        }

        settings = new Settings(pFile);
        logger.info("Properties file successfully read.");

        storage = new Storage();

        if (!storage.create()) {
            return;
        }

        logger.info("SQL Connection created and tested.");

        final String key = settings.getProperty("placeholder.network.unique.key");
        final String decodedKey = new String(BaseEncoding.base64().decode(key));

        placeholderNetwork = new PlaceholderNetwork(key, IDK.PLACEHOLDER_NETWORK, IDK.PLACEHOLDER_NETWORK_SALT);

        if (!placeholderNetwork.isConnected() || !placeholderNetwork.isAuthenticated()) {
            logger.error("Authentication failed, your server key is invalid.");
            Bootloader.exitServer();
            return;
        }

        logger.info("Successfully connected!");

        if (decodedKey.split("-").length != 3) {
            logger.error(IDK.NAME + " has broken down.");
            return;
        }

        try {
            OperationCodes.loadCodes();
            logger.info("OperationCode(s) loaded.");
        } catch (final FileNotFoundException e) {
            logger.error("Couldn't found op-code file.", e);
            return;
        } catch (final IOException e) {
            logger.error("Couldn't read op-code file.", e);
            return;
        }

        try {
            IDK.loadSettings();
            logger.info("GameSetting(s) loaded.");
        } catch (final Exception e) {
            logger.error("Couldn't read the game settings.", e);
            return;
        }

        MessageHandler.loadMessages();
        game = new Game();
        game.getCatalogManager().loadCache();
        pluginManager = new PluginManager();
        pluginManager.load(new File("plugins"));
        game.getBotManager().load();

        if (!Bootloader.placeholderNetwork.loadPlugins()) {
            System.exit(0);
        }

        /**
         * Online-User updater (+ keeps mysql connection up)
         */
        WorkerTasks.initWorkerTasks(1); // initialize worker tasks

        sessionManager = new SessionManager(Integer.valueOf(settings.getProperty("idk.game.maxconns", "2000")));
        listener = new ConnectionListener(settings.getProperty("idk.game.host"), Integer.valueOf(settings
        .getProperty("idk.game.port")));

        if (listener.tryListen()) {
            logger.info("Ready for connections (Debugging " + (IDK.DEBUG ? "enabled" : "disabled") + ").");
            WorkerTasks.addTask(new ServerTask() {

                @Override
                public void run() {
                    final GapList<Entry<String, String>> parameters = new GapList<Entry<String, String>>();
                    if (Bootloader.placeholderNetwork.analyticsPing(parameters)) {
                        Bootloader.getStorage().executeQuery(
                        "UPDATE idk_settings SET `value`='" + Bootloader.getSessionManager().getActiveSessionCount()
                        + "' WHERE `key`='system.user.online'");
                        return;
                    }
                    Bootloader.exitServer();
                }

            }, 0, 60000, WorkerTasks.getSystemExecutor());

            WorkerTasks.addTask(new ServerTask() {
                @Override
                public void run() {
                    if (Bootloader.placeholderNetwork.loadPlugins(false)) {
                        return;
                    }
                    Bootloader.exitServer();
                }
            }, 600000, 600000, WorkerTasks.getSystemExecutor());
        }
    }

    public static long getTimestamp() {
        return System.currentTimeMillis() / 1000L;
    }

    public static Date getDateFromTimestamp(final long timestamp) {
        return new Date(timestamp * 1000);
    }

    public static void exitServer() {
        Bootloader.exitServer(0);
    }

    public static void exitServer(final int status) {
        System.exit(status);
    }

    public static String getHashedString(final byte[] md5Array) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (final NoSuchAlgorithmException e) {
            return null;
        }
        md.update(md5Array);
        final BigInteger hash = new BigInteger(1, md.digest());
        String result = hash.toString(16);
        while (result.length() < 32) { // 40 for SHA-1
            result = "0" + result;
        }
        return result;
    }

    // fields
    private static Settings settings;
    private static ConnectionListener listener;
    private static SessionManager sessionManager;
    private static PluginManager pluginManager;
    private static Storage storage;
    private static PlaceholderNetwork placeholderNetwork;
    private static Game game;

}
