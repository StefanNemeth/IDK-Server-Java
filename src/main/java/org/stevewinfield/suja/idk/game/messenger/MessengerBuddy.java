/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.messenger;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.ISerialize;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class MessengerBuddy implements ISerialize {
    private static Logger logger = Logger.getLogger(MessengerBuddy.class);

    // getters
    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getMission() {
        return mission;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean isInRoom() {
        return inRoom;
    }

    public Session getSession() {
        return session;
    }

    public MessengerBuddy() {
        this.playerId = 0;
        this.playerName = "";
        this.avatar = "";
        this.mission = "";
        this.lastOnline = "";
        this.online = false;
    }

    public void setSession(final Session session) {
        this.session = session;
        this.online = this.session != null;
        this.inRoom = online && this.session.isInRoom();
    }

    public void update() {
        this.inRoom = online && this.session.isInRoom();
    }

    public void set(final ResultSet row) {
        try {
            this.playerId = row.getInt("id");
            this.playerName = row.getString("nickname");
            this.avatar = row.getString("figurecode");
            this.mission = row.getString("motto");
            this.lastOnline = "";
            this.session = Bootloader.getSessionManager().getAuthenticatedSession(playerId);
            this.online = this.session != null;
            this.inRoom = online && this.session.isInRoom();
        } catch (final SQLException ex) {
            logger.error("SQL Exception", ex);
        }
    }

    public void set(final int playerId, final String playerName, final String avatar, final String mission) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.avatar = avatar;
        this.mission = mission;
        this.lastOnline = "";
        this.session = Bootloader.getSessionManager().getAuthenticatedSession(playerId);
        this.online = this.session != null;
        this.inRoom = online && this.session.isInRoom();
    }

    // fields
    private int playerId;
    private String playerName;
    private String avatar;
    private String mission;
    private String lastOnline;
    private boolean online;
    private Session session;
    private boolean inRoom;

    @Override
    public void serialize(final MessageWriter writer) {
        writer.push(this.playerId);
        writer.push(this.playerName);
        writer.push(true);
        writer.push(this.online);
        writer.push(this.inRoom);
        writer.push(this.online ? this.avatar : ""); // if on -> figure
        writer.push(false);
        writer.push(this.online ? this.mission : ""); // if on -> motto
        writer.push(this.online ? "" : this.lastOnline);
        writer.push("");
        writer.push("");
    }
}
