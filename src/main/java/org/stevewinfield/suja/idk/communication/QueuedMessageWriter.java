/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.nio.charset.Charset;

public class QueuedMessageWriter {
    private int senderId;

    public QueuedMessageWriter() {
        this.initialize();
    }

    public int getSenderId() {
        return senderId;
    }

    public void initialize() {
        this.body = ChannelBuffers.dynamicBuffer();
        this.count = 0;
        this.senderId = 0;
    }

    public void push(final MessageWriter writer) {
        this.body.writeBytes(writer.getBytes());
        if (writer.getSenderId() != null) {
            this.senderId = writer.getSenderId();
        }
        ++count;
    }

    public int getSize() {
        return count;
    }

    public ChannelBuffer getBytes() {
        return this.body;
    }

    public String getDebugString() {
        final ChannelBuffer bodeh = body.duplicate();
        return new String(bodeh.toString(Charset.defaultCharset()));
    }

    private ChannelBuffer body;
    private int count;
}
