/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.recycler.readers;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.catalog.recycler.writers.CatalogRecyclerResultWriter;
import org.stevewinfield.suja.idk.game.furnitures.Furniture;
import org.stevewinfield.suja.idk.game.inventory.PlayerItem;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecycleItemsReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom() || !IDK.CATA_RECYCLER_ENABLED) {
            return;
        }

        final int amount = reader.readInteger();

        if (amount < 5 || amount > 5) {
            return;
        }

        int level = 1;
        final SecureRandom random = new SecureRandom();

        if ((random.nextInt(41) + 1) == 40) {
            level = 5;
        } else if ((random.nextInt(26) + 1) == 25) {
            level = 4;
        } else if ((random.nextInt(16) + 1) == 15) {
            level = 3;
        } else if ((random.nextInt(6) + 1) == 5) {
            level = 2;
        }

        if (!Bootloader.getGame().getCatalogManager().getCatalogRecyclerRewards().containsKey(level)) {
            return;
        }

        final GapList<Integer> recyclerRewards = Bootloader.getGame().getCatalogManager().getCatalogRecyclerRewards().get(level);
        final int randomId = random.nextInt(recyclerRewards.size());
        final Furniture recyclerReward = Bootloader.getGame().getFurnitureManager().getFurniture(recyclerRewards.get(randomId));

        if (recyclerReward == null) {
            return;
        }

        final GapList<Integer> itemTypes = new GapList<>();
        final GapList<Integer> items = new GapList<>();

        for (int i = 0; i < amount; i++) {
            final int itemId = reader.readInteger();
            if (!session.getPlayerInstance().getInventory().hasItem(itemId)) {
                return;
            }
            final PlayerItem furni = session.getPlayerInstance().getInventory().getItem(itemId);
            if (!furni.getBase().isRecyclable() || itemTypes.contains(furni.getBase().getSpriteId())) {
                return;
            }
            itemTypes.add(furni.getBase().getSpriteId());
            items.add(furni.getItemId());
        }

        String itemRemoveQuery = "";

        for (final Integer toRemove : items) {
            session.getPlayerInstance().getInventory().removeItem(toRemove, session);
            itemRemoveQuery += " OR id=" + toRemove;
        }

        session.writeMessage(new CatalogRecyclerResultWriter(true, session.getPlayerInstance().getInventory().addItem(Bootloader.getGame().getFurnitureManager().getFurniture(IDK.CATA_RECYCLER_BOX_ID), session, 1, (new SimpleDateFormat(IDK.SYSTEM_DATE_FORMAT).format(new Date())) + (char) 10 + recyclerReward.getId(), null)));

        if (itemRemoveQuery.length() > 0) {
            Bootloader.getStorage().executeQuery("DELETE FROM items WHERE " + itemRemoveQuery.substring(4));
        }
    }

}
