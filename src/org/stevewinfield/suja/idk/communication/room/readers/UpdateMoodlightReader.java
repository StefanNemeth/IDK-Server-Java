/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.InputFilter;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.miscellaneous.MoodlightData;
import org.stevewinfield.suja.idk.game.miscellaneous.MoodlightPreset;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class UpdateMoodlightReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom())
            return;

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || !room.hasRights(session, true) || room.getMoodlight() == null)
            return;

        final MoodlightData data = MoodlightData.getInstance(room.getMoodlight().getTermFlags()[0]);
        final int presetId = reader.readInteger();

        if (!data.getPresets().containsKey(presetId))
            return;

        final MoodlightPreset preset = data.getPresets().get(presetId);

        preset.setBackgroundOnly(!reader.readBoolean());
        preset.setColorCode(InputFilter.filterString(reader.readUTF().trim()));
        preset.setColorIntensity(reader.readInteger());

        data.setCurrentPreset(presetId);

        if (!MoodlightData.isValidColor(preset.getColorCode()))
            return;

        room.getMoodlight().setTermFlags(new String[] { data.getFlagData() });
        room.getMoodlight().setFlags(data.getDisplayData());
        room.getMoodlight().update(session);
    }

}
