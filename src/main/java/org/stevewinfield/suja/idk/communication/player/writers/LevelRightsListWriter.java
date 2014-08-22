/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class LevelRightsListWriter extends MessageWriter {

    public LevelRightsListWriter(final boolean hasVIP, final boolean hasClub, final boolean hasAdmin) {
        super(OperationCodes.getOutgoingOpCode("LevelRightsWriter"));
        super.push(hasVIP ? 2 : (hasClub ? 1 : 0));
        super.push(hasAdmin ? 1000 : 0);
    }

}
