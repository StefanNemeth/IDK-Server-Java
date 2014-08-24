/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.navigator;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.navigator.writers.NavigatorOfficialRoomsWriter;
import org.stevewinfield.suja.idk.game.rooms.RoomInformation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class NavigatorListManager {
    private static final Logger logger = Logger.getLogger(NavigatorListManager.class);

    public MessageWriter getOfficialItemsWriter() {
        return new NavigatorOfficialRoomsWriter(this.officialItems);
    }

    public int getSearchCategory(String query) {
        return this.searchCategories.containsKey((query = query.toLowerCase())) ? this.searchCategories.get(query) : -1;
    }

    public NavigatorListManager(final int amount) {
        this.officialItems = new GapList<>();
        this.searchCategories = new ConcurrentHashMap<>();
        try {
            ResultSet items = Bootloader.getStorage().queryParams("SELECT * FROM official_items").executeQuery();
            while (items.next()) {
                final OfficialItem item = new OfficialItem();
                item.set(items);
                this.officialItems.add(item);
            }
            items = Bootloader.getStorage().queryParams("SELECT * FROM room_search_categories").executeQuery();
            while (items.next()) {
                this.searchCategories.put(items.getString("search_query").toLowerCase(), items.getInt("category_id"));
            }
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
        logger.info(officialItems.size() + " Offical Items loaded.");
        logger.info(searchCategories.size() + " Search Categories loaded.");
        this.navigatorLists = new ConcurrentHashMap<>();
        this.navigatorLists.put(-1, new NavigatorList(-1));
        for (int i = 1; i <= amount; i++) {
            this.navigatorLists.put(i, new NavigatorList(i));
        }
    }

    public MessageWriter getListWriter(final int index) {
        return navigatorLists.containsKey(index) ? navigatorLists.get(index).getListWriter() : null;
    }

    public void setRoom(final RoomInformation room, final int playersTotal, final boolean update) {
        this.navigatorLists.get(-1).setRoom(room, playersTotal, update);
        if (this.navigatorLists.containsKey(room.getCategoryId())) {
            this.navigatorLists.get(room.getCategoryId()).setRoom(room, playersTotal, update);
        }
    }

    public void setRoom(final RoomInformation room, final int playersTotal) {
        this.setRoom(room, playersTotal, false);
    }

    public ConcurrentHashMap<Integer, NavigatorList> getNavigatorLists() {
        return this.navigatorLists;
    }

    // fields
    private final ConcurrentHashMap<Integer, NavigatorList> navigatorLists;
    private final ConcurrentHashMap<String, Integer> searchCategories;
    private final GapList<OfficialItem> officialItems;
}
