/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication;

import org.stevewinfield.suja.idk.encryption.Base64Encryption;
import org.stevewinfield.suja.idk.encryption.WireEncryption;

public class MessageReader {

    public int getRemainingLength() {
        return this.body.length - pointer;
    }

    public short getMessageId() {
        return this.messageId;
    }

    public MessageReader(final short messageId, final byte[] body) {
        this.initialize(messageId, body);
    }

    public void initialize(final short messageId, byte[] body) {
        if (body == null)
            body = new byte[0];

        this.messageId = messageId;
        this.body = body;
        this.pointer = 0;
    }

    public byte[] readBytes(int bytes) {
        if (bytes > this.getRemainingLength())
            bytes = this.getRemainingLength();

        final byte[] data = new byte[bytes];

        for (int i = 0; i < bytes; i++) {
            data[i] = this.body[this.pointer++];
        }

        return data;
    }

    public String getDebugString() {
        return new String(this.body).replace((char) 2, '#').replace((char) 1, ' ');
    }

    public byte[] plainReadBytes(int bytes) {
        if (bytes > this.getRemainingLength())
            bytes = this.getRemainingLength();

        final byte[] data = new byte[bytes];

        for (int x = 0, y = pointer; x < bytes; x++, y++)
            data[x] = this.body[y];

        return data;
    }

    public String readUTF() {
        final int length = Base64Encryption.decode(new String(this.readBytes(2)));
        return new String(this.readBytes(length)).replace((char) 1, ' ').replace((char) 2, ' ').replace((char) 0, ' ');
    }

    public boolean readBoolean() {
        return this.getRemainingLength() > 0 && this.body[pointer++] == 'I';
    }

    public int readInteger() {
        final int[] res = WireEncryption.decode(this.plainReadBytes(6));
        final int totalBytes = res[0];

        this.pointer += totalBytes;
        return res[1];
    }

    public void dispose() {
        try {
            MessageReaderFactory.objectCallback(this);
            this.finalize();
        } catch (final Throwable e) {
            e.printStackTrace();
        }
    }

    private short messageId;
    private byte[] body;
    private int pointer;
}
