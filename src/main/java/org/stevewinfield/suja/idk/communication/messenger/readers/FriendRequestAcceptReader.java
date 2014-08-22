/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.readers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.friendstream.FriendStream;
import org.stevewinfield.suja.idk.game.friendstream.FriendStreamEventType;
import org.stevewinfield.suja.idk.game.friendstream.FriendStreamLinkType;
import org.stevewinfield.suja.idk.game.messenger.MessengerBuddy;
import org.stevewinfield.suja.idk.game.players.PlayerInformation;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class FriendRequestAcceptReader implements IMessageReader {
    private static Logger logger = Logger.getLogger(FriendRequestAcceptReader.class);

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;

        int amount = reader.readInteger();

        if (amount > 50)
            amount = 50;

        final List<Session> newFriends = new GapList<Session>();
        final String updateString = "UPDATE player_friends SET is_request=0 WHERE player_acc_id="
        + session.getPlayerInstance().getInformation().getId();
        String whereString = "";

        for (int i = 0; i < amount; i++) {
            final int playerId = reader.readInteger();
            if (session.getPlayerMessenger().getRequests().containsKey(playerId)) {
                final Session friend = Bootloader.getSessionManager().getAuthenticatedSession(playerId);
                if (friend != null) {
                    newFriends.add(friend);
                }
                whereString += " OR player_req_id=" + playerId;
                session.getPlayerMessenger().getRequests().remove(playerId);

                ResultSet userInfo;
                try {
                    userInfo = Bootloader
                    .getStorage()
                    .queryParams(
                    "SELECT id, nickname, figurecode, motto, gender, stream_enabled FROM players WHERE id = "
                    + playerId).executeQuery();
                    if (userInfo.next()) {
                        final MessengerBuddy buddy = new MessengerBuddy();
                        buddy.set(userInfo);
                        if (session.getPlayerInstance().getInformation().isStreamEnabled())
                            session.getFriendStream().broadcastEvent(session, FriendStreamEventType.FRIEND_MADE,
                            FriendStreamLinkType.FRIEND_REQUEST,
                            new String[] { playerId + "", userInfo.getString("nickname") });
                        session.getPlayerMessenger().getBuddies().put(playerId, buddy);
                        if (buddy.isOnline()) {
                            session.getPlayerMessenger().onStatusChanged(playerId, true);
                            if (userInfo.getInt("stream_enabled") == 1)
                                buddy
                                .getSession()
                                .getFriendStream()
                                .broadcastEvent(
                                buddy.getSession(),
                                FriendStreamEventType.FRIEND_MADE,
                                FriendStreamLinkType.FRIEND_REQUEST,
                                new String[] { session.getPlayerInstance().getInformation().getId() + "",
                                        session.getPlayerInstance().getInformation().getPlayerName() });
                        } else if (userInfo.getInt("stream_enabled") == 1) {
                            session.getPlayerMessenger().onStatusChanged(playerId, false);
                            final GapList<Integer> friends = new GapList<Integer>();
                            final ResultSet row = Bootloader
                            .getStorage()
                            .queryParams(
                            "SELECT player_acc_id, player_req_id, is_request  FROM player_friends WHERE player_req_id = "
                            + playerId + " OR player_acc_id = " + playerId + " ORDER BY id").executeQuery();
                            while (row.next()) {
                                if (row.getInt("is_request") == 1)
                                    continue;
                                int mId = 0;
                                if (row.getInt("player_acc_id") != playerId) {
                                    mId = row.getInt("player_acc_id");
                                } else {
                                    mId = row.getInt("player_req_id");
                                }
                                friends.add(mId);
                            }
                            new FriendStream(playerId, friends).broadcastEvent(playerId,
                            userInfo.getString("nickname"), userInfo.getString("figurecode"),
                            userInfo.getString("gender").toLowerCase() == "m" ? PlayerInformation.MALE_GENDER
                            : PlayerInformation.FEMALE_GENDER, FriendStreamEventType.FRIEND_MADE,
                            FriendStreamLinkType.FRIEND_REQUEST, new String[] {
                                    session.getPlayerInstance().getInformation().getId() + "",
                                    session.getPlayerInstance().getInformation().getPlayerName() });
                        }
                        session.getFriendStream().addFriend(playerId);
                    }
                    userInfo.close();
                } catch (final SQLException e) {
                    logger.error("SQL Exception", e);
                }
            }
        }

        if (whereString != "") {
            Bootloader.getStorage().executeQuery(updateString + " AND (" + whereString.substring(4) + ")");
        }

        for (final Session newFriend : newFriends) {
            if (newFriend.isAuthenticated()) {
                final MessengerBuddy buddy = new MessengerBuddy();
                buddy.set(session.getPlayerInstance().getInformation().getId(), session.getPlayerInstance()
                .getInformation().getPlayerName(), session.getPlayerInstance().getInformation().getAvatar(), session
                .getPlayerInstance().getInformation().getMission());
                newFriend.getPlayerMessenger().getBuddies()
                .put(session.getPlayerInstance().getInformation().getId(), buddy);
                newFriend.getPlayerMessenger().onStatusChanged(session.getPlayerInstance().getInformation().getId(),
                true);
                newFriend.getFriendStream().addFriend(session.getPlayerInstance().getInformation().getId());
            }
        }
    }

}
