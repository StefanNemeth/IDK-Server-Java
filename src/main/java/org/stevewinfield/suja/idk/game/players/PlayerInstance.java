/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.players;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.game.inventory.PlayerInventory;
import org.stevewinfield.suja.idk.game.levels.ClubSubscription;
import org.stevewinfield.suja.idk.game.levels.ClubSubscriptionLevel;
import org.stevewinfield.suja.idk.game.levels.LevelRight;
import org.stevewinfield.suja.idk.game.rooms.RoomInformation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerInstance {
    private static final Logger logger = Logger.getLogger(PlayerInstance.class);

    public PlayerInformation getInformation() {
        return this.information;
    }

    public ConcurrentHashMap<Integer, PlayerAchievement> getAchievements() {
        return achievements;
    }

    public GapList<Integer> getFavoriteRooms() {
        return favoriteRooms;
    }

    public GapList<RoomInformation> getRooms() {
        return rooms;
    }

    public ClubSubscription getSubscriptionManager() {
        return subscriptionManager;
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    public PlayerInstance() {
        this.information = new PlayerInformation();
        this.rights = new ConcurrentHashMap<>();
        this.achievements = new ConcurrentHashMap<>();
        this.favoriteRooms = new GapList<>();
        this.rooms = new GapList<>();
        this.inventory = new PlayerInventory();
    }

    public boolean hasRight(final String label) {
        return this.rights.containsKey(label);
    }

    public void load(final ResultSet set) {
        this.information.set(set);
        try {
            if (this.information.getAvailableRespects() < 3) {
                final Calendar cal = Calendar.getInstance();

                cal.setTime(Bootloader.getDateFromTimestamp(information.getLastUpdate()));
                final Integer lastUpdate = cal.get(Calendar.DATE);

                cal.setTime(Bootloader.getDateFromTimestamp(Bootloader.getTimestamp()));
                final Integer today = cal.get(Calendar.DATE);

                if (!today.equals(lastUpdate)) {
                    Bootloader.getStorage().executeQuery("UPDATE players SET available_respects=3, last_update=" + Bootloader.getTimestamp() + " WHERE id=" + information.getId());
                    information.setAvailableRespects(3);
                }
            }
            this.subscriptionManager = new ClubSubscription(this.getInformation().getId());
            final ResultSet subscription = Bootloader.getStorage().queryParams("SELECT * FROM player_subscriptions WHERE player_id=" + this.getInformation().getId()).executeQuery();

            if (subscription.next()) {
                this.subscriptionManager.set(subscription);
            }

            final ResultSet favorite = Bootloader.getStorage().queryParams(
                    "SELECT * FROM player_room_favorites " +
                            "WHERE player_id=" + information.getId() + " " +
                            "ORDER BY id DESC LIMIT " + IDK.NAV_MAX_FAVORITES
            ).executeQuery();
            while (favorite.next()) {
                this.favoriteRooms.add(favorite.getInt("room_id"));
            }
            favorite.close();
            final ResultSet roomRow = Bootloader.getStorage().queryParams(
                    "SELECT rooms.*, nickname FROM rooms, players " +
                            "WHERE owner_id=" + information.getId() + " " +
                            "AND players.id=owner_id " +
                            "ORDER BY name ASC" + (!this.hasRight("unlimited_rooms") ? " " +
                            "LIMIT " + IDK.NAV_MAX_ROOMS_PER_PLAYER : "")
            ).executeQuery();
            while (roomRow.next()) {
                final RoomInformation info = new RoomInformation();
                info.set(roomRow);
                this.rooms.add(info);
            }
            roomRow.close();
            this.inventory.load(this.information.getId());
            final ResultSet achievements = Bootloader.getStorage().queryParams("SELECT * FROM player_achievements WHERE player_id=" + information.getId()).executeQuery();
            while (achievements.next()) {
                if (this.achievements.containsKey(achievements.getInt("achievement_id"))) {
                    continue;
                }
                final PlayerAchievement achievement = new PlayerAchievement();
                achievement.set(achievements);
                this.achievements.put(achievements.getInt("achievement_id"), achievement);
            }
            achievements.close();
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
        for (final LevelRight right : this.information.getLevel().getRights()) {
            this.rights.put(right.getLabel(), right);
        }
        for (final LevelRight right : Bootloader.getGame().getLevelManager().getSpecialRights(this.information.getPlayerName())) {
            this.rights.put(right.getLabel(), right);
        }
    }

    public boolean hasClub() {
        return this.hasRight("club_regular") || this.subscriptionManager.getBaseLevel() >= ClubSubscriptionLevel.BASIC;
    }

    public boolean hasVIP() {
        return this.hasRight("club_vip") || this.subscriptionManager.getBaseLevel() >= ClubSubscriptionLevel.VIP;
    }

    public void addRoomToList(final RoomInformation info) {
        this.rooms.add(info);
    }

    public PlayerAchievement setAchievement(final int achievementId, final int level, final int progress) {
        if (!this.achievements.containsKey(achievementId)) {
            final PlayerAchievement ach = new PlayerAchievement(this.getInformation().getId(), achievementId, progress, level);
            this.achievements.put(achievementId, ach);
            return ach;
        }
        this.achievements.get(achievementId).set(this.achievements.get(achievementId).getProgress() + progress, level);
        return this.achievements.get(achievementId);
    }

    // fields
    private final PlayerInformation information;
    private final ConcurrentHashMap<String, LevelRight> rights;
    private final ConcurrentHashMap<Integer, PlayerAchievement> achievements;
    private final GapList<Integer> favoriteRooms;
    private final GapList<RoomInformation> rooms;
    private final PlayerInventory inventory;
    private ClubSubscription subscriptionManager;
}
