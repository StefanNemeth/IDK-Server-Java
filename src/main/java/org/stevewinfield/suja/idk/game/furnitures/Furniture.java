/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.game.rooms.wired.WiredManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class Furniture {
    private static final Logger logger = Logger.getLogger(Furniture.class);

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSpriteId() {
        return spriteId;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public double getHeight() {
        return height;
    }

    public boolean isStackable() {
        return stackable;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public boolean isInventoryStackable() {
        return inventoryStackable;
    }

    public boolean isSitable() {
        return sitable;
    }

    public boolean isLayable() {
        return layable;
    }

    public boolean isGiftable() {
        return giftable;
    }

    public boolean isRecyclable() {
        return recyclable;
    }

    public int getInteractor() {
        return interactor;
    }

    public int getCycleCount() {
        return cycleCount;
    }

    public boolean hasRightCheck() {
        return rightCheck;
    }

    public int getEffectId() {
        return effectId;
    }

    public int getRandomVendingId() {
        return Integer.valueOf(this.vendingIds[new Random().nextInt(this.vendingIds.length)]);
    }

    public boolean isWiredItem() {
        return wiredItem;
    }

    public boolean isGift() {
        return this.interactor == FurnitureInteractor.GIFT;
    }

    public boolean isTradeable() {
        return tradeable;
    }

    public String getType() {
        return type;
    }

    public Furniture() {
        this.name = "";
        this.type = "s";
    }

    public void set(final ResultSet row) {
        try {
            this.id = row.getInt("id");
            this.name = row.getString("name");
            this.spriteId = row.getInt("sprite_id");
            this.width = row.getInt("width");
            this.length = row.getInt("length");
            this.height = row.getDouble("height");
            this.stackable = row.getInt("is_stackable") == 1;
            this.walkable = row.getInt("is_walkable") == 1;
            this.inventoryStackable = row.getInt("is_inventory_stackable") == 1;
            this.sitable = row.getInt("is_sitable") == 1;
            this.layable = row.getInt("is_layable") == 1;
            this.giftable = row.getInt("is_giftable") == 1;
            this.recyclable = row.getInt("is_recyclable") == 1;
            this.interactor = row.getInt("interactor");
            this.cycleCount = row.getInt("cycle_count");
            this.effectId = row.getInt("effect_id");
            this.vendingIds = row.getString("vending_ids").split(",");
            this.tradeable = row.getInt("is_tradeable") == 1;
            this.wiredItem = WiredManager.isWiredItem(this);
            this.type = row.getString("type").trim().toLowerCase();
            this.rightCheck = row.getInt("check_rights") == 1;
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

    // fields
    private int id;
    private String name;
    private int spriteId;
    private int width;
    private int length;
    private double height;
    private boolean stackable;
    private boolean walkable;
    private boolean inventoryStackable;
    private boolean sitable;
    private boolean layable;
    private boolean tradeable;
    private boolean giftable;
    private boolean recyclable;
    private int interactor;
    private int cycleCount;
    private int effectId;
    private String[] vendingIds;
    private boolean wiredItem;
    private String type;
    private boolean rightCheck;
}
