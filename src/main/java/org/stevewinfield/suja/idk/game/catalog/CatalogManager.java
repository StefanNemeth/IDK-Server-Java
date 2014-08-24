/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.catalog;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.InputFilter;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.catalog.writers.CatalogIndexWriter;
import org.stevewinfield.suja.idk.communication.catalog.writers.CatalogPurchaseResultWriter;
import org.stevewinfield.suja.idk.communication.catalog.writers.ClubGiftReadyWriter;
import org.stevewinfield.suja.idk.communication.catalog.writers.GiftReceiverNotFoundWriter;
import org.stevewinfield.suja.idk.communication.player.writers.*;
import org.stevewinfield.suja.idk.game.furnitures.Furniture;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;
import org.stevewinfield.suja.idk.game.levels.ClubSubscriptionLevel;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

public class CatalogManager {
    private static final Logger logger = Logger.getLogger(CatalogManager.class);

    public LinkedHashMap<Integer, CatalogPage> getCatalogPages() {
        return catalogPages;
    }

    public LinkedHashMap<Integer, CatalogClubOffer> getCatalogClubOffers() {
        return catalogClubOffers;
    }

    public LinkedHashMap<String, CatalogClubGift> getCatalogClubGifts() {
        return catalogClubGifts;
    }

    public LinkedHashMap<Integer, GapList<Integer>> getCatalogRecyclerRewards() {
        return catalogRecyclerRewards;
    }

    public MessageWriter getCachedIndex() {
        return cachedIndex;
    }

    public LinkedHashMap<Integer, Furniture> getCatalogModernGiftItems() {
        return catalogModernGiftItems;
    }

    public CatalogManager() {
        this.catalogPages = new LinkedHashMap<>();
        this.catalogPages.put(-1, new CatalogPage());
    }

    public int getLoadedItems() {
        return loadedItems;
    }

    public void loadCache() {
        this.catalogPages = new LinkedHashMap<>();
        this.catalogClubOffers = new LinkedHashMap<>();
        this.catalogClubGifts = new LinkedHashMap<>();
        this.catalogModernGiftItems = new LinkedHashMap<>();
        this.catalogDefaultGiftItems = new GapList<>();
        this.catalogRecyclerRewards = new LinkedHashMap<>();
        this.catalogPages.put(-1, new CatalogPage());
        int itemsLoaded = 0;
        try {
            ResultSet row = Bootloader.getStorage().queryParams("SELECT * FROM catalog_pages WHERE visible=1 ORDER BY order_num, id").executeQuery();
            while (row.next()) {
                final CatalogPage page = new CatalogPage();
                page.set(row);
                this.catalogPages.put(row.getInt("id"), page);
            }
            logger.info(this.catalogPages.size() + " CatalogPage(s) loaded.");
            row = Bootloader.getStorage().queryParams("SELECT * FROM catalog_gift_items ORDER BY id").executeQuery();
            while (row.next()) {
                final Furniture base = Bootloader.getGame().getFurnitureManager().getFurniture(row.getInt("furni_id"));
                if (row.getInt("is_modern") == 1) {
                    this.catalogModernGiftItems.put(base.getSpriteId(), base);
                } else {
                    this.catalogDefaultGiftItems.add(base);
                }
            }
            row = Bootloader.getStorage().queryParams("SELECT * FROM catalog_items WHERE visible=1 ORDER BY id").executeQuery();
            while (row.next()) {
                if (!this.catalogPages.containsKey(row.getInt("page_id"))) {
                    continue;
                }
                final CatalogItem item = new CatalogItem();
                item.set(row);
                this.catalogPages.get(row.getInt("page_id")).addItem(item);
                itemsLoaded++;
            }
            logger.info(itemsLoaded + " CatalogItem(s) loaded.");
            row = Bootloader.getStorage().queryParams("SELECT * FROM catalog_subscriptions ORDER BY id").executeQuery();
            while (row.next()) {
                final CatalogClubOffer offer = new CatalogClubOffer();
                offer.set(row);
                this.catalogClubOffers.put(offer.getId(), offer);
            }
            logger.info(this.catalogClubOffers.size() + " CatalogClubOffer(s) loaded.");
            row = Bootloader.getStorage().queryParams("SELECT * FROM catalog_club_gifts ORDER BY id").executeQuery();
            while (row.next()) {
                final CatalogClubGift gift = new CatalogClubGift();
                gift.set(row);
                this.catalogClubGifts.put(gift.getName(), gift);
            }
            logger.info(this.catalogClubGifts.size() + " CatalogClubGift(s) loaded.");
            row = Bootloader.getStorage().queryParams("SELECT * FROM catalog_recycler_rewards ORDER BY id").executeQuery();
            while (row.next()) {
                final int chanceLevel = row.getInt("chance_level");
                if (!this.catalogRecyclerRewards.containsKey(chanceLevel)) {
                    this.catalogRecyclerRewards.put(chanceLevel, new GapList<Integer>());
                }
                this.catalogRecyclerRewards.get(chanceLevel).add(row.getInt("item_id"));

            }
            logger.info(this.catalogRecyclerRewards.size() + " Recycler reward(s) loaded.");
            row.close();
            for (final CatalogPage page : this.catalogPages.values()) {
                page.cache();
            }

        } catch (final SQLException ex) {
            logger.error("SQL Exception", ex);
        }

        this.loadedItems = itemsLoaded;
        this.cachedIndex = new CatalogIndexWriter(this.catalogPages.get(-1));
    }

    public List<CatalogPage> getSubPages(final int pageId) {
        final List<CatalogPage> pages = new GapList<>();
        for (final CatalogPage page : this.catalogPages.values()) {
            if (page.getParentId() == pageId && page.isVisible()) {
                pages.add(page);
            }
        }
        return pages;
    }

    public void purchaseItem(final Session session, final CatalogPage page, final int itemId, final String flags) {
        this.purchaseItem(session, page, itemId, flags, false, null, null, 0, 0, 0);
    }

    public void purchaseItem(final Session session, final CatalogPage page, final int itemId, String flags, final boolean isGift, final String targetName, final String targetMessage, final int giftColor, final int giftBox, final int giftRibbon) {

        /**
         * Buy VIP/Club membership
         */
        if (page.getLayout().equals("club_buy")) {
            if (isGift || !this.catalogClubOffers.containsKey(itemId)) {
                return;
            }

            final CatalogClubOffer offer = this.catalogClubOffers.get(itemId);

            if ((offer.getType() == CatalogClubOfferType.BASIC && session.getPlayerInstance().getSubscriptionManager().getBaseLevel() > ClubSubscriptionLevel.BASIC) || session.getPlayerInstance().getInformation().getCreditsBalance() < offer.getPrice()) {
                return;
            }

            if (offer.getPrice() > 0) {
                session.getPlayerInstance().getInformation().setCredits(-offer.getPrice());
                session.writeMessage(new CreditsBalanceWriter(session.getPlayerInstance().getInformation().getCreditsBalance()));
                session.getPlayerInstance().getInformation().updateCurrencies();
            }

            session.getPlayerInstance().getSubscriptionManager().update(offer.getType() == CatalogClubOfferType.VIP ? ClubSubscriptionLevel.VIP : ClubSubscriptionLevel.BASIC, offer.getLengthSeconds());
            session.writeMessage(new CatalogPurchaseResultWriter(offer.getId(), offer.getName(), offer.getPrice(), 0, 0));
            session.writeMessage(new LevelRightsListWriter(session.getPlayerInstance().hasVIP(), session.getPlayerInstance().hasClub(), session.getPlayerInstance().hasRight("hotel_admin")));
            session.writeMessage(new SubscriptionStatusWriter(session.getPlayerInstance().getSubscriptionManager(), true));

            final int availableClubGifts = session.getPlayerInstance().getSubscriptionManager().getAvailableGifts();

            if (availableClubGifts > 0) {
                session.writeMessage(new ClubGiftReadyWriter(availableClubGifts));
            }
            return;
        }

        if (!page.getItems().containsKey(itemId)) {
            return;
        }

        final CatalogItem item = page.getItems().get(itemId);

        if (item == null || session.getPlayerInstance().getInformation().getCreditsBalance() < item.getCostsCoins() || session.getPlayerInstance().getInformation().getPixelsBalance() < item.getCostsPixels() || session.getPlayerInstance().getInformation().getShellsBalance() < item.getCostsExtra()) {
            return;
        }

        if (item.getBaseItem().getInteractor() == FurnitureInteractor.TROPHY) {
            if (flags.length() > 255) {
                flags = flags.substring(0, 255);
            }
            flags = session.getPlayerInstance().getInformation().getPlayerName().replace("" + (char) 9, "") + (char) 9 + (new SimpleDateFormat(IDK.SYSTEM_DATE_FORMAT).format(new Date())) + (char) 9 + InputFilter.filterString(flags.replace("" + (char) 9, "").trim(), false);
        } else {
            flags = "";
        }

        int targetId = 0;
        int coinsPrice = item.getCostsCoins();

        if (isGift) {
            if (giftBox < 0 || giftRibbon < 0 || giftBox >= IDK.CATA_GIFTS_BOX_COUNT || giftRibbon >= IDK.CATA_GIFTS_RIBBON_COUNT) {
                return;
            }
            if (targetName.length() > 0) {
                final PreparedStatement nameCheck = Bootloader.getStorage().queryParams("SELECT id FROM players WHERE nickname = ?");
                try {
                    nameCheck.setString(1, targetName);
                    final ResultSet set = nameCheck.executeQuery();
                    if (set.next()) {
                        targetId = set.getInt("id");
                    }
                } catch (final SQLException ex) {
                    logger.error("SQL Exception", ex);
                }
            }
            if (targetId == 0) {
                session.writeMessage(new GiftReceiverNotFoundWriter());
                return;
            }
            Furniture base;
            if (this.catalogModernGiftItems.containsKey(giftColor)) {
                base = this.catalogModernGiftItems.get(giftColor);
                coinsPrice += IDK.CATA_GIFTS_MODERN_PRICE;
                if (session.getPlayerInstance().getInformation().getCreditsBalance() < coinsPrice) {
                    session.writeMessage(new CurrencyErrorWriter(true, false, false));
                    return;
                }
            } else {
                base = this.catalogDefaultGiftItems.get((new SecureRandom()).nextInt(this.catalogDefaultGiftItems.size()));
            }
            if (base != null) {
                final Session target = Bootloader.getSessionManager().getAuthenticatedSession(targetId);
                final String extraData = " " + targetMessage + (char) 10 + giftBox + (char) 10 + giftRibbon + (char) 10 + item.getId() + (char) 10 + flags.replace("" + (char) 10, "");
                if (target == null) {
                    int lastItem;
                    Bootloader.getStorage().executeQuery("INSERT INTO items (base_item, special_interactor) VALUES (" + base.getId() + ", -1)");
                    Bootloader.getStorage().executeQuery("INSERT INTO player_items (item_id, player_id) VALUES (" + (lastItem = Bootloader.getStorage().readLastId("items")) + ", " + targetId + ")");
                    final PreparedStatement ps = Bootloader.getStorage().queryParams("INSERT INTO item_flags (item_id, flag) VALUES (" + lastItem + ", ?)");
                    try {
                        ps.setString(1, extraData);
                        ps.execute();
                    } catch (final SQLException e) {
                        logger.error("SQL Exception", e);
                    }
                } else if (target.isAuthenticated()) {
                    target.getPlayerInstance().getInventory().addItem(base, target, item.getAmount(), extraData, item.getSecondaryData(), null);
                }
            } else {
                return;
            }
            session.writeMessage(new CatalogPurchaseResultWriter(item, true));
        }

        if (coinsPrice > 0) {
            session.getPlayerInstance().getInformation().setCredits(-coinsPrice);
            session.writeMessage(new CreditsBalanceWriter(session.getPlayerInstance().getInformation().getCreditsBalance()));
        }

        if (item.getCostsPixels() > 0 || item.getCostsExtra() > 0) {
            if (item.getCostsPixels() > 0) {
                session.getPlayerInstance().getInformation().setPixels(-item.getCostsPixels());
            } else {
                session.getPlayerInstance().getInformation().setShells(-item.getCostsExtra());
            }
            session.writeMessage(new ActivityPointsWriter(session.getPlayerInstance().getInformation().getPixelsBalance(), session.getPlayerInstance().getInformation().getShellsBalance()));
        }

        session.getPlayerInstance().getInformation().updateCurrencies();

        if (!isGift) {

            session.getPlayerInstance().getInventory().addItem(item.getBaseItem(), session, item.getAmount(), flags, item.getSecondaryData(), item);
        }
    }

    // fields
    private LinkedHashMap<Integer, CatalogPage> catalogPages;
    private LinkedHashMap<Integer, CatalogClubOffer> catalogClubOffers;
    private LinkedHashMap<String, CatalogClubGift> catalogClubGifts;
    private LinkedHashMap<Integer, GapList<Integer>> catalogRecyclerRewards;
    private GapList<Furniture> catalogDefaultGiftItems;
    private LinkedHashMap<Integer, Furniture> catalogModernGiftItems;
    private MessageWriter cachedIndex;
    private int loadedItems;
}
