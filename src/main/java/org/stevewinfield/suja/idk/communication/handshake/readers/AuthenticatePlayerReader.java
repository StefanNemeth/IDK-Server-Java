/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.handshake.readers;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.Translations;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.QueuedMessageWriter;
import org.stevewinfield.suja.idk.communication.achievement.writers.AchievementDataListWriter;
import org.stevewinfield.suja.idk.communication.friendstream.writers.FriendStreamEventWriter;
import org.stevewinfield.suja.idk.communication.handshake.writers.AuthenticatedWriter;
import org.stevewinfield.suja.idk.communication.moderation.writers.ModerationToolWriter;
import org.stevewinfield.suja.idk.communication.player.writers.*;
import org.stevewinfield.suja.idk.game.friendstream.FriendStreamEventData;
import org.stevewinfield.suja.idk.game.levels.ClubSubscriptionLevel;
import org.stevewinfield.suja.idk.game.miscellaneous.NotifyType;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthenticatePlayerReader implements IMessageReader {
    private static Logger logger = Logger.getLogger(AuthenticatePlayerReader.class);

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (session.isAuthenticated()) {
            return;
        }

        if (!session.tryAuthenticate(reader.readUTF())) {
            session.sendNotification(NotifyType.MULTI_ALERT, Translations.getTranslation("fail_authenticate"));
            return;
        }

        if (IDK.DEBUG) {
            logger.debug(session.getPlayerInstance().getInformation().getPlayerName() + " logged in.");
        }

        final QueuedMessageWriter queue = new QueuedMessageWriter();
        queue.push(new AuthenticatedWriter());
        queue.push(new LevelRightsListWriter(session.getPlayerInstance().hasVIP(), session.getPlayerInstance().hasClub(), session.getPlayerInstance().hasRight("hotel_admin")));
        queue.push(new PlayerFavoriteRoomsWriter(session.getPlayerInstance().getFavoriteRooms()));
        queue.push(new AchievementDataListWriter(Bootloader.getGame().getAchievementManager().getAchievements().values()));

        if (session.getPlayerInstance().hasRight("moderation_tool")) {
            queue.push(new ModerationToolWriter(session.getPlayerInstance(), Bootloader.getGame().getModerationManager().getPresetMessages().values(), Bootloader.getGame().getModerationManager().getPresetActions()));
        }

        queue.push(new AvailabilityStatusWriter());
        queue.push(new InfoFeedEnableMessageWriter(true)); // todo
        queue.push(new ActivityPointsWriter(session.getPlayerInstance().getInformation().getPixelsBalance(), session.getPlayerInstance().getInformation().getShellsBalance()));

        final long timestamp = Bootloader.getTimestamp();
        final long diff[] = new long[]{0, 0, 0, 0, 0};

        if (session.getPlayerInstance().getSubscriptionManager().getExpireTime() >= timestamp) {
            long diffInSeconds = (session.getPlayerInstance().getSubscriptionManager().getExpireTime() - timestamp);
            diff[4] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
            diff[3] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
            diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
            diff[1] = (diffInSeconds = (diffInSeconds / 24));
            diff[0] = (diffInSeconds = (diffInSeconds / 30)) >= 12 ? diffInSeconds % 12 : diffInSeconds;
            diff[0] = (long) Math.floor(diff[0] / 31.0);
        }

        final int days = (int) (diff[1] - (31 * diff[0]));

        queue.push(new SubscriptionInfoWriter("club_habbo", days, (session.getPlayerInstance().getSubscriptionManager().getBaseLevel() != ClubSubscriptionLevel.NONE && days > 5 ? 2 : 0), (session.getPlayerInstance().getSubscriptionManager().getBaseLevel() == ClubSubscriptionLevel.VIP), session.getPlayerInstance().getSubscriptionManager().getBaseLevel() == ClubSubscriptionLevel.NONE, IDK.CATA_CLUB_OFFER_PRICE_REGULAR, IDK.CATA_CLUB_OFFER_PRICE_NOW));

        if (session.getPlayerInstance().getInformation().isStreamEnabled()) {
            final List<FriendStreamEventData> events = new ArrayList<FriendStreamEventData>(session.getFriendStream().getEvents());
            Collections.reverse(events);

            queue.push(new FriendStreamEventWriter(events));
        }

        session.writeMessage(queue);
        session.writeMessage(new PlayerHomeRoomWriter(session.getPlayerInstance().getInformation().getHomeRoom()));

    }
}