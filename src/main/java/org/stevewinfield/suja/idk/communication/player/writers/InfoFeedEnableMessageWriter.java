/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class InfoFeedEnableMessageWriter extends MessageWriter {

    public InfoFeedEnableMessageWriter(final boolean enabled) {
        super(OperationCodes.getOutgoingOpCode("InfoFeedEnableMessage"));
        super.push(enabled);
    }
}
