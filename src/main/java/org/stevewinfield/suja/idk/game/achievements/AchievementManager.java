/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.achievements;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.achievement.writers.AchievementProgressWriter;
import org.stevewinfield.suja.idk.communication.achievement.writers.AchievementUnlockedWriter;
import org.stevewinfield.suja.idk.communication.player.writers.ActivityPointsWriter;
import org.stevewinfield.suja.idk.game.friendstream.FriendStreamEventType;
import org.stevewinfield.suja.idk.game.players.PlayerAchievement;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class AchievementManager {
    private static Logger logger = Logger.getLogger(AchievementManager.class);

    public ConcurrentHashMap<String, Achievement> getAchievements() {
        return achievements;
    }

    public AchievementManager() {
        this.achievements = new ConcurrentHashMap<String, Achievement>();
        try {
            int achievementsLoaded = 0;
            int achievemntLevelsLoaded = 0;
            ResultSet row = Bootloader.getStorage().queryParams("SELECT * FROM achievements").executeQuery();
            while (row.next()) {
                if (!this.achievements.containsKey(row.getString("group_name"))) {
                    final Achievement achievement = new Achievement();
                    achievement.set(row);
                    this.achievements.put(row.getString("group_name"), achievement);
                    achievementsLoaded++;
                }
            }
            row = Bootloader.getStorage().queryParams("SELECT * FROM achievement_levels ORDER BY id").executeQuery();
            final int i = 1;
            while (row.next()) {
                Achievement ach = null;
                for (final Achievement entry : this.achievements.values()) {
                    if (entry.getId() == row.getInt("achievement_id")) {
                        ach = entry;
                        break;
                    }
                }
                if (ach == null)
                    continue;
                ach.addLevel(row.getInt("level_reward"), row.getInt("level_score"), row.getInt("progress_needed"));
                achievemntLevelsLoaded++;
            }
            row.close();
            final GapList<String> toRemove = new GapList<String>();
            for (final Achievement achievement : this.achievements.values()) {
                if (achievement.getLevelCount() < 1)
                    toRemove.add(achievement.getGroupName());
            }
            for (final String x : toRemove) {
                this.achievements.remove(x);
            }
            logger.info(achievementsLoaded + " Achievement(s) loaded.");
            logger.info(achievemntLevelsLoaded + " Achievement Level(s) loaded.");
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

    public boolean progressAchievement(final Session session, final String achievementName, final int progressAmount) {
        if (!this.achievements.containsKey(achievementName))
            return false;

        final Achievement achievement = this.achievements.get(achievementName);

        if (achievement == null)
            return false;

        PlayerAchievement playerAchievement = null;

        if (session.getPlayerInstance().getAchievements().containsKey(achievement.getId()))
            playerAchievement = session.getPlayerInstance().getAchievements().get(achievement.getId());

        if (playerAchievement != null && playerAchievement.getLevel() == achievement.getLevelCount())
            return false;

        int targetLevel = playerAchievement != null ? playerAchievement.getLevel() + 1 : 1;

        if (targetLevel > achievement.getLevelCount())
            targetLevel = achievement.getLevelCount();

        int newProgress = playerAchievement != null ? playerAchievement.getProgress() + progressAmount : progressAmount;
        final AchievementLevel level = achievement.getLevels().get(targetLevel);

        int newLevel = playerAchievement != null ? playerAchievement.getLevel() : 0;
        int newTarget = newLevel + 1;

        if (newTarget > achievement.getLevelCount())
            newTarget = achievement.getLevelCount();

        if (newProgress >= level.getRequirement()) {
            newLevel++;
            newTarget++;

            newProgress = 0;

            if (newTarget > achievement.getLevelCount())
                newTarget = achievement.getLevelCount();

            if (level.getPixelsReward() > 0) {
                session.getPlayerInstance().getInformation().setPixels(level.getPixelsReward());
                session.writeMessage(new ActivityPointsWriter(session.getPlayerInstance().getInformation()
                .getPixelsBalance(), session.getPlayerInstance().getInformation().getShellsBalance()));
                session.getPlayerInstance().getInformation().updateCurrencies();
            }

            session.writeMessage(new AchievementUnlockedWriter(achievement, targetLevel, level.getPointsReward(), level
            .getPixelsReward()));

            playerAchievement = session.getPlayerInstance().setAchievement(achievement.getId(), newLevel,
            progressAmount);

            session.writeMessage(new AchievementProgressWriter(achievement, newTarget, achievement.getLevels().get(
            newTarget), achievement.getLevelCount(), playerAchievement));

            session.getFriendStream().broadcastEvent(session, FriendStreamEventType.GET_ACHIVEMENT_BADGE, 0,
            new String[] { achievement.getGroupName() + newLevel });
            return true;
        }
        playerAchievement = session.getPlayerInstance().setAchievement(achievement.getId(), newLevel, progressAmount);
        session.writeMessage(new AchievementProgressWriter(achievement, targetLevel, level,
        achievement.getLevelCount(), playerAchievement));
        return false;
    }

    private final ConcurrentHashMap<String, Achievement> achievements;
}
