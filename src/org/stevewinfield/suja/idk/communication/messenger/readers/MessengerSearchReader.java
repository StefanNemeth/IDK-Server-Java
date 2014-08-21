/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.readers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.InputFilter;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.messenger.writers.MessengerSearchResultWriter;
import org.stevewinfield.suja.idk.game.messenger.MessengerBuddy;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class MessengerSearchReader implements IMessageReader {
    private static Logger logger = Logger.getLogger(MessengerSearchReader.class);

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;

        String query = InputFilter.filterString(reader.readUTF().replace('%', ' '));

        if (query.length() > 100)
            query = query.substring(0, 100);

        List<MessengerBuddy> searchResults = new GapList<MessengerBuddy>();

        if (query.length() > 1) {
            try {
                final PreparedStatement searchSth = Bootloader.getStorage().queryParams(
                "SELECT id, nickname, figurecode, motto FROM players WHERE nickname LIKE ? LIMIT 50");
                searchSth.setString(1, query + "%");
                final ResultSet row = searchSth.executeQuery();

                while (row.next()) {
                    final MessengerBuddy buddy = new MessengerBuddy();
                    buddy.set(row);
                    searchResults.add(buddy);
                }
                row.close();
            } catch (final SQLException e) {
                logger.error("SQL Exception", e);
            }
        }

        final List<MessengerBuddy> nonFriends = new GapList<MessengerBuddy>();
        final List<MessengerBuddy> friends = new GapList<MessengerBuddy>();

        for (final MessengerBuddy buddy : searchResults) {
            if (session.getPlayerMessenger().getBuddies().containsKey(buddy.getPlayerId())) {
                friends.add(buddy);
            } else {
                nonFriends.add(buddy);
            }
        }

        session.writeMessage(new MessengerSearchResultWriter(friends, nonFriends));

        searchResults.clear();
        searchResults = null;
    }

}
