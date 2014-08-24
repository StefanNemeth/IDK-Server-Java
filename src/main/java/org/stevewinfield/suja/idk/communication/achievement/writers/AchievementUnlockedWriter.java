/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.achievement.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.achievements.Achievement;

public class AchievementUnlockedWriter extends MessageWriter {

    public AchievementUnlockedWriter(final Achievement achievement, final int level, final int pointsReward, final int pixelsReward) {
        super(OperationCodes.getOutgoingOpCode("AchievementUnlocked"));
        super.push(achievement.getId());
        super.push(level);
        super.push(144);
        super.push(achievement.getGroupName() + level);
        super.push(pointsReward);
        super.push(pixelsReward);
        super.push(0);
        super.push(10);
        super.push(21);
        super.push(level > 1 ? achievement.getGroupName() + (level - 1) : "");
        super.push(achievement.getCategory());
    }

}
