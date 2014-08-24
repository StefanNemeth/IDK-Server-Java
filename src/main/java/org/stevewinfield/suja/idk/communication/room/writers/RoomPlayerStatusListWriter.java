/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

import java.util.List;
import java.util.Map.Entry;

public class RoomPlayerStatusListWriter extends MessageWriter {

    public RoomPlayerStatusListWriter(final List<RoomPlayer> players) {
        super(OperationCodes.getOutgoingOpCode("RoomPlayerStatusList"));
        super.push(players.size());

        for (final RoomPlayer player : players) {
            super.push(player.getVirtualId());
            super.push(player.getPosition().getX());
            super.push(player.getPosition().getY());
            super.push(String.valueOf(player.getPosition().getAltitude()));
            super.push(player.getHeadRotation());
            super.push(player.getRotation());

            final StringBuilder statusList = new StringBuilder("/");

            for (final Entry<String, String> status : player.getStatusMap().entrySet()) {
                statusList.append(status.getKey()).append(' ').append(status.getValue()).append('/');
            }

            super.push(statusList.toString() + "/");
        }
    }

}
