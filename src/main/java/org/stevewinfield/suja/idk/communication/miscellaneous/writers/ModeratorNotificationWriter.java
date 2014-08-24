/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.miscellaneous.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class ModeratorNotificationWriter extends MessageWriter {

    public ModeratorNotificationWriter(final String message, final String url) {
        super(OperationCodes.getOutgoingOpCode("ModeratorNotification"));
        super.push(message);
        if (url.length() > 0) {
            super.push(url);
        }
    }

    public ModeratorNotificationWriter(final String message) {
        this(message, "");
    }

}
