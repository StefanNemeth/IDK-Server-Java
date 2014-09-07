package org.stevewinfield.suja.idk.game.event.item;

import org.stevewinfield.suja.idk.game.catalog.CatalogClubOffer;
import org.stevewinfield.suja.idk.game.event.Event;

public class ItemPurchaseClubEvent extends Event {
    public CatalogClubOffer getOffer() {
        return offer;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ItemPurchaseClubEvent(CatalogClubOffer offer, int price) {
        this.offer = offer;
        this.price = price;
    }

    private CatalogClubOffer offer;
    private int price;
}
