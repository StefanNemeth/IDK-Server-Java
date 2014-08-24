/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.messenger.MessengerBuddy;

import java.util.List;

public class MessengerSearchResultWriter extends MessageWriter {

    public MessengerSearchResultWriter(final List<MessengerBuddy> friends, final List<MessengerBuddy> nonFriends) {
        super(OperationCodes.getOutgoingOpCode("MessengerSearchResult"));
        super.push(friends.size());

        for (final MessengerBuddy buddy : friends) {
            super.push(buddy.getPlayerId());
            super.push(buddy.getPlayerName());
            super.push(buddy.getMission());
            super.push(buddy.isOnline());
            super.push(buddy.isOnline());
            super.push("");
            super.push(true);
            super.push(buddy.isOnline() ? buddy.getAvatar() : "");
            super.push("");
        }

        super.push(nonFriends.size());

        for (final MessengerBuddy buddy : nonFriends) {
            super.push(buddy.getPlayerId());
            super.push(buddy.getPlayerName());
            super.push(buddy.getMission());
            super.push(buddy.isOnline());
            super.push(false);
            super.push("");
            super.push(false);
            super.push(buddy.isOnline() ? buddy.getAvatar() : "");
            super.push("");
        }
    }

}
