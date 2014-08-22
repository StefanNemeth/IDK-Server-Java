/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.messenger;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.communication.ISerialize;
import org.stevewinfield.suja.idk.communication.MessageWriter;

public class MessengerRequest implements ISerialize {
    private static Logger logger = Logger.getLogger(MessengerRequest.class);

    // getters
    public int getRequestPlayerId() {
        return requestPlayerId;
    }

    public String getRequestPlayerName() {
        return requestPlayerName;
    }

    public String getRequestPlayerFigure() {
        return requestPlayerFigure;
    }

    public MessengerRequest() {
        this.requestPlayerId = 0;
        this.requestPlayerName = "";
    }

    public void set(final int requestPlayerId, final String requestPlayerName, final String requestPlayerFigure) {
        this.requestPlayerId = requestPlayerId;
        this.requestPlayerName = requestPlayerName;
        this.requestPlayerFigure = requestPlayerFigure;
    }

    public void set(final ResultSet row) {
        try {
            this.requestPlayerId = row.getInt("player_req_id");
            this.requestPlayerName = row.getString("request_playername");
            this.requestPlayerFigure = row.getString("request_playerfigure");
        } catch (final SQLException ex) {
            logger.error("SQL Exception", ex);
        }
    }

    // fields
    private int requestPlayerId;
    private String requestPlayerName;
    private String requestPlayerFigure;

    @Override
    public void serialize(final MessageWriter writer) {
        writer.push(requestPlayerId);
        writer.push(requestPlayerName);
        writer.push(requestPlayerFigure);
    }
}
