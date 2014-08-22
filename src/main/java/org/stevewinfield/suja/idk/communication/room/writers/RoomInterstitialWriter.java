/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomInterstitialWriter extends MessageWriter {

    public RoomInterstitialWriter() {
        super(OperationCodes.getOutgoingOpCode("RoomInterstitial"));
        super.push("");
        super.push("");
    }

}
