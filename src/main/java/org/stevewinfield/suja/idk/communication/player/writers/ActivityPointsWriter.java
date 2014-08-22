/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class ActivityPointsWriter extends MessageWriter {

    public ActivityPointsWriter(final int pixels, final int shells) {
        super(OperationCodes.getOutgoingOpCode("ActivityPointsWriter"));
        super.push(2); // count
        super.push(0); // 0: pixel
        super.push(pixels); // pixel
        super.push(4); // 4: shells
        super.push(shells); // shells
    }

}
