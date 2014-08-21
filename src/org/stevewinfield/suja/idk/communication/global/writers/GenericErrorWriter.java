/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.global.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class GenericErrorWriter extends MessageWriter {

    public GenericErrorWriter(final int errorCode) {
        super(OperationCodes.getOutgoingOpCode("GenericError"));
        super.push(errorCode);
    }

}
