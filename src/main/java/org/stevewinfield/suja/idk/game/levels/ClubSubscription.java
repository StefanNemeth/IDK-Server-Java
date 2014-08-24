/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.levels;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.catalog.writers.MonthlyClubGiftChoosedWriter;
import org.stevewinfield.suja.idk.game.catalog.CatalogClubGift;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClubSubscription {
    private static final Logger logger = Logger.getLogger(ClubSubscription.class);

    // getters
    public int getPlayerId() {
        return playerId;
    }

    public boolean isActive() {
        return baseLevel != ClubSubscriptionLevel.NONE && Bootloader.getTimestamp() < expire;
    }

    public int getBaseLevel() {
        return baseLevel;
    }

    public long getCreatedTime() {
        return created;
    }

    public long getExpireTime() {
        return expire;
    }

    public long getClubTime() {
        return hcTime;
    }

    public long getVIPTime() {
        return vipTime;
    }

    public int getChoosenGifts() {
        return choosenGifts;
    }

    public boolean hadMembership() {
        return this.created > 0 || this.vipTime > 0 || this.hcTime > 0;
    }

    public int getAvailableGifts() {
        return this.hadMembership() ? (((int) Math.ceil(((int) (this.getPastClubTime() / 86400) + (int) (this.getPastVipTime() / 86400)) / 31)) - choosenGifts + 1) : 0;
    }

    public int getNextGiftSpan() {
        return this.hadMembership() ? 31 - (((int) (this.getPastClubTime() / 86400) + (int) (this.getPastVipTime() / 86400)) % 31) : 0;
    }

    public ClubSubscription(final int playerId) {
        this.playerId = playerId;
        this.baseLevel = ClubSubscriptionLevel.NONE;
    }

    public long getPastVipTime() {
        long time = vipTime;

        if (baseLevel == ClubSubscriptionLevel.VIP) {
            time += Bootloader.getTimestamp() - created;
        }

        return time;
    }

    public long getPastClubTime() {
        long time = hcTime;

        if (baseLevel == ClubSubscriptionLevel.BASIC) {
            time += Bootloader.getTimestamp() - created;
        }

        return time;
    }

    public void expire() {
        hcTime = this.getPastClubTime();
        vipTime = this.getPastVipTime();
        baseLevel = ClubSubscriptionLevel.NONE;
        created = 0;
        expire = 0;
        Bootloader.getStorage()
                .executeQuery("UPDATE player_subscriptions SET " +
                                "past_time_hc=" + hcTime + ", " +
                                "past_time_vip=" + vipTime + ", " +
                                "subscription_level=0, " +
                                "timestamp_created=0, " +
                                "timestamp_expire=0 " +
                                "WHERE player_id=" + this.playerId
                );
    }

    public void set(final ResultSet row) {
        try {
            this.baseLevel = row.getInt("subscription_level");
            this.created = row.getInt("timestamp_created");
            this.expire = row.getInt("timestamp_expire");
            this.hcTime = row.getInt("past_time_hc");
            this.vipTime = row.getInt("past_time_vip");
            this.choosenGifts = row.getInt("choosen_gifts");

            if (!this.isActive()) // expire if subscription is expired
            {
                this.expire();
            }
        } catch (final SQLException ex) {
            logger.error("SQL Exception", ex);
        }
    }

    public void chooseClubGift(final CatalogClubGift gift, final Session session) {
        Bootloader.getStorage().executeQuery("UPDATE player_subscriptions SET choosen_gifts=" + ++this.choosenGifts + "  WHERE player_id=" + this.playerId);
        session.getPlayerInstance().getInventory().addItem(gift.getBase(), session, 1, "", null);

        session.writeMessage(new MonthlyClubGiftChoosedWriter(gift.getName(), gift));
    }

    public void update(final int level, final int time) {
        final long timestamp = Bootloader.getTimestamp();
        final boolean saveClubTime = this.baseLevel == ClubSubscriptionLevel.BASIC && level > ClubSubscriptionLevel.BASIC;

        if (saveClubTime) {
            this.hcTime = this.getPastClubTime();
        }

        if (!this.isActive()) {
            this.created = timestamp;
            this.expire = timestamp;
        }

        this.expire += time;
        this.baseLevel = level;

        if (Bootloader.getStorage().entryExists("SELECT player_id FROM player_subscriptions WHERE player_id=" + this.playerId)) {
            Bootloader.getStorage().executeQuery(
                    "UPDATE player_subscriptions SET " +
                            "subscription_level=" + level + ", " +
                            "timestamp_expire=" + expire +
                            (saveClubTime ? ", past_time_hc=" + (int) this.hcTime : "") + " " +
                            "WHERE player_id=" + this.playerId
            );
        } else {
            Bootloader.getStorage().executeQuery(
                    "INSERT INTO player_subscriptions (player_id, subscription_level, timestamp_created, timestamp_expire) " +
                            "VALUES (" + this.playerId + ", " + this.baseLevel + ", '" + this.created + "', '" + this.expire + "')"
            );
        }
    }

    private final int playerId;
    private int baseLevel;
    private int choosenGifts;
    private long created;
    private long expire;
    private long hcTime;
    private long vipTime;
}
