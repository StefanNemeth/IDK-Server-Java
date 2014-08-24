/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.miscellaneous.MoodlightData;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class SwitchMoodlightReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || !room.hasRights(session, true) || room.getMoodlight() == null) {
            return;
        }

        final MoodlightData data = MoodlightData.getInstance(room.getMoodlight().getTermFlags()[0]);
        data.setEnabled(!data.isEnabled());

        room.getMoodlight().setTermFlags(new String[]{data.getFlagData()});
        room.getMoodlight().setFlags(data.getDisplayData());
        room.getMoodlight().update(session);
    }

}
