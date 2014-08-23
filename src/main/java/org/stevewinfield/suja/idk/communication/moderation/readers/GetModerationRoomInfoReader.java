/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.moderation.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.Translations;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.moderation.writers.ModerationRoomInfoWriter;
import org.stevewinfield.suja.idk.game.miscellaneous.NotifyType;
import org.stevewinfield.suja.idk.game.rooms.RoomInformation;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetModerationRoomInfoReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.getPlayerInstance().hasRight("moderation_tool"))
            return;

        final RoomInformation info = Bootloader.getGame().getRoomManager().getRoomInformation(reader.readInteger());

        if (info == null) {
            session.sendNotification(NotifyType.MOD_ALERT,
            Translations.getTranslation("fail_load_room_information"));
            return;
        }

        session.writeMessage(new ModerationRoomInfoWriter(info, Bootloader.getGame().getRoomManager()
        .getLoadedRoomInstance(info.getId())));
    }

}
