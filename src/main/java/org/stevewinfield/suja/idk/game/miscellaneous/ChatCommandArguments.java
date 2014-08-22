/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.miscellaneous;

public class ChatCommandArguments {
    private final String[] message;
    private int pointer;
    private final boolean shouted;

    public boolean isShouted() {
        return shouted;
    }

    public ChatCommandArguments(final String message, final boolean shouted) {
        this.message = message.split(" ");
        this.pointer = 0;
        this.shouted = shouted;
    }

    public String readWord() {
        final String msg = message[pointer];
        pointer++;
        return msg;
    }

    public int readInteger() {
        int val = 0;
        try {
            val = Integer.valueOf(message[pointer]);
            pointer++;
        } catch (final NumberFormatException ex) {
            pointer++;
            return 0;
        }
        return val;
    }

    public boolean readBoolean() {
        return readInteger() > 0;
    }

    public String readMessage() {
        String result = "";
        for (; pointer < this.message.length; pointer++)
            result += this.message[pointer];
        return result;
    }
}
