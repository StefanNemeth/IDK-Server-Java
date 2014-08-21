/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.messenger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.messenger.writers.MessageReceivedWriter;
import org.stevewinfield.suja.idk.communication.messenger.writers.MessengerInviteReceived;
import org.stevewinfield.suja.idk.communication.messenger.writers.MessengerUpdateListWriter;
import org.stevewinfield.suja.idk.game.players.PlayerInformation;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class Messenger {
    private static Logger logger = Logger.getLogger(Messenger.class);

    // getters
    public PlayerInformation getPlayerInformation() {
        return player;
    }

    public ConcurrentHashMap<Integer, MessengerRequest> getRequests() {
        return requests;
    }

    public ConcurrentHashMap<Integer, MessengerBuddy> getBuddies() {
        return buddies;
    }

    public ConcurrentHashMap<Integer, MessengerBuddy> getOnlineBuddies() {
        return onlineBuddies;
    }

    public void onMessageReceived(final int buddyId, final String message) {
        if (this.getBuddies().containsKey(buddyId)) {
            this.session.writeMessage(new MessageReceivedWriter(buddyId, message));
        }
    }

    public void onInviteReceived(final int buddyId, final String message) {
        if (this.getBuddies().containsKey(buddyId)) {
            this.session.writeMessage(new MessengerInviteReceived(buddyId, message));
        }
    }

    public void onStatusChanged(final int buddyId) {
        if (this.onlineBuddies.containsKey(buddyId)) {
            this.buddies.get(buddyId).update();
            this.session.writeMessage(new MessengerUpdateListWriter(this.buddies.get(buddyId)));
        }
    }

    public void onStatusChanged(final int buddyId, final boolean goes) {
        if (this.onlineBuddies.containsKey(buddyId) && !goes) {
            this.onlineBuddies.get(buddyId).setSession(null);
            this.onlineBuddies.remove(buddyId);
            this.session.writeMessage(new MessengerUpdateListWriter(this.buddies.get(buddyId)));
        } else if (!this.onlineBuddies.containsKey(buddyId) && goes) {
            this.buddies.get(buddyId).setSession(Bootloader.getSessionManager().getAuthenticatedSession(buddyId));
            this.onlineBuddies.put(buddyId, this.buddies.get(buddyId));
            this.session.writeMessage(new MessengerUpdateListWriter(this.buddies.get(buddyId)));
        }
    }

    public Messenger(final Session session, final PlayerInformation info) {
        this.session = session;
        this.player = info;
        this.buddies = new ConcurrentHashMap<Integer, MessengerBuddy>();
        this.onlineBuddies = new ConcurrentHashMap<Integer, MessengerBuddy>();
        this.requests = new ConcurrentHashMap<Integer, MessengerRequest>();
        try {
            final ResultSet row = Bootloader
            .getStorage()
            .queryParams(
            "SELECT * FROM player_friends WHERE player_req_id = " + this.player.getId() + " OR player_acc_id = "
            + this.player.getId() + " ORDER BY id").executeQuery();
            while (row.next()) {
                if (row.getInt("is_request") == 1) {
                    if (row.getInt("player_req_id") != player.getId()
                    && !this.requests.containsKey(row.getInt("player_req_id"))) {
                        final MessengerRequest request = new MessengerRequest();
                        request.set(row);
                        this.requests.put(row.getInt("player_req_id"), request);
                    }
                    continue;
                }
                int playerId = 0;
                if (row.getInt("player_acc_id") != player.getId()) {
                    playerId = row.getInt("player_acc_id");
                } else {
                    playerId = row.getInt("player_req_id");
                }
                if (playerId > 0 && !this.buddies.containsKey(playerId)) {
                    final ResultSet userInfo = Bootloader.getStorage()
                    .queryParams("SELECT id, nickname, figurecode, motto FROM players WHERE id = " + playerId)
                    .executeQuery();
                    if (userInfo.next()) {
                        final MessengerBuddy buddy = new MessengerBuddy();
                        buddy.set(userInfo);
                        this.buddies.put(playerId, buddy);
                        if (buddy.isOnline())
                            this.onlineBuddies.put(playerId, buddy);
                    }
                    userInfo.close();
                }
            }
            row.close();
            for (final MessengerBuddy buddy : this.getOnlineBuddies().values()) {
                buddy.getSession().getPlayerMessenger().onStatusChanged(info.getId(), true);
            }
        } catch (final SQLException ex) {
            logger.error("SQL Exception", ex);
        }
    }

    // fields
    private final Session session;
    private final PlayerInformation player;
    private final ConcurrentHashMap<Integer, MessengerBuddy> buddies;
    private final ConcurrentHashMap<Integer, MessengerBuddy> onlineBuddies;
    private final ConcurrentHashMap<Integer, MessengerRequest> requests;
}
