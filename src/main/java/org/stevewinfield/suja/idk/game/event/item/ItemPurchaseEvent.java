package org.stevewinfield.suja.idk.game.event.item;

import org.stevewinfield.suja.idk.game.catalog.CatalogItem;
import org.stevewinfield.suja.idk.game.event.Event;

public class ItemPurchaseEvent extends Event {
    public CatalogItem getItem() {
        return item;
    }

    /**
     * @return Item cost in coins
     */
    public int getCostsCoins() {
        return costsCoins;
    }

    /**
     * @param costsCoins Item cost in coins
     */
    public void setCostsCoins(int costsCoins) {
        this.costsCoins = costsCoins;
    }

    /**
     * @return Item cost in pixels
     */
    public int getCostsPixels() {
        return costsPixels;
    }

    /**
     * @param costsPixels Item cost in pixels
     */
    public void setCostsPixels(int costsPixels) {
        this.costsPixels = costsPixels;
    }

    /**
     * Extra costs is the third currency, which are usually snowflakes or shells
     * @return Item cost in extras
     */
    public int getCostsExtra() {
        return costsExtra;
    }

    /**
     * Extra costs is the third currency, which are usually snowflakes or shells
     * @param costsExtra Item cost in extras
     */
    public void setCostsExtra(int costsExtra) {
        this.costsExtra = costsExtra;
    }

    public boolean isGift() {
        return isGift;
    }

    public ItemPurchaseEvent(CatalogItem item, int costsCoins, int costsPixels, int costsExtra, boolean isGift) {
        this.item = item;
        this.costsCoins = costsCoins;
        this.costsPixels = costsPixels;
        this.costsExtra = costsExtra;
        this.isGift = isGift;
    }

    private CatalogItem item;
    private int costsCoins;
    private int costsPixels;
    private int costsExtra;
    private boolean isGift;
}
