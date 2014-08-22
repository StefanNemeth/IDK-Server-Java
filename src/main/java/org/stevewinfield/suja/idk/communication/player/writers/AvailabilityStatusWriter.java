/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class AvailabilityStatusWriter extends MessageWriter {

    public AvailabilityStatusWriter() {
        super(OperationCodes.getOutgoingOpCode("AvailabilityStatus"));
        super.push(true);
        super.push(false);
    }

}
