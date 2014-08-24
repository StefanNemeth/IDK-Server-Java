/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.catalog.writers.GiftOpenedWriter;
import org.stevewinfield.suja.idk.game.catalog.CatalogItem;
import org.stevewinfield.suja.idk.game.catalog.CatalogPage;
import org.stevewinfield.suja.idk.game.furnitures.Furniture;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class OpenBoxReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || !room.hasRights(session, true)) {
            return;
        }

        final int boxId = reader.readInteger();

        if (!room.getRoomItems().containsKey(boxId)) {
            return;
        }

        final RoomItem box = room.getRoomItems().get(boxId);

        if (box.getBase().getId() == IDK.CATA_RECYCLER_BOX_ID) {
            if (box.getTermFlags() == null || box.getTermFlags().length < 2) {
                return;
            }

            final Furniture furni = Bootloader.getGame().getFurnitureManager().getFurniture(Integer.valueOf(box.getTermFlags()[1]));

            if (furni == null) {
                return;
            }

            room.removeItem(box, session);
            session.writeMessage(new GiftOpenedWriter(furni));

            session.getPlayerInstance().getInventory().addItem(furni, session, 1, "", null);
            Bootloader.getStorage().executeQuery("DELETE FROM items WHERE id=" + boxId);
        } else if (!box.getBase().isGift()) {
            return;
        }

        if (box.getTermFlags() == null || box.getTermFlags().length < 4) {
            return;
        }

        final int itemId = Integer.valueOf(box.getTermFlags()[3]);
        CatalogItem item = null;

        for (final CatalogPage page : Bootloader.getGame().getCatalogManager().getCatalogPages().values()) {
            if (page.getItems().containsKey(itemId)) {
                item = page.getItems().get(itemId);
                break;
            }
        }

        if (item == null) {
            return;
        }

        room.removeItem(box, session);
        session.writeMessage(new GiftOpenedWriter(item.getBaseItem()));

        session.getPlayerInstance().getInventory().addItem(item.getBaseItem(), session, item.getAmount(), (box.getTermFlags().length > 4 ? box.getTermFlags()[4] : ""), item.getSecondaryData(), null);

        Bootloader.getStorage().executeQuery("DELETE FROM items WHERE id=" + boxId);
    }

}
