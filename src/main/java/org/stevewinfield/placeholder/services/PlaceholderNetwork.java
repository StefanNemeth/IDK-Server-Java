/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.placeholder.services;

import com.google.common.io.BaseEncoding;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

public class PlaceholderNetwork {
    private static final Logger logger = Logger.getLogger(PlaceholderNetwork.class);

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getLastBuildNumber() {
        return lastBuildNumber;
    }

    public boolean needsUpdate() {
        return !IDK.BUILD_NUMBER.equals(lastBuildNumber);
    }

    public boolean isConnected() {
        return connected;
    }

    public PlaceholderNetwork(final String uniqueKey, final byte[] prefixArray, final byte[] saltArray) {
        logger.info("Connecting to Mirror Servers..");
        this.prefix = new String(prefixArray);
        this.salt = new String(saltArray);
        this.uniqueKey = uniqueKey;

        if (this.updateConnectionList()) {
            this.connected = true;
            this.authenticated = this.authenticateIDK();
            return;
        }

        logger.error("Connection failed, try to get in touch with an IDK administrator.");
        Bootloader.exitServer();
    }

    public boolean authenticateIDK() {
        final ServerResult response = this.requestServer(this.authServer, "confirm");

        if (response == null) {
            return false;
        }

        this.authenticated = true;
        return true;
    }

    public boolean loadPlugins() {
        return loadPlugins(true);
    }

    public boolean loadPlugins(final boolean showLog) {
        final ServerResult response = this.requestServer(this.pluginServer, "confirm");

        if (response == null) {
            return false;
        }

        final JSONObject plugins = (JSONObject) response.getResponse().get("plugins");
        int counter = 0;

        for (final Object plugin : plugins.keySet()) {
            if (Bootloader.getPluginManager().addPlugin((String) plugin, new String(BaseEncoding.base64().decode((String) plugins.get(plugin))), showLog)) {
                counter++;
            }
        }
        if (showLog) {
            logger.info(counter + " plugin(s) external loaded from Placeholder Network.");
        }
        return true;

    }

    public boolean updateConnectionList() {
        final ServerResult response = this.requestServer("", "expo-connection");

        if (response == null) {
            return false;
        }

        final JSONObject servers = (JSONObject) response.getResponse().get("servers");

        if (!servers.containsKey("auth") || !servers.containsKey("updates") || !servers.containsKey("plugins") || !servers.containsKey("analytics")) {
            return false;
        }

        this.authServer = (String) servers.get("auth");
        this.updateServer = (String) servers.get("updates");
        this.pluginServer = (String) servers.get("plugins");
        this.analyticsServer = (String) servers.get("analytics");
        return true;
    }

    public ServerResult requestServer(final String serverURL, final String path) {
        return this.requestServer(serverURL, path, new GapList<Entry<String, String>>());
    }

    public ServerResult requestServer(final String serverURL, final String path, final List<Entry<String, String>> parameters) {
        final String randomToken = Bootloader.getHashedString(("rd" + (new Random().nextInt())).getBytes());
        try {
            String parameterString = "";
            if (path.startsWith("confirm")) {
                parameters.add(new AbstractMap.SimpleEntry<>("token", randomToken));
                parameters.add(new AbstractMap.SimpleEntry<>("key", uniqueKey));
                parameters.add(new AbstractMap.SimpleEntry<>("host", InetAddress.getLocalHost().toString()));
            }
            for (final Entry<String, String> parameter : parameters) {
                parameterString += "&".concat(parameter.getKey()).concat("=").concat(parameter.getValue());
            }
            if (parameterString.length() > 0) {
                parameterString = parameterString.substring(1);
            }
            final URL url = new URL("http" + (serverURL.isEmpty() ? "s" : "") + ":".concat("" + SLASH + SLASH).concat(serverURL).concat(!serverURL.isEmpty() ? "." : "").concat(prefix).concat("" + SLASH).concat(path).concat("?").concat(parameterString));
            final URLConnection conn = url.openConnection();
            final BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            final StringBuilder result = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                result.append(inputLine);
            }
            br.close();
            final JSONObject array = (JSONObject) ((new JSONParser()).parse(result.toString()));
            if (!((String) array.get("responseCode")).startsWith("200")) {
                return null;
            }
            final ServerResult serverResult = new ServerResult((String) array.get("responseCode"), (JSONObject) array.get("response"));
            if (path.startsWith("confirm")) {
                final int serverId = Integer.valueOf(serverURL.split("-")[2]);
                final String hash = this.getNetworkHash(this.getAuthenticationData(randomToken));
                if (hash.equals(this.getNetworkHash(IDK.PLACEHOLDER_NETWORK_SALT)) || !hash.equals(serverResult.getResponse().get("hashcheck")) || serverId != (Long) serverResult.getResponse().get("serverId")) {
                    return null;
                }
            }
            return serverResult;
        } catch (final MalformedURLException e) {
            logger.error("MalformedURLException", e);
        } catch (final IOException e) {
            logger.error("IOException", e);
        } catch (final ParseException e) {
            logger.error("ParseException", e);
        }
        return null;
    }

    public boolean analyticsPing(final GapList<Entry<String, String>> parameters) {
        if (this.requestServer(analyticsServer, "confirm", parameters) != null) {
            return true;
        }
        this.connected = false;
        this.authenticated = false;
        return false;
    }

    public String getNetworkHash(final byte[] dig) {
        return Bootloader.getHashedString(dig);
    }

    public byte[] getAuthenticationData(final String randomToken) {
        return uniqueKey.concat(randomToken).concat(salt).getBytes();
    }

    private final char SLASH = 47;
    private final String prefix;
    private final String salt;
    private final String uniqueKey;

    private boolean authenticated;
    private boolean connected;

    private String updateServer;
    private String authServer;
    private String pluginServer;
    private String analyticsServer;
    private String lastBuildNumber;
}
