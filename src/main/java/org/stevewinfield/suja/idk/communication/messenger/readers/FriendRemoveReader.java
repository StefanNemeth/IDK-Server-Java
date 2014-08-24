/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.QueuedMessageWriter;
import org.stevewinfield.suja.idk.communication.messenger.writers.MessengerUpdateListWriter;
import org.stevewinfield.suja.idk.game.messenger.MessengerBuddy;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class FriendRemoveReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        int amount = reader.readInteger();

        if (amount > 50) {
            amount = 50;
        }

        final MessengerBuddy sessionBuddy = new MessengerBuddy();
        sessionBuddy.set(session.getPlayerInstance().getInformation().getId(), session.getPlayerInstance().getInformation().getPlayerName(), session.getPlayerInstance().getInformation().getAvatar(), session.getPlayerInstance().getInformation().getMission());

        final QueuedMessageWriter queue = new QueuedMessageWriter();
        final MessageWriter broadcastOldFriend = new MessengerUpdateListWriter(sessionBuddy, -1);

        final String query = "DELETE FROM player_friends WHERE ";
        String where = "";

        for (int i = 0; i < amount; i++) {
            final int playerId = reader.readInteger();
            if (session.getPlayerMessenger().getBuddies().containsKey(playerId)) {
                where += " OR ((player_req_id=" + session.getPlayerInstance().getInformation().getId() + " AND player_acc_id=" + playerId + ") OR (player_req_id=" + playerId + " AND player_acc_id=" + session.getPlayerInstance().getInformation().getId() + "))";
                final MessengerBuddy buddy = session.getPlayerMessenger().getBuddies().get(playerId);
                session.getPlayerMessenger().getBuddies().remove(playerId);
                if (buddy.getSession() != null) {
                    session.getPlayerMessenger().getOnlineBuddies().remove(playerId);
                    buddy.getSession().getPlayerMessenger().getBuddies().remove(session.getPlayerInstance().getInformation().getId());
                    buddy.getSession().getPlayerMessenger().getOnlineBuddies().remove(session.getPlayerInstance().getInformation().getId());
                    buddy.getSession().writeMessage(broadcastOldFriend);
                }
                queue.push(new MessengerUpdateListWriter(buddy, -1));
            }
        }

        if (where != "") {
            Bootloader.getStorage().executeQuery(query + where.substring(4));
        }

        session.writeMessage(queue);
    }

}
