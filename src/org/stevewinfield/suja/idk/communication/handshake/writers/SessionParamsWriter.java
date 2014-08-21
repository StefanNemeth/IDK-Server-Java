/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.handshake.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class SessionParamsWriter extends MessageWriter {

    public SessionParamsWriter() {
        super(OperationCodes.getOutgoingOpCode("SessionParams"));
        super.push(9);
        super.push(false);
        super.push(false);
        super.push(true);
        super.push(true);
        super.push(3);
        super.push(false);
        super.push(2);
        super.push(false);
        super.push(4);
        super.push(1);
        super.push(5);
        super.push("dd-MM-yyyy");
        super.push(7);
        super.push(0);
        super.push(8);
        super.push("http://hotel-us");
        super.push(9);
    }

}
