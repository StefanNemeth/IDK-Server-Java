/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.InputFilter;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class SaveStickyReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom())
            return;

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null)
            return;

        final boolean hasRights = room.hasRights(session);

        if (!room.guestStickysAreAllowed() && !hasRights)
            return;

        final int itemId = reader.readInteger();

        if (!room.getRoomItems().containsKey(itemId))
            return;

        final RoomItem item = room.getRoomItems().get(itemId);

        if (item.getInteractorId() != FurnitureInteractor.POST_IT
        || item.getTermFlags() == null
        || item.getTermFlags().length < 3
        || (!hasRights && Integer.valueOf(item.getTermFlags()[2]) != session.getPlayerInstance().getInformation()
        .getId()))
            return;

        final String raw = reader.readUTF();
        final String[] split = raw.split(" ");

        if (split.length < 2)
            return;

        final String color = split[0].toUpperCase().trim();
        String text = InputFilter.filterString(raw.substring(color.length() + 1)).trim().replace((char) 10 + "", "");
        String buyIdString = item.getTermFlags()[2];

        if ((!color.equals("FFFF33") && !color.equals("FF9CFF") && !color.equals("9CCEFF") && !color.equals("9CFF9C"))
        || text.length() > 391) {
            return;
        }

        if (!hasRights) {
            text += "\n-----\n" + session.getPlayerInstance().getInformation().getPlayerName() + "\n"
            + new SimpleDateFormat(IDK.SYSTEM_DATE_FORMAT + " " + IDK.SYSTEM_TIME_FORMAT).format(new Date());
            buyIdString = "-1";
            Bootloader.getGame().getAchievementManager().progressAchievement(session, "ACH_NotesLeft", 1);
        }

        item.setFlags(color);
        item.setTermFlags(new String[] { color, text, buyIdString });
        item.update();
    }

}
