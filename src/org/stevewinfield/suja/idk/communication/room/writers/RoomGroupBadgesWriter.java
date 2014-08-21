/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomGroupBadgesWriter extends MessageWriter {

    public RoomGroupBadgesWriter() {
        super(OperationCodes.getOutgoingOpCode("RoomGroupBadges"));
        // todo
        super.push(1); // count
        super.push(1); // loop -> id
        super.push("s58116s04078s04072s52074889902cf4440630470f222ad5c6489d7"); // badge
    }

}
