/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk;

import java.sql.ResultSet;

public class IDK {

    /**
     * Constants
     */
    public static String BUILD_NUMBER;
    public static String VERSION;
    public static String NAME;

    public static final String AUTHOR = "STEVE WINFIELD";
    public static final String XML_POLICY = "<?xml version=\"1.0\"?>\r\n" + "<!DOCTYPE cross-domain-policy SYSTEM \"/xml/dtds/cross-domain-policy.dtd\">\r\n" + "<cross-domain-policy>\r\n" + "<allow-access-from domain=\"*\" to-ports=\"*\" />\r\n" + "</cross-domain-policy>\0";

    public static void loadSettings() throws Exception {
        final ResultSet settings = Bootloader.getStorage().queryParams("SELECT * FROM idk_settings").executeQuery();
        while (settings.next()) {
            switch (settings.getString("key")) {
                case "system.debug":
                    DEBUG = Integer.valueOf(settings.getString("value")) == 1;
                    continue;
                case "nav.max.favorites":
                    NAV_MAX_FAVORITES = Integer.valueOf(settings.getString("value"));
                    continue;
                case "nav.max.rooms.per.player":
                    NAV_MAX_ROOMS_PER_PLAYER = Integer.valueOf(settings.getString("value"));
                    continue;
                case "cata.fireworks.charges.amount":
                    CATA_FIREWORKS_CHARGES_AMOUNT = Integer.valueOf(settings.getString("value"));
                    continue;
                case "cata.fireworks.charges.credits":
                    CATA_FIREWORKS_CHARGES_CREDITS = Integer.valueOf(settings.getString("value"));
                    continue;
                case "cata.fireworks.charges.pixels":
                    CATA_FIREWORKS_CHARGES_PIXELS = Integer.valueOf(settings.getString("value"));
                    continue;
                case "cata.club.offer.price.regular":
                    CATA_CLUB_OFFER_PRICE_REGULAR = Integer.valueOf(settings.getString("value"));
                    continue;
                case "cata.club.offer.price.now":
                    CATA_CLUB_OFFER_PRICE_NOW = Integer.valueOf(settings.getString("value"));
                    continue;
                case "friendstream.avatar.url":
                    FRIENDSTREAM_AVATAR_URL = settings.getString("value");
                    continue;
                case "bb.timer.default.seconds":
                    BB_DEFAULT_TIMER_SECONDS = Integer.valueOf(settings.getString("value"));
                    continue;
                case "cata.gifts.modern.enabled":
                    CATA_GIFTS_MODERN_ENABLED = Integer.valueOf(settings.getString("value")) == 1;
                    continue;
                case "cata.gifts.modern.price":
                    CATA_GIFTS_MODERN_PRICE = Integer.valueOf(settings.getString("value"));
                    continue;
                case "cata.gifts.box.count":
                    CATA_GIFTS_BOX_COUNT = Integer.valueOf(settings.getString("value"));
                    continue;
                case "cata.gifts.ribbon.count":
                    CATA_GIFTS_RIBBON_COUNT = Integer.valueOf(settings.getString("value"));
                    continue;
                case "cata.recycler.enabled":
                    CATA_RECYCLER_ENABLED = Integer.valueOf(settings.getString("value")) == 1;
                    continue;
                case "system.date.format":
                    SYSTEM_DATE_FORMAT = settings.getString("value");
                    continue;
                case "system.time.format":
                    SYSTEM_TIME_FORMAT = settings.getString("value");
                    continue;
                case "cata.recycler.box.id":
                    CATA_RECYCLER_BOX_ID = Integer.valueOf(settings.getString("value"));
                    continue;
                case "cata.stickies.max.count":
                    CATA_STICKIES_MAX_COUNT = Integer.valueOf(settings.getString("value"));
                    continue;
                case "bots.shout.responses":
                    BOTS_SHOUT_RESPONSES = settings.getString("value").split(";");
                    continue;
                case "room.max.walk.altitude.difference":
                    ROOM_MAX_WALK_ALTITUDE_DIFFERENCE = Integer.valueOf(settings.getString("value"));
                    continue;
                case "cata.rollers.roll.delay":
                    CATA_ROLLERS_ROLL_DELAY = Integer.valueOf(settings.getString("value"));
                    continue;
                case "cata.rollers.max.count":
                    CATA_ROLLERS_MAX_COUNT = Integer.valueOf(settings.getString("value"));
                    continue;
                case "cata.avatar.effects.duration":
                    CATA_AVATAR_EFFECTS_DURATION = Integer.valueOf(settings.getString("value"));
                    continue;
            }
        }
    }

    /**
     * Game Settings
     */
    public static boolean DEBUG = false;
    public static String SYSTEM_DATE_FORMAT = "yyyy-MM-dd";
    public static String SYSTEM_TIME_FORMAT = "HH:mm";
    public static String SYSTEM_PLUGINS_PATH = "plugins";
    public static String[] BOTS_SHOUT_RESPONSES = new String[]{"Come on.. I didn't understand you.", "What have you said?", "Ergh, what..? You're too quiet."};
    public static int NAV_MAX_FAVORITES = 20;
    public static int NAV_MAX_ROOMS_PER_PLAYER = 20;
    public static int CATA_RECYCLER_BOX_ID = 1445;
    public static boolean CATA_RECYCLER_ENABLED = true;
    public static boolean CATA_GIFTS_MODERN_ENABLED = true;
    public static int CATA_GIFTS_MODERN_PRICE = 1;
    public static int CATA_GIFTS_BOX_COUNT = 7;
    public static int CATA_GIFTS_RIBBON_COUNT = 11;
    public static int CATA_FIREWORKS_CHARGES_AMOUNT = 10;
    public static int CATA_FIREWORKS_CHARGES_CREDITS = 0;
    public static int CATA_FIREWORKS_CHARGES_PIXELS = 200;
    public static int CATA_CLUB_OFFER_PRICE_REGULAR = 25;
    public static int CATA_CLUB_OFFER_PRICE_NOW = 15;
    public static int CATA_STICKIES_MAX_COUNT = 200;
    public static int CATA_ROLLERS_MAX_COUNT = 100;
    public static int CATA_ROLLERS_ROLL_DELAY = 1;
    public static int BB_DEFAULT_TIMER_SECONDS = 60;
    public static int ROOM_MAX_WALK_ALTITUDE_DIFFERENCE = 2;
    public static int CATA_AVATAR_EFFECTS_DURATION = 3600;
    public static String FRIENDSTREAM_AVATAR_URL = "http://127.0.0.1/habbo-imaging/avatar.png?gesture=sml&figure=%s";

    /**
     * Network Settings
     */
    public static final byte[] PLACEHOLDER_NETWORK = new byte[]{112, 108, 97, 99, 101, 104, 111, 108, 100, 101, 114, 45, 110, 101, 116, 119, 111, 114, 107, 46, 97, 112, 112, 115, 112, 111, 116, 46, 99, 111, 109};
    public static final byte[] PLACEHOLDER_NETWORK_SALT = new byte[]{104, 111, 108, 100, 101, 114, 45, 110, 101, 116, 119, 111, 114, 107, 46, 97, 112, 112, 99, 101, 104, 111, 115, 112, 111, 111, 114};
}
