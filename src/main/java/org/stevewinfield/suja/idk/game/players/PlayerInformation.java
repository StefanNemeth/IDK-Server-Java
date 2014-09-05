/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.players;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.levels.Level;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerInformation {
    private static final Logger logger = Logger.getLogger(PlayerInformation.class);

    public static final int MALE_GENDER = 0;
    public static final int FEMALE_GENDER = 1;

    // getters
    public int getId() {
        return this.id;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public String getEmail() {
        return this.email;
    }

    public int getGender() {
        return this.gender;
    }

    public String getMission() {
        return this.mission;
    }

    public Level getLevel() {
        return this.level;
    }

    public int getCreditsBalance() {
        return this.creditsBalance;
    }

    public int getPixelsBalance() {
        return this.pixelsBalance;
    }

    public int getShellsBalance() {
        return this.shellsBalance;
    }

    public int getHomeRoom() {
        return this.homeRoom;
    }

    public int getRespectPoints() {
        return this.respectPoints;
    }

    public int getAvailableRespects() {
        return this.availableRespects;
    }

    public int getAvailableScratches() {
        return this.availableScratches;
    }

    public int getScore() {
        return this.score;
    }

    public boolean isStreamEnabled() {
        return this.streamEnabled;
    }

    public boolean canTrade() {
        return this.trade;
    }

    public int getTotalCFHS() {
        return cfhs;
    }

    public int getAbusiveCFHS() {
        return abusiveCFHS;
    }

    public int getCautions() {
        return cautions;
    }

    public int getBans() {
        return bans;
    }

    public int getRegisteredTimestamp() {
        return registeredTimestamp;
    }

    public int getLastLoginTimestamp() {
        return lastLoginTimestamp;
    }

    public int getLastUpdate() {
        return lastUpdate;
    }

    public PlayerInformation() {
        this.playerName = "Unknown";
        this.avatar = "hr-893-45.hd-180-2.ch-210-66.lg-270-82.sh-300-91.wa-2007-.ri-1-";
        this.email = "unknown@sulake.com";
        this.mission = "I'm unknown, and you?";
        this.level = new Level();
    }

    public void set(final ResultSet row) throws SQLException {
        this.id = row.getInt("id");
        this.playerName = row.getString("nickname");
        this.email = row.getString("email");
        this.avatar = row.getString("figurecode");
        this.gender = row.getString("gender").toUpperCase().equals("F") ? PlayerInformation.FEMALE_GENDER : PlayerInformation.MALE_GENDER;
        this.mission = row.getString("motto");
        this.level = Bootloader.getGame().getLevelManager().getLevel(row.getInt("level"));
        this.creditsBalance = row.getInt("credits_balance");
        this.pixelsBalance = row.getInt("pixels_balance");
        this.shellsBalance = row.getInt("shells_balance");
        this.homeRoom = row.getInt("home_room");
        this.respectPoints = row.getInt("respect_points");
        this.availableRespects = row.getInt("available_respects");
        this.availableScratches = row.getInt("available_scrachtes");
        this.score = row.getInt("score");
        this.streamEnabled = row.getInt("stream_enabled") == 1;
        this.trade = row.getInt("can_trade") == 1;
        this.cfhs = row.getInt("total_cfhs");
        this.abusiveCFHS = row.getInt("abusive_cfhs");
        this.cautions = row.getInt("cautions");
        this.bans = row.getInt("bans");
        this.registeredTimestamp = row.getInt("registered_timestamp");
        this.lastLoginTimestamp = row.getInt("last_login_timestamp");
        this.lastUpdate = row.getInt("last_update");
    }

    public void addCredits(final int credits) {
        this.creditsBalance += credits;
    }

    public void setPixels(final int pixels) {
        this.pixelsBalance += pixels;
    }

    public void setFigure(final String avatar, final String gender) {
        this.avatar = avatar;
        this.gender = gender.equals("f") ? PlayerInformation.FEMALE_GENDER : PlayerInformation.MALE_GENDER;
    }

    public void setMotto(final String motto) {
        this.mission = motto;
    }

    public void setStreamEnabled(final boolean enabled) {
        this.streamEnabled = enabled;
    }

    public void setShells(final int shells) {
        this.shellsBalance += shells;
    }

    public void setAvailableRespects(final int respects) {
        this.availableRespects = respects;
    }

    public void incrementRespectPoints() {
        this.respectPoints++;
        Bootloader.getStorage().executeQuery("UPDATE players SET respect_points=" + this.respectPoints + " WHERE id=" + this.id);
    }

    public void decrementAvailableRespects() {
        this.availableRespects--;
        Bootloader.getStorage().executeQuery("UPDATE players SET available_respects=" + this.availableRespects + " WHERE id=" + this.id);
    }

    public void updateCurrencies() {
        Bootloader.getStorage().executeQuery("UPDATE players SET credits_balance=" + this.creditsBalance + ", pixels_balance=" + this.pixelsBalance + ", shells_balance=" + this.shellsBalance + " WHERE id=" + this.id);
    }

    public void setHomeRoom(final int home) {
        this.homeRoom = home;
        Bootloader.getStorage().executeQuery("UPDATE players SET home_room=" + home + " WHERE id=" + this.id);
    }

    public void setLastLoginTimestamp(final int timestamp) {
        this.lastLoginTimestamp = timestamp;
    }

    // fields
    private int id;
    private String playerName;
    private String avatar;
    private String email;
    private int gender;
    private String mission;
    private Level level;
    private int creditsBalance;
    private int pixelsBalance;
    private int shellsBalance;
    private int homeRoom;
    private int respectPoints;
    private int availableRespects;
    private int availableScratches;
    private int score;
    private boolean streamEnabled;
    private boolean trade;
    private int cfhs;
    private int abusiveCFHS;
    private int cautions;
    private int bans;
    private int registeredTimestamp;
    private int lastLoginTimestamp;
    private int lastUpdate;
}
