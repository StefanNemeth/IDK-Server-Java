/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class OperationCodes {

    public static short getIncomingOpCode(final String key) {
        return Short.valueOf(incomingOpCodes.getProperty(key, "0"));
    }

    public static short getOutgoingOpCode(final String key) {
        return Short.valueOf(outgoingOpCodes.getProperty(key, "0"));
    }

    public static void loadCodes() throws IOException {
        incomingOpCodes = new Properties();
        outgoingOpCodes = new Properties();

        incomingOpCodes.load(new FileInputStream("opcodes-incoming.properties"));
        outgoingOpCodes.load(new FileInputStream("opcodes-outgoing.properties"));
    }

    private static Properties incomingOpCodes;
    private static Properties outgoingOpCodes;
}
