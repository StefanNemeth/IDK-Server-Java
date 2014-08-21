/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class FriendRequestDeclineReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;

        reader.readBoolean();
        int amount = reader.readInteger();

        if (amount > 50)
            amount = 50;

        final String updateString = "DELETE FROM player_friends WHERE player_acc_id="
        + session.getPlayerInstance().getInformation().getId();
        String whereString = "";

        for (int i = 0; i < amount; i++) {
            final int playerId = reader.readInteger();
            if (session.getPlayerMessenger().getRequests().containsKey(playerId)) {
                whereString += " OR player_req_id=" + playerId;
                session.getPlayerMessenger().getRequests().remove(playerId);
            }
        }

        if (whereString != "") {
            Bootloader.getStorage().executeQuery(updateString + " AND (" + whereString.substring(4) + ")");
        }
    }

}
