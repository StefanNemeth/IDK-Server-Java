/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class AchievementScoreUpdateWriter extends MessageWriter {

    public AchievementScoreUpdateWriter(final int score) {
        super(OperationCodes.getOutgoingOpCode("AchievementScoreUpdate"));
        super.push(score);
    }

}
