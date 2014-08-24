/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication;

import java.util.LinkedList;
import java.util.Queue;

public class MessageReaderFactory {
    private static final Queue<MessageReader> freeObjects = new LinkedList<MessageReader>();

    public static MessageReader getMessageReader(final short messageId, final byte[] body) {
        if (freeObjects.size() > 0) {
            MessageReader reader = null;

            synchronized (freeObjects) {
                reader = freeObjects.poll();
            }

            if (reader == null) {
                return new MessageReader(messageId, body);
            }

            reader.initialize(messageId, body);
            return reader;
        }
        return new MessageReader(messageId, body);
    }

    public static void objectCallback(final MessageReader reader) {
        synchronized (freeObjects) {
            freeObjects.offer(reader);
        }
    }
}
