/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.stevewinfield.suja.idk.encryption.Base64Encryption;
import org.stevewinfield.suja.idk.encryption.WireEncryption;

import java.nio.charset.Charset;

public class MessageWriter {
    private short headerId;
    private ChannelBuffer body;
    private int senderId;

    public Integer getSenderId() {
        return senderId;
    }

    public short getId() {
        return headerId;
    }

    public MessageWriter(final short outgoingOpCode) {
        this.initialize(outgoingOpCode);
    }

    public void setSender(final int senderId) {
        this.senderId = senderId;
    }

    public void initialize(final short outgoingOpCode) {
        this.headerId = outgoingOpCode;
        this.body = ChannelBuffers.dynamicBuffer();
        this.push(Base64Encryption.encode(this.headerId));
    }

    public void push(final boolean s) {
        this.body.writeByte((byte) (s ? 'I' : 'H'));
    }

    public void push(final byte[] x) {
        this.body.writeBytes(x);
    }

    public void push(final int i) {
        this.push(WireEncryption.encode(i));
    }

    public void push(final String s) {
        this.push(s.replace((char) 2, ' ').replace((char) 1, ' ').replace((char) 0, ' '), true);
    }

    public void push(final String s, final boolean breaks) {
        this.push(s.getBytes());
        if (breaks) {
            this.body.writeByte((byte) 2);
        }
    }

    public void push(final ISerialize x) {
        x.serialize(this);
    }

    public String getDebugString() {
        final ChannelBuffer bodeh = body.duplicate();
        return new String(bodeh.toString(Charset.defaultCharset()));
    }

    private ChannelBuffer cachedResult;

    public ChannelBuffer getBytes() {
        if (cachedResult == null) {
            cachedResult = ChannelBuffers.copiedBuffer(this.body);
            cachedResult.writeByte(1);
        }
        return cachedResult;
    }
}
