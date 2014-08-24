/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.friendstream;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.players.PlayerInformation;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;

public class FriendStream {
    private static Logger logger = Logger.getLogger(FriendStream.class);

    public Collection<FriendStreamEventData> getEvents() {
        return events.values();
    }

    public GapList<Integer> getFriends() {
        this.updateNeeded = false;
        return friends;
    }

    public boolean needsUpdate() {
        return updateNeeded;
    }

    public FriendStream(final int playerId, final GapList<Integer> friends) {
        this.playerId = playerId;
        this.friends = friends;
        this.events = new LinkedHashMap<Integer, FriendStreamEventData>();
    }

    public FriendStream() {
        this.events = new LinkedHashMap<Integer, FriendStreamEventData>();
    }

    public void load(final int playerId, final GapList<Integer> friends) {
        String queryData = "";

        for (final Integer id : friends) {
            queryData += " OR player_id=" + id;
        }

        if (queryData.length() > 0) {
            queryData = "WHERE" + queryData.substring(3);
        }

        this.playerId = playerId;
        this.friends = friends;
        this.events = new LinkedHashMap<Integer, FriendStreamEventData>();
        try {
            final ResultSet set = Bootloader.getStorage().queryParams("SELECT * FROM friendstream_events " + queryData).executeQuery();
            while (set.next()) {
                final FriendStreamEventData event = new FriendStreamEventData();
                event.set(set);
                if (event.getEventType() == FriendStreamEventType.FRIEND_MADE && event.getEventData()[0].equals("" + playerId)) {
                    continue;
                }
                events.put(set.getInt("id"), event);
            }
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

    public void broadcastEvent(final Session session, final int eventType, final int linkType, final String[] eventData) {
        this.broadcastEvent(session.getPlayerInstance().getInformation().getId(), session.getPlayerInstance().getInformation().getPlayerName(), session.getPlayerInstance().getInformation().getAvatar(), session.getPlayerInstance().getInformation().getGender(), eventType, linkType, eventData);
    }

    public void broadcastEvent(final int playerId, final String name, final String avatar, final int gender, final int eventType, final int linkType, final String[] eventData) {
        final long timestamp = Bootloader.getTimestamp();
        int itemId = 0;
        String data = "";
        for (final String entry : eventData) {
            data += (char) 13 + entry;
        }
        if (data.length() > 0) {
            data = data.substring(1);
        }
        try {
            final PreparedStatement std = Bootloader.getStorage().queryParams("INSERT INTO friendstream_events (player_id, player_name, player_avatar, player_gender, event_type, timestamp, link_type, event_data) VALUES" + "(" + playerId + ", ?, ?, '" + (gender == PlayerInformation.MALE_GENDER ? "m" : "f") + "', " + eventType + ", '" + timestamp + "', " + linkType + ", ?)");
            std.setString(1, name);
            std.setString(2, avatar);
            std.setString(3, data);
            std.execute();
            std.close();
            itemId = Bootloader.getStorage().readLastId("friendstream_events");
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
        final FriendStreamEventData event = new FriendStreamEventData(itemId, playerId, name, gender, avatar, eventType, timestamp, linkType, eventData);
        for (final Integer i : this.friends) {
            Session friend = null;
            if (!Bootloader.getSessionManager().hasAuthenticatedSession(i) || !(friend = Bootloader.getSessionManager().getAuthenticatedSession(i)).isAuthenticated()) {
                continue;
            }
            friend.getFriendStream().onEvent(event);
        }
    }

    public void addFriend(final int friendId) {
        this.friends.add(friendId);
        this.load(this.playerId, this.friends);
    }

    public void removeFriend(final int friendId) {
        this.friends.remove(friendId);
        this.load(this.playerId, this.friends);
    }

    public void onEvent(final FriendStreamEventData event) {
        this.events.put(event.getId(), event);
        this.updateNeeded = true;
    }

    // fields
    private int playerId;
    private GapList<Integer> friends;
    private boolean updateNeeded;
    private LinkedHashMap<Integer, FriendStreamEventData> events;
}
