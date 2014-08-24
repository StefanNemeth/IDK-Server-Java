/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.InputFilter;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class PlayerWhisperReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || session.getRoomPlayer().isGettingKicked()) {
            return;
        }

        String message = reader.readUTF();

        if (message.length() < 1) {
            return;
        }

        final String targetName = message.substring(0, message.indexOf(" "));

        if (targetName.length() < 1 || message.length() < (targetName.length() + 1)) {
            return;
        }

        message = InputFilter.filterString(message.substring(message.indexOf(" ") + 1));

        if (message.length() < 1) {
            return;
        }

        if (message.length() > 100) {
            message = message.substring(0, 100);
        }

        session.getRoomPlayer().whisper(session, session.getRoomPlayer().getVirtualId(), message);

        if (targetName.equals(session.getPlayerInstance().getInformation().getPlayerName())) {
            return;
        }

        RoomPlayer target = null;

        for (final RoomPlayer player : room.getRoomPlayers().values()) {
            if (player.getSession() != null && player.getPlayerInformation().getPlayerName().equals(targetName)) {
                target = player;
                break;
            }
        }

        if (target != null) {
            target.whisper(target.getSession(), session.getRoomPlayer().getVirtualId(), message);
        }
    }

}
