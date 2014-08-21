/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.handshake.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class AuthenticatedWriter extends MessageWriter {

    public AuthenticatedWriter() {
        super(OperationCodes.getOutgoingOpCode("Authenticated"));
    }

}
