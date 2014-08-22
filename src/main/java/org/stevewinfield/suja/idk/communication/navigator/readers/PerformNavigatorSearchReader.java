/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.navigator.readers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.InputFilter;
import org.stevewinfield.suja.idk.collections.NavigatorListHelper;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.navigator.writers.NavigatorListRoomsWriter;
import org.stevewinfield.suja.idk.game.rooms.RoomInformation;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class PerformNavigatorSearchReader implements IMessageReader {
    private static Logger logger = Logger.getLogger(PerformNavigatorSearchReader.class);

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;

        final ConcurrentHashMap<Integer, RoomInformation> results = new ConcurrentHashMap<Integer, RoomInformation>();
        List<RoomInformation> sortedResult = new GapList<RoomInformation>();
        String query = InputFilter.filterString(reader.readUTF()).trim().toLowerCase();

        if (query.length() > 64)
            query = query.substring(0, 64);

        if (query.length() > 0) {
            final int searchCategoryId = Bootloader.getGame().getNavigatorListManager().getSearchCategory(query);
            for (final RoomInstance instance : Bootloader.getGame().getRoomManager().getLoadedRoomInstances()) {
                if (!query.startsWith("owner:")) {
                    if (!instance.getInformation().getOwnerName().toLowerCase().equals(query)
                    && (!instance.getInformation().getName().toLowerCase().startsWith(query))
                    && (searchCategoryId == -1 || instance.getInformation().getCategoryId() == searchCategoryId)) {
                        final String[] tags = instance.getInformation().getSearchableTags();
                        boolean found = false;
                        for (final String tag : tags) {
                            if (tag.toLowerCase().equals(query)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found)
                            continue;
                    }
                } else if (!instance.getInformation().getOwnerName().toLowerCase().equals(query.substring(6))) {
                    continue;
                }
                results.put(instance.getInformation().getId(), instance.getInformation());
            }

            if (results.size() < 50) {
                final PreparedStatement searchResults = Bootloader
                .getStorage()
                .queryParams(
                "SELECT rooms.*, nickname FROM rooms, players WHERE players.id=owner_id AND (nickname = ? "
                + (!query.startsWith("owner:") ? ("OR name LIKE ? OR tags LIKE ? OR tags LIKE ? OR tags = ?" + (searchCategoryId > -1 ? " OR category_id="
                + searchCategoryId
                : ""))
                : "") + ") LIMIT 50");
                try {
                    searchResults.setString(1, query.startsWith("owner:") ? query.substring(6) : query);
                    if (!query.startsWith("owner:")) {
                        searchResults.setString(2, query + "%");
                        searchResults.setString(3, query.replace(",", "") + ",%");
                        searchResults.setString(4, "%," + query.replace(",", ""));
                        searchResults.setString(5, query.replace(",", ""));
                    }
                    final ResultSet set = searchResults.executeQuery();
                    while (set.next()) {
                        if (results.containsKey(set.getInt("id")))
                            continue;
                        final RoomInformation information = new RoomInformation();
                        information.set(set);
                        results.put(information.getId(), information);
                    }
                } catch (final SQLException e) {
                    logger.error("SQL Exception", e);
                    return;
                }
            }

            sortedResult = new GapList<RoomInformation>(results.values());
            Collections.sort(sortedResult, new NavigatorListHelper(query));

            if (sortedResult.size() > 50)
                sortedResult = sortedResult.subList(0, sortedResult.size() - (sortedResult.size() - 50));
        }

        session.writeMessage(new NavigatorListRoomsWriter(1, 9, query, sortedResult));
    }

}
