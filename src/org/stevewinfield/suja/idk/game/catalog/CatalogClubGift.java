/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.catalog;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.ISerialize;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.game.furnitures.Furniture;

public class CatalogClubGift implements ISerialize {
    private static Logger logger = Logger.getLogger(CatalogClubGift.class);

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMinMonths() {
        return minMonths;
    }

    public boolean onlyVIP() {
        return vip;
    }

    public Furniture getBase() {
        return baseItem;
    }

    public CatalogClubGift() {
        this.name = "";
    }

    public void set(final ResultSet row) {
        try {
            this.id = row.getInt("id");
            this.name = row.getString("name");
            this.baseItem = Bootloader.getGame().getFurnitureManager().getFurniture(row.getInt("furni_id"));
            this.vip = row.getInt("only_vip") == 1;
            this.minMonths = row.getInt("min_months");
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

    // fields
    private int id;
    private int minMonths;
    private boolean vip;
    private String name;
    private Furniture baseItem;

    @Override
    public void serialize(final MessageWriter writer) {
        writer.push(id);
        writer.push(name);
        writer.push(0); // is a gift so its free
        writer.push(0);
        writer.push(0);
        writer.push(1); // item
        writer.push(baseItem == null ? "s" : baseItem.getType());
        writer.push(baseItem == null ? 0 : baseItem.getSpriteId());
        writer.push("");
        writer.push(1);
        writer.push(-1);
        writer.push(0);
    }
}
