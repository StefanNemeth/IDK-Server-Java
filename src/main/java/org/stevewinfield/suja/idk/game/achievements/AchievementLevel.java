/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.achievements;

public class AchievementLevel {
    // getters
    public int getLevel() {
        return level;
    }

    public int getPixelsReward() {
        return pixelsReward;
    }

    public int getPointsReward() {
        return pointsReward;
    }

    public int getRequirement() {
        return requirement;
    }

    public AchievementLevel(final int level, final int pixelsReward, final int pointsReward, final int requirement) {
        this.level = level;
        this.pixelsReward = pixelsReward;
        this.pointsReward = pointsReward;
        this.requirement = requirement;
    }

    // fields
    private final int level;
    private final int pixelsReward;
    private final int pointsReward;
    private final int requirement;
}
