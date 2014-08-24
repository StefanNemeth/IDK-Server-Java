/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.inventory;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.catalog.writers.CatalogPurchaseResultWriter;
import org.stevewinfield.suja.idk.communication.inventory.writers.InventoryItemRemovedWriter;
import org.stevewinfield.suja.idk.communication.inventory.writers.InventoryNewItemsWriter;
import org.stevewinfield.suja.idk.communication.inventory.writers.UpdatePlayerInventoryWriter;
import org.stevewinfield.suja.idk.game.catalog.CatalogItem;
import org.stevewinfield.suja.idk.game.furnitures.Furniture;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureType;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.wired.WiredManager;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerInventory {
    private static final Logger logger = Logger.getLogger(PlayerInventory.class);

    public Collection<PlayerItem> getItems() {
        return this.items.values();
    }

    public GapList<PlayerItem> getFloorItems() {
        final GapList<PlayerItem> list = new GapList<>();
        for (final PlayerItem item : this.items.values()) {
            if (!item.getBase().getType().equals(FurnitureType.WALL)) {
                list.add(item);
            }
        }
        return list;
    }

    public GapList<PlayerItem> getWallItems() {
        final GapList<PlayerItem> list = new GapList<>();
        for (final PlayerItem item : this.items.values()) {
            if (item.getBase().getType().equals(FurnitureType.WALL)) {
                list.add(item);
            }
        }
        return list;
    }

    public PlayerInventory() {
        this.items = new ConcurrentHashMap<>();
        this.itemsToAdd = new GapList<>();
        this.itemsToRemove = new GapList<>();
        this.itemsToUpdate = new GapList<>();
    }

    public boolean hasItem(final int itemId) {
        return this.items.containsKey(itemId);
    }

    public PlayerItem getItem(final int itemId) {
        return this.items.get(itemId);
    }

    public void removeItem(final int itemId, final Session session) {
        this.removeItem(itemId, session, true);
    }

    public void removeItem(final int itemId, final Session session, final boolean removeDb) {
        this.items.remove(itemId);
        if (itemsToAdd.contains(itemId)) {
            itemsToAdd.remove(new Integer(itemId));
        }
        if (itemsToUpdate.contains(itemId)) {
            itemsToUpdate.remove(new Integer(itemId));
        }
        if (removeDb) {
            itemsToRemove.add(itemId);
        }
        if (session != null) {
            session.writeMessage(new InventoryItemRemovedWriter(itemId));
        }
    }

    public int addItem(final Furniture baseItem, final Session session, final int amount, final String flags, final String secondaryData) {
        return this.addItem(baseItem, session, amount, flags, null, null);
    }

    public int addItem(final Furniture baseItem, final Session session, final int amount, String flags, final String secondaryData, final CatalogItem catalogItem) {
        final String insert = "INSERT INTO items (base_item, special_interactor) VALUES (" + baseItem.getId() + ", -1)";

        if (flags.length() < 1) {
            switch (baseItem.getInteractor()) {
                default:
                    flags = "0";
                    break;
                case FurnitureInteractor.POST_IT:
                    flags = "FFFF33" + (char) 10 + "" + (char) 10 + session.getPlayerInstance().getInformation().getId();
                    break;
                case FurnitureInteractor.BATTLE_BANZAI_TIMER:
                    flags = IDK.BB_DEFAULT_TIMER_SECONDS + "";
                    break;
            }
        }

        switch (baseItem.getInteractor()) {
            case FurnitureInteractor.WALLPAPER:
            case FurnitureInteractor.FLOOR:
            case FurnitureInteractor.LANDSCAPE:
                if (secondaryData != null) {
                    flags = secondaryData;
                }
                break;
        }

        final List<PlayerItem> floorItems = new GapList<>();
        final List<PlayerItem> wallItems = new GapList<>();

        if (WiredManager.isWiredItem(baseItem)) {
            flags = "";
        }

        int returnValue = -1;

        for (int i = amount; i > 0; i--) {
            Bootloader.getStorage().executeQuery(insert);
            final int id = Bootloader.getStorage().readLastId("items");
            if (id < 1) {
                continue;
            }
            if (returnValue < 0) {
                returnValue = id;
            }
            String termFlags = flags;
            if (flags.equals("0") && baseItem.getInteractor() == FurnitureInteractor.TELEPORTER) {
                termFlags = this.addItem(baseItem, session, 1, id + "", null) + "";
            }
            final PlayerItem playerItem = new PlayerItem(id, baseItem, termFlags, baseItem.getInteractor());
            if (!playerItem.getBase().getType().equals(FurnitureType.WALL)) {
                floorItems.add(playerItem);
            } else {
                wallItems.add(playerItem);
            }
        }

        if (catalogItem != null) {
            session.writeMessage(new CatalogPurchaseResultWriter(catalogItem, false));
        }

        for (final PlayerItem inItem : floorItems) {
            this.addItem(inItem);
        }

        for (final PlayerItem inItem : wallItems) {
            this.addItem(inItem);
        }

        session.writeMessage(new UpdatePlayerInventoryWriter());
        session.writeMessage(new InventoryNewItemsWriter(floorItems, wallItems));

        return returnValue;
    }

    public void addItem(final RoomItem item, final Session session, final boolean setFlag) {
        if (this.itemsToRemove.contains(item.getItemId())) {
            this.itemsToRemove.remove(new Integer(item.getItemId()));
        }
        this.itemsToAdd.add(item.getItemId());
        if (setFlag) {
            this.itemsToUpdate.add(item.getItemId());
        }
        String flags = item.getFlags();
        if (item.getTermFlags() != null) {
            flags = "";
            for (final String tFlag : item.getTermFlags()) {
                flags += tFlag + (char) 10;
            }
        }
        this.items.put(item.getItemId(), new PlayerItem(item.getItemId(), item.getBase(), flags, item.getInteractorId()));
        if (session != null) {
            session.writeMessage(new UpdatePlayerInventoryWriter());
        }
    }

    public void addItem(final PlayerItem item, final boolean setFlag) {
        if (this.itemsToRemove.contains(item.getItemId())) {
            this.itemsToRemove.remove(new Integer(item.getItemId()));
        }
        this.itemsToAdd.add(item.getItemId());
        if (setFlag) {
            this.itemsToUpdate.add(item.getItemId());
        }
        this.items.put(item.getItemId(), item);
    }

    public void addItem(final PlayerItem item) {
        this.addItem(item, true);
    }

    public void save(final int playerId) {
        final StringBuilder removeQuery = new StringBuilder();
        final StringBuilder addQuery = new StringBuilder();
        final StringBuilder updateQuery = new StringBuilder();
        List<String> flagList = new GapList<>();
        for (final Integer removeItem : this.itemsToRemove) {
            removeQuery.append(" OR item_id=").append(removeItem);
        }
        for (final Integer addItem : this.itemsToAdd) {
            addQuery.append(" ,(").append(addItem).append(", ").append(playerId).append(")");
        }
        for (final Integer updateItem : this.itemsToUpdate) {
            updateQuery.append(" ,(").append(updateItem).append(", ?)");
            flagList.add(this.items.get(updateItem).getFlags());
        }
        if (addQuery.length() > 0) {
            Bootloader.getStorage().executeQuery("REPLACE INTO player_items (item_id, player_id) VALUES " + addQuery.toString().substring(2));
        }
        if (removeQuery.length() > 0) {
            Bootloader.getStorage().executeQuery("DELETE FROM player_items WHERE (" + removeQuery.toString().substring(4) + ") AND player_id=" + playerId);
        }
        if (updateQuery.length() > 0) {
            final PreparedStatement pn = Bootloader.getStorage().queryParams("REPLACE INTO item_flags (item_id, flag) VALUES " + updateQuery.toString().substring(2));
            try {
                for (int i = 0; i < flagList.size(); i++) {
                    pn.setString(i + 1, flagList.get(i));
                }
                pn.execute();
            } catch (final SQLException ex) {
                logger.error("SQL Exception", ex);
            }
            flagList.clear();
        }
        this.itemsToUpdate.clear();
        this.itemsToAdd.clear();
        this.itemsToRemove.clear();
    }

    public boolean itemHasToUpdate(final Integer item) {
        return this.itemsToUpdate.contains(item);
    }

    public void load(final int playerId) {
        try {
            final ResultSet row = Bootloader.getStorage().queryParams("SELECT * FROM player_items, items WHERE player_id = " + playerId + " AND items.id=item_id ORDER BY item_id").executeQuery();
            while (row.next()) {
                final PlayerItem item = new PlayerItem();
                item.set(row);
                this.items.put(row.getInt("item_id"), item);
            }
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

    // fields
    private final ConcurrentHashMap<Integer, PlayerItem> items;

    // sql queues
    private final GapList<Integer> itemsToAdd;
    private final GapList<Integer> itemsToRemove;
    private final GapList<Integer> itemsToUpdate;

}
