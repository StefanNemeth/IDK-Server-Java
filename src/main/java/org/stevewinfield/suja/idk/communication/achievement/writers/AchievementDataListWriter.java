/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.achievement.writers;

import java.util.Collection;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.achievements.Achievement;
import org.stevewinfield.suja.idk.game.achievements.AchievementLevel;

public class AchievementDataListWriter extends MessageWriter {

    public AchievementDataListWriter(final Collection<Achievement> achievements) {
        super(OperationCodes.getOutgoingOpCode("AchievementDataList"));
        super.push(achievements.size());

        for (final Achievement achievement : achievements) {
            super.push(achievement.getGroupName().startsWith("ACH_") ? achievement.getGroupName().substring(4)
            : achievement.getGroupName());

            final Collection<AchievementLevel> levels = achievement.getLevels().values();
            super.push(levels.size());

            for (final AchievementLevel level : levels) {
                super.push(level.getLevel());
                super.push(level.getRequirement());
            }
        }
    }

}
