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
import org.stevewinfield.suja.idk.game.miscellaneous.ChatCommandArguments;
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
            final String command = message.substring(1).split(" ")[0].toLowerCase();
            final String args = message.length() > (1 + command.length()) ? message.substring(2 + command.length()) : "";
            if (Bootloader.getGame().getChatCommandHandler().commandExists(command) && session.getPlayerInstance().hasRight(Bootloader.getGame().getChatCommandHandler().getCommand(command).getPermissionCode()) && Bootloader.getGame().getChatCommandHandler().getCommand(command).execute(session.getRoomPlayer(), new ChatCommandArguments(args, shouting))) {
                return;
            }
        }

        session.getRoomPlayer().chat(message, shouting ? ChatType.SHOUT : ChatType.SAY);
    }

}
