/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.readers;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.InputFilter;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.messenger.writers.MessengerRequestNoficiationWriter;
import org.stevewinfield.suja.idk.game.messenger.MessengerRequest;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SendFriendRequestReader implements IMessageReader {
    private static Logger logger = Logger.getLogger(SendFriendRequestReader.class);

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        String requestName = InputFilter.filterString(reader.readUTF());

        if (requestName.length() > 100) {
            requestName = requestName.substring(0, 100);
        }

        try {
            final PreparedStatement sthId = Bootloader.getStorage().queryParams("SELECT id FROM players WHERE nickname=?");
            sthId.setString(1, requestName);

            final ResultSet row = sthId.executeQuery();

            if (!row.next()) {
                return;
            }

            final int requestId = row.getInt("id");

            if (requestId < 1 || requestId == session.getPlayerInstance().getInformation().getId() || session.getPlayerMessenger().getBuddies().containsKey(requestId) || session.getPlayerMessenger().getRequests().containsKey(requestId) || Bootloader.getStorage().readInteger("SELECT COUNT(id) FROM player_friends WHERE player_req_id=" + session.getPlayerInstance().getInformation().getId() + " AND player_acc_id=" + requestId) > 0) {
                return;
            }

            final PreparedStatement sthCrt = Bootloader.getStorage().queryParams("INSERT INTO player_friends (player_req_id, player_acc_id, request_playername, request_playerfigure, is_request) VALUES (" + session.getPlayerInstance().getInformation().getId() + ", " + requestId + ", ?, ?, 1)");
            sthCrt.setString(1, session.getPlayerInstance().getInformation().getPlayerName());
            sthCrt.setString(2, session.getPlayerInstance().getInformation().getAvatar());
            sthCrt.execute();

            final Session requestSession = Bootloader.getSessionManager().getAuthenticatedSession(requestId);

            if (requestSession != null && requestSession.isAuthenticated()) {
                final MessengerRequest request = new MessengerRequest();
                request.set(session.getPlayerInstance().getInformation().getId(), session.getPlayerInstance().getInformation().getPlayerName(), session.getPlayerInstance().getInformation().getAvatar());
                requestSession.getPlayerMessenger().getRequests().put(session.getPlayerInstance().getInformation().getId(), request);
                requestSession.writeMessage(new MessengerRequestNoficiationWriter(session.getPlayerInstance().getInformation().getId(), session.getPlayerInstance().getInformation().getPlayerName(), session.getPlayerInstance().getInformation().getAvatar()));
            }
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }

    }

}
