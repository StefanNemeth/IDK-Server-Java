/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.navigator.writers.RoomCategoriesWriter;

public class RoomManager {
    private static Logger logger = Logger.getLogger(RoomManager.class);

    public ConcurrentHashMap<Integer, Integer> getTeleporterCache() {
        return this.teleporterCache;
    }

    public RoomManager() {
        this.roomModels = new ConcurrentHashMap<String, RoomModel>();
        this.roomCategories = new ConcurrentHashMap<Integer, RoomCategory>();
        this.teleporterCache = new ConcurrentHashMap<Integer, Integer>();
        try {
            final ResultSet models = Bootloader.getStorage().queryParams("SELECT * FROM room_models").executeQuery();
            while (models.next()) {
                final RoomModel model = new RoomModel();
                model.set(models);
                roomModels.put(models.getString("name"), model);
            }
            logger.info(roomModels.size() + " Room Model(s) loaded.");
            final ResultSet categories = Bootloader.getStorage().queryParams("SELECT * FROM room_categories ORDER BY id ASC")
            .executeQuery();
            while (categories.next()) {
                final RoomCategory category = new RoomCategory();
                category.set(categories);
                roomCategories.put(categories.getInt("id"), category);
            }
            final SortedSet<Integer> keys = new TreeSet<Integer>(roomCategories.keySet());
            final GapList<RoomCategory> cats = new GapList<RoomCategory>();
            for (final int key : keys) {
                cats.add(roomCategories.get(key));
            }
            this.cachedCategoryWriter = new RoomCategoriesWriter(cats);
            logger.info(roomCategories.size() + " Room Categories loaded.");
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
        this.loadedRoomInstances = new ConcurrentHashMap<Integer, RoomInstance>();
    }

    public RoomModel getRoomModel(final String modelId) {
        return this.roomModels.get(modelId);
    }

    public RoomInstance getLoadedRoomInstance(final int roomId) {
        return this.loadedRoomInstances.containsKey(roomId) ? this.loadedRoomInstances.get(roomId) : null;
    }

    public Collection<RoomInstance> getLoadedRoomInstances() {
        return this.loadedRoomInstances.values();
    }

    public void removeLoadedRoomInstance(final int roomId) {
        this.loadedRoomInstances.remove(roomId);
    }

    public RoomInformation getRoomInformation(final int roomId) {
        if (this.loadedRoomInstances.containsKey(roomId)) {
            return this.loadedRoomInstances.get(roomId).getInformation();
        }
        RoomInformation info = null;
        try {
            final ResultSet row = Bootloader
            .getStorage()
            .queryParams(
            "SELECT rooms.*, nickname FROM rooms, players WHERE rooms.id=" + roomId
            + " AND players.id=owner_id ORDER BY name ASC LIMIT 1").executeQuery();
            if (row.next()) {
                info = new RoomInformation();
                info.set(row);
            }
            row.close();
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
        return info;
    }

    public RoomInstance loadRoomInstance(final int roomId) {
        if (this.loadedRoomInstances.containsKey(roomId)) {
            return this.loadedRoomInstances.get(roomId);
        }
        RoomInstance instance = null;
        try {
            final ResultSet row = Bootloader
            .getStorage()
            .queryParams(
            "SELECT rooms.*, nickname FROM rooms, players WHERE rooms.id=" + roomId
            + " AND players.id=owner_id ORDER BY name ASC LIMIT 1").executeQuery();
            if (row.next()) {
                instance = new RoomInstance();
                instance.load(row);
                this.loadedRoomInstances.put(roomId, instance);
            }
            row.close();
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
        return instance;
    }

    public void removeInstance(final int roomId) {
        this.loadedRoomInstances.remove(roomId);
    }

    public MessageWriter getCachedCategoryWriter() {
        return cachedCategoryWriter;
    }

    public RoomCategory getRoomCategory(final int id) {
        return this.roomCategories.containsKey(id) ? this.roomCategories.get(id) : null;
    }

    public int getRoomCategoryCount() {
        return this.roomCategories.size();
    }

    private final ConcurrentHashMap<Integer, RoomInstance> loadedRoomInstances;
    private final ConcurrentHashMap<Integer, RoomCategory> roomCategories;
    private final ConcurrentHashMap<String, RoomModel> roomModels;
    private final ConcurrentHashMap<Integer, Integer> teleporterCache;
    private MessageWriter cachedCategoryWriter;

}
