/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.room.writers.RoomDecorationWriter;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;
import org.stevewinfield.suja.idk.game.inventory.PlayerItem;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.util.AbstractMap;

public class RoomApplyDecorationReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || !room.hasRights(session, true)) {
            return;
        }

        final int itemId = reader.readInteger();

        if (!session.getPlayerInstance().getInventory().hasItem(itemId)) {
            return;
        }

        final PlayerItem item = session.getPlayerInstance().getInventory().getItem(itemId);

        if (item.getFlags().length() < 1) {
            return;
        }

        String key = "";
        String updateData = "";

        switch (item.getBase().getInteractor()) {
            case FurnitureInteractor.WALLPAPER:
                key = "wallpaper";
                updateData = "wallpaper=" + item.getFlags() + ";floor=" + room.getInformation().getDecorations().get("floor") + ";landscape=" + room.getInformation().getDecorations().get("landscape");
                break;
            case FurnitureInteractor.FLOOR:
                key = "floor";
                updateData = "wallpaper=" + room.getInformation().getDecorations().get("wallpaper") + ";floor=" + item.getFlags() + ";landscape=" + room.getInformation().getDecorations().get("landscape");
                break;
            case FurnitureInteractor.LANDSCAPE:
                key = "landscape";
                updateData = "wallpaper=" + room.getInformation().getDecorations().get("wallpaper") + ";floor=" + room.getInformation().getDecorations().get("floor") + ";landscape=" + item.getFlags();
                break;
        }

        room.getInformation().setDecoration(key, item.getFlags());
        room.writeMessage(new RoomDecorationWriter(new AbstractMap.SimpleEntry<String, String>(key, item.getFlags())), session);
        Bootloader.getStorage().executeQuery("UPDATE rooms SET decorations='" + updateData + "' WHERE id=" + room.getInformation().getId());

        session.getPlayerInstance().getInventory().removeItem(itemId, session);
    }

}
