/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.handshake.readers;

import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.handshake.writers.SessionParamsWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class InitializeCryptoReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (session.isAuthenticated())
            return;
        session.writeMessage(new SessionParamsWriter());
    }

}
