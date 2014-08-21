/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.inventory;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.ISerialize;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.game.furnitures.Furniture;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureType;

public class PlayerItem implements ISerialize {
    private static Logger logger = Logger.getLogger(PlayerItem.class);

    // getters
    public int getItemId() {
        return itemId;
    }

    public Furniture getBase() {
        return base;
    }

    public String getFlags() {
        return flags;
    }

    public int getInteractorId() {
        return interactorId;
    }

    public boolean isWiredItem() {
        return this.base.isWiredItem();
    }

    public boolean flagsHidden() {
        return this.base.isWiredItem() || this.base.isGift() || this.getBase().getId() == IDK.CATA_RECYCLER_BOX_ID
        || this.getBase().getInteractor() == FurnitureInteractor.TELEPORTER;
    }

    public PlayerItem() {
        this.itemId = 0;
        this.base = null;
        this.flags = "0";
    }

    public PlayerItem(final int itemId, final Furniture base, final String flags, final int interactorId) {
        this.itemId = itemId;
        this.base = base;
        this.flags = flags;
        this.interactorId = interactorId;
    }

    public void set(final ResultSet row) {
        try {
            this.itemId = row.getInt("item_id");
            this.base = Bootloader.getGame().getFurnitureManager().getFurniture(row.getInt("base_item"));
            this.interactorId = row.getInt("special_interactor") > -1 ? row.getInt("special_interactor") : this.base
            .getInteractor();
            final ResultSet _row = Bootloader.getStorage()
            .queryParams("SELECT flag FROM item_flags WHERE item_id=" + this.itemId).executeQuery();
            if (_row != null && _row.next())
                this.flags = _row.getString("flag");
            else
                this.flags = "0";
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

    @Override
    public void serialize(final MessageWriter writer) {
        int typeId = 1;
        int secondaryId = 0;

        switch (this.getBase().getInteractor()) {
        case FurnitureInteractor.WALLPAPER:
            typeId = 2;
            break;
        case FurnitureInteractor.FLOOR:
            typeId = 3;
            break;
        case FurnitureInteractor.LANDSCAPE:
            typeId = 4;
            break;
        case FurnitureInteractor.GIFT:
            final String[] termFlags = this.flags.split("" + (char) 10);
            if (termFlags.length > 2) {
                secondaryId = (Integer.valueOf(termFlags[1]) * 1000) + Integer.valueOf(termFlags[2]);
            }
            break;
        }

        writer.push(itemId);
        writer.push(this.base.getType().toUpperCase() + "");
        writer.push(itemId);
        writer.push(base.getSpriteId());
        writer.push(typeId); // types.. (todo)
        writer.push(this.flagsHidden() ? "" : flags);
        writer.push(true); // can recycle (todo)
        writer.push(base.isTradeable());
        writer.push(base.isInventoryStackable());
        writer.push(true); // can sell (todo)
        writer.push(-1);
        if (!base.getType().equals(FurnitureType.WALL)) {
            writer.push(""); // extra..
            writer.push(secondaryId); // extra..secondary id
        }
    }

    // fields
    private int itemId;
    private String flags;
    private Furniture base;
    private int interactorId;

}
