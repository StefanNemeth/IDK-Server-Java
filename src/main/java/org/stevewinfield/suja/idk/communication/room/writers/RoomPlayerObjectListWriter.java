/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

import java.util.List;

public class RoomPlayerObjectListWriter extends MessageWriter {

    public RoomPlayerObjectListWriter(final List<RoomPlayer> players) {
        super(OperationCodes.getOutgoingOpCode("RoomPlayerObjectList"));
        super.push(players.size());

        for (final RoomPlayer player : players) {
            super.push(player);
        }
    }

    public RoomPlayerObjectListWriter(final RoomPlayer player) {
        this(new GapList<RoomPlayer>(player));
    }

}
