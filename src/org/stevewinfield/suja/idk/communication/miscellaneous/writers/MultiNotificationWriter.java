/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.miscellaneous.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class MultiNotificationWriter extends MessageWriter {

    public MultiNotificationWriter(final String message) {
        super(OperationCodes.getOutgoingOpCode("MultiNotification"));
        super.push(true);
        super.push(message);
    }

}
