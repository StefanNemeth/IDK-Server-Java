/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.catalog;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.ISerialize;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.game.furnitures.Furniture;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;

import java.sql.ResultSet;

public class CatalogItem implements ISerialize {
    private static final Logger logger = Logger.getLogger(CatalogItem.class);

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPageId() {
        return pageId;
    }

    public int getCostsPixels() {
        return costsPixels;
    }

    public int getCostsCoins() {
        return costsCoins;
    }

    public int getCostsExtra() {
        return costsExtra;
    }

    public int getAmount() {
        return amount;
    }

    public Furniture getBaseItem() {
        return baseItem;
    }

    public CatalogItem() {
        this.id = 0;
        this.pageId = 0;
        this.costsPixels = 0;
        this.costsCoins = 0;
        this.costsExtra = 0;
        this.displayName = "";
        this.amount = 0;
        this.baseItem = new Furniture();
    }

    public String getSecondaryData() {
        return this.secondaryData;
    }

    public void set(final ResultSet row) {
        try {
            this.id = row.getInt("id");
            this.displayName = row.getString("display_name");
            this.pageId = row.getInt("page_id");
            this.baseItem = Bootloader.getGame().getFurnitureManager().getFurniture(row.getInt("furni_id"));
            switch (this.baseItem.getInteractor()) {
                case FurnitureInteractor.WALLPAPER:
                case FurnitureInteractor.FLOOR:
                case FurnitureInteractor.LANDSCAPE:
                    final String[] split = this.displayName.split("_");
                    if (split.length == 3) {
                        this.secondaryData = split[2];
                    }
                    break;
            }

            this.costsPixels = row.getInt("costs_pixels");
            this.costsCoins = row.getInt("costs_coins");
            this.costsExtra = row.getInt("costs_extra");
            this.amount = row.getInt("amount");
        } catch (final Exception e) {
            System.out.println(this.id);
            logger.error("SQL Exception", e);
        }
    }

    // fields
    private int id;
    private String displayName;
    private int costsPixels;
    private int costsCoins;
    private int pageId;
    private int costsExtra;
    private int amount;
    private String secondaryData;
    private Furniture baseItem;

    @Override
    public void serialize(final MessageWriter writer) {
        writer.push(id);
        writer.push(displayName);
        writer.push(costsCoins);
        writer.push(costsPixels);
        writer.push(costsExtra);
        writer.push(1); // item
        writer.push(baseItem == null ? "s" : baseItem.getType());
        writer.push(baseItem == null ? 0 : baseItem.getSpriteId());
        writer.push(this.secondaryData != null ? this.secondaryData : ""); // TODO
        // default
        // flags
        writer.push(amount);
        writer.push(-1);
        writer.push(0); // TODO club restriction
    }
}
