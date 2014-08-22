/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class FurniCampaignsWriter extends MessageWriter {

    public FurniCampaignsWriter() {
        super(OperationCodes.getOutgoingOpCode("FurniCampaigns"));
        super.push(0);
    }

}
