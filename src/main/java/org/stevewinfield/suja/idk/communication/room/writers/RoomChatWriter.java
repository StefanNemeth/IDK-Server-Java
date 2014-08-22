/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatType;

public class RoomChatWriter extends MessageWriter {

    public RoomChatWriter(final int actorId, final String message, final int emotionId, final int type) {
        super(type == ChatType.SAY ? OperationCodes.getOutgoingOpCode("RoomChatTalk")
        : (type == ChatType.SHOUT ? OperationCodes.getOutgoingOpCode("RoomChatShout") : OperationCodes
        .getOutgoingOpCode("RoomChatWhisper")));

        // get message
        final StringBuilder builder = new StringBuilder();
        final Map<Integer, String> linkRefs = new HashMap<Integer, String>();
        final String[] bits = message.split(" ");
        int j = 0, i = 0;

        for (final String bit : bits) {
            if (j > 0)
                builder.append(' ');
            if (bit.startsWith("http://") || bit.startsWith("https://") || bit.startsWith("www.")) {
                linkRefs.put(i, bit);
                builder.append("{" + i++ + "}");
            } else {
                builder.append(bit);
            }
            j++;
        }

        super.push(actorId);
        super.push(builder.toString());
        super.push(emotionId);
        super.push(linkRefs.size());

        for (final Entry<Integer, String> entry : linkRefs.entrySet()) {
            String url = entry.getValue();

            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;

            super.push(url); // todo make secure
            super.push(entry.getValue());
            super.push(true); // is trusted?

        }

        super.push(0);
    }

}
