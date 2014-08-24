/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.global.readers;

import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class LatencyTestReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        /**
         * TODO: Better way
         */
        final MessageWriter w = new MessageWriter((short) 354);
        w.push(reader.readInteger());
        session.writeMessage(w);
    }

}
