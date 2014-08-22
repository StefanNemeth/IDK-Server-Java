/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication;

import org.stevewinfield.suja.idk.network.sessions.Session;

public interface IMessageReader {
    void parse(final Session session, final MessageReader reader);
}
