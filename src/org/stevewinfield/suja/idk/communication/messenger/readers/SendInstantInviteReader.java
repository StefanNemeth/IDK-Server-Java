/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.readers;

import java.util.List;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.InputFilter;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class SendInstantInviteReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;

        final int amount = reader.readInteger();
        final List<Integer> playerIds = new GapList<Integer>();

        for (int i = 0; i < amount; i++)
            playerIds.add(reader.readInteger());

        String message = InputFilter.filterString(reader.readUTF(), false);

        if (message.length() > 121) {
            message = message.substring(0, 121);
        }

        for (final int playerId : playerIds) {
            if (session.getPlayerMessenger().getOnlineBuddies().containsKey(playerId)) {
                session.getPlayerMessenger().getOnlineBuddies().get(playerId).getSession().getPlayerMessenger()
                .onInviteReceived(session.getPlayerInstance().getInformation().getId(), message);
            }
        }
    }

}
