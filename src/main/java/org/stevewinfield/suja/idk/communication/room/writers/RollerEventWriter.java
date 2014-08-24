/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.rooms.coordination.Vector3;

public class RollerEventWriter extends MessageWriter {

    public RollerEventWriter(final Vector3 source, final Vector3 target, final int playerId, final int rollerId, final int itemId, final boolean itemMode) {
        super(OperationCodes.getOutgoingOpCode("RollerEvent"));
        super.push(source.getX());
        super.push(source.getY());
        super.push(target.getX());
        super.push(target.getY());
        super.push(itemMode); // TODO: roller or item
        if (itemMode) {
            super.push(itemId);
        } else {
            super.push(rollerId);
            super.push(2);
            super.push(playerId);
        }
        super.push(String.valueOf(source.getAltitude()));
        super.push(String.valueOf(target.getAltitude()));
        if (itemMode) {
            super.push(rollerId);
        }
    }

}
