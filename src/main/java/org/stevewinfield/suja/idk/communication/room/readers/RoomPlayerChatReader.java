/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.InputFilter;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatType;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class RoomPlayerChatReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || session.getRoomPlayer().isGettingKicked()) {
            return;
        }

        final boolean shouting = reader.getMessageId() == OperationCodes.getIncomingOpCode("RoomPlayerChatShout");
        String message = InputFilter.filterString(reader.readUTF());

        if (message.length() < 1) {
            return;
        }

        if (message.length() > 100) {
            message = message.substring(0, 100);
        }

        if (message.startsWith(":") && message.length() > 1) {
            if (Bootloader.getGame().getChatCommandHandler().handleCommand(session, message, shouting)) {
                return;
            }
        }

        session.getRoomPlayer().chat(message, shouting ? ChatType.SHOUT : ChatType.SAY);
    }

}
