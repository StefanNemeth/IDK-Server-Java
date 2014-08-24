/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.moderation;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ModerationPresetMessage {
    private static Logger logger = Logger.getLogger(ModerationPresetMessage.class);

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public ModerationPresetMessage() {
        this.message = "";
    }

    public void set(final ResultSet row) {
        try {
            this.id = row.getInt("id");
            this.type = row.getString("type").equals("room") ? ModerationPresetMessageType.ROOM_MESSAGE : ModerationPresetMessageType.PLAYER_MESSAGE;
            this.message = row.getString("message");
        } catch (final SQLException ex) {
            logger.error("SQL Exception", ex);
        }
    }

    // fields
    private int id;
    private int type;
    private String message;
}
