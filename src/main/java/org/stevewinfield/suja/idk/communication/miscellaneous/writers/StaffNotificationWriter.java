/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.miscellaneous.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class StaffNotificationWriter extends MessageWriter {

    public StaffNotificationWriter(final String message, final String url) {
        super(OperationCodes.getOutgoingOpCode("StaffNotification"));
        super.push(message);
        if (url.length() > 0) {
            super.push(url);
        }
    }

    public StaffNotificationWriter(final String message) {
        this(message, "");
    }

}
