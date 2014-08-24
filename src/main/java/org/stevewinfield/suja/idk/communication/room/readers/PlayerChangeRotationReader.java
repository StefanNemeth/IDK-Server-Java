/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.coordination.Rotation;
import org.stevewinfield.suja.idk.game.rooms.coordination.Vector2;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class PlayerChangeRotationReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || session.getRoomPlayer().isWalking()) {
            return;
        }

        final RoomPlayer player = session.getRoomPlayer();
        final Vector2 targetPosition = new Vector2(reader.readInteger(), reader.readInteger());

        if ((targetPosition.getX() == player.getPosition().getX() && targetPosition.getY() == player.getPosition().getY()) || player.getStatusMap().containsKey("sit") || player.getStatusMap().containsKey("lay")) {
            return;
        }

        final int rotation = Rotation.calculate(player.getPosition().getX(), player.getPosition().getY(), targetPosition.getX(), targetPosition.getY());

        boolean update = false;

        if (player.getRotation() != rotation) {
            player.setBodyRotation(rotation);
            update = true;
        }

        if (player.getHeadRotation() != rotation) {
            player.setHeadRotation(rotation);
            update = true;
        }

        if (update) {
            player.update();
        }
    }

}
