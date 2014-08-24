/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.network.sessions;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.QueuedMessageWriter;
import org.stevewinfield.suja.idk.communication.global.writers.GenericErrorWriter;
import org.stevewinfield.suja.idk.communication.miscellaneous.writers.ModeratorNotificationWriter;
import org.stevewinfield.suja.idk.communication.miscellaneous.writers.MultiNotificationWriter;
import org.stevewinfield.suja.idk.communication.miscellaneous.writers.StaffNotificationWriter;
import org.stevewinfield.suja.idk.communication.player.writers.PlayerInfoUpdateWriter;
import org.stevewinfield.suja.idk.communication.room.writers.*;
import org.stevewinfield.suja.idk.game.friendstream.FriendStream;
import org.stevewinfield.suja.idk.game.messenger.Messenger;
import org.stevewinfield.suja.idk.game.messenger.MessengerBuddy;
import org.stevewinfield.suja.idk.game.miscellaneous.NotifyType;
import org.stevewinfield.suja.idk.game.players.PlayerAchievement;
import org.stevewinfield.suja.idk.game.players.PlayerInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomAccessType;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.RoomType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;

public class Session {
    private static Logger logger = Logger.getLogger(Session.class);

    // getters
    public int getId() {
        return id;
    }

    public Channel getChannel() {
        return channel;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public int getRoomRequestId() {
        return roomRequestId;
    }

    public PlayerInstance getPlayerInstance() {
        return playerInstance;
    }

    public Messenger getPlayerMessenger() {
        return playerMessenger;
    }

    public int getRoomId() {
        return roomId;
    }

    public FriendStream getFriendStream() {
        return friendStream;
    }

    public boolean isLoadingRoom() {
        return roomId > 0 && !roomJoined;
    }

    public boolean isInRoom() {
        return roomId > 0 && roomJoined && roomPlayer != null;
    }

    public boolean roomLoadingChecksPassed() {
        return loadingsCheckPassed;
    }

    public RoomPlayer getRoomPlayer() {
        return roomPlayer;
    }

    public int getTargetTeleporter() {
        return targetTeleporter;
    }

    public int getTargetTeleporterRoom() {
        return targetTeleporterRoom;
    }

    public boolean isTeleporting() {
        return targetTeleporter > 0;
    }

    public Session(final int id, final Channel channel) {
        this.id = id;
        this.channel = channel;
        this.authenticated = false;
    }

    public void prepareRoom(final RoomInstance room, final String password) {
        prepareRoom(room, password, false);
    }

    public void setTargetTeleporterId(final int id) {
        this.targetTeleporter = id;
    }

    public void prepareRoom(final RoomInstance room, final String password, final boolean bypassAuth) {
        if (this.isInRoom()) {
            final RoomInstance oldRoom = Bootloader.getGame().getRoomManager().loadRoomInstance(this.getRoomId());
            oldRoom.removePlayerFromRoom(this, false, false);
        }

        this.clearLoading();
        final QueuedMessageWriter queue = new QueuedMessageWriter();

        if (room.getInformation().getTotalPlayers() >= room.getInformation().getMaxPlayers() && !playerInstance.hasRight("enter_full_rooms")) {
            queue.push(new RoomJoinErrorWriter(1));
            queue.push(new RoomKickedWriter());
            this.writeMessage(queue);
            return;
        }

        this.roomId = room.getInformation().getId();
        this.roomJoined = false;
        this.loadingsCheckPassed = bypassAuth || room.getInformation().getOwnerId() == playerInstance.getInformation().getId() || playerInstance.hasRight("enter_locked_rooms");

        if (!this.loadingsCheckPassed) {
            if (room.getInformation().getAccessType() == RoomAccessType.PASSWORD && !room.getInformation().getPassword().equals(password)) {
                queue.push(new GenericErrorWriter(-100002));
                queue.push(new RoomKickedWriter());
                this.writeMessage(queue);
                return;
            } else if (room.getInformation().getAccessType() == RoomAccessType.BELL) {
                final MessageWriter target = new RoomDoorbellWriter(this.getPlayerInstance().getInformation().getPlayerName());
                int rightCount = 0;
                for (final RoomPlayer player : room.getRoomPlayers().values()) {
                    if (player.getSession() != null && room.hasRights(player.getSession())) {
                        player.getSession().writeMessage(target);
                        rightCount++;
                    }
                }
                queue.push(rightCount > 0 ? new RoomDoorbellWriter() : new RoomDoorbellNoResponseWriter());
                if (rightCount > 0) {
                    this.roomRequestId = room.getInformation().getId();
                }
                this.writeMessage(queue);
                return;
            }
            this.loadingsCheckPassed = true;
        }
        this.enterRoom(room);
    }

    public void enterRoom(final RoomInstance room) {
        if (!this.loadingsCheckPassed || this.roomJoined || this.roomId != room.getInformation().getId()) {
            return;
        }

        final QueuedMessageWriter queue = new QueuedMessageWriter();

        queue.push(new RoomOpenFlatWriter());
        queue.push(new RoomUrlWriter("http://www.twitter.com/WinfieldSteve"));
        queue.push(new RoomEntryModelWriter(room.getInformation().getModelName(), room.getInformation().getId()));

        if (room.getInformation().getRoomType() == RoomType.PRIVATE) {
            for (final Entry<String, String> decoration : room.getInformation().getDecorations().entrySet()) {
                if (!decoration.getValue().equals("0.0")) {
                    queue.push(new RoomDecorationWriter(decoration));
                }
            }
            if (room.hasRights(this, true)) {
                queue.push(new RoomRightsWriter());
                queue.push(new RoomOwnerRightsWriter());
            } else if (room.hasRights(this)) {
                queue.push(new RoomRightsWriter());
            }
            queue.push(new RoomRatingInfoWriter(this.playerInstance.getInformation().getId() == room.getInformation().getOwnerId() || room.getVotes().contains(playerInstance.getInformation().getId()) ? room.getInformation().getScore() : -1));
        }
        this.writeMessage(queue);
    }

    public void sendInformationUpdate() {
        if (!this.authenticated) {
            return;
        }

        this.writeMessage(new PlayerInfoUpdateWriter(-1, this.playerInstance.getInformation().getAvatar(), this.playerInstance.getInformation().getGender(), this.playerInstance.getInformation().getMission(), this.playerInstance.getInformation().getScore()));

        if (this.isInRoom()) {
            this.roomPlayer.getRoom().writeMessage(new PlayerInfoUpdateWriter(this.roomPlayer.getVirtualId(), this.playerInstance.getInformation().getAvatar(), this.playerInstance.getInformation().getGender(), this.playerInstance.getInformation().getMission(), this.playerInstance.getInformation().getScore()), null);
        }
    }

    public boolean tryAuthenticate(final String token) {
        try {
            final PreparedStatement statement = Bootloader.getStorage().queryParams("SELECT * FROM players WHERE auth_token = ? LIMIT 1");

            statement.setString(1, token);
            final ResultSet row = statement.executeQuery();

            if (row.next()) {
                final Session checkSession = Bootloader.getSessionManager().getAuthenticatedSession(row.getInt("id"));
                if (checkSession != null) {
                    checkSession.disconnect();
                }
                final int timestamp = (int) Bootloader.getTimestamp();
                this.playerInstance = new PlayerInstance();
                this.playerInstance.load(row);
                Bootloader.getStorage().executeQuery("UPDATE players SET last_login_timestamp=" + timestamp + " WHERE id=" + row.getInt("id"));
                this.playerInstance.getInformation().setLastLoginTimestamp(timestamp);
                Bootloader.getSessionManager().makeAuthenticatedSession(this.playerInstance.getInformation().getId(), this);
                this.playerMessenger = new Messenger(this, this.playerInstance.getInformation());
                this.friendStream = new FriendStream();
                final GapList<Integer> friends = new GapList<Integer>();
                for (final Integer id : playerMessenger.getBuddies().keySet()) {
                    friends.add(id);
                }
                this.friendStream.load(this.playerInstance.getInformation().getId(), friends);
                this.authenticated = true;
                return true;
            }
        } catch (final SQLException exception) {
            logger.error("SQL Exception", exception);
        }
        return false;
    }

    public void disconnect() {
        if (!this.getChannel().isConnected()) {
            return;
        }
        this.getChannel().disconnect();
    }

    public void sendNotification(final int notifyType, final String message, final String url) {
        switch (notifyType) {
            case NotifyType.MOD_ALERT:
                this.writeMessage(new ModeratorNotificationWriter(message, url));
                return;
            case NotifyType.STAFF_ALERT:
                this.writeMessage(new StaffNotificationWriter(message, url));
                return;
            case NotifyType.MULTI_ALERT:
                this.writeMessage(new MultiNotificationWriter(message));
                break;
        }
    }

    public void sendNotification(final int notifyType, final String message) {
        this.sendNotification(notifyType, message, "");
    }

    public void writeMessage(final MessageWriter writer) {
        if (!this.getChannel().isConnected()) {
            return;
        }
        if (IDK.DEBUG) {
            logger.debug("SND #" + writer.getId() + " " + writer.getDebugString().replace((char) 13, ' ').replace((char) 10, ' '));
        }
        this.getChannel().write(writer);
    }

    public void setTargetTeleporterRoom(final int id) {
        this.targetTeleporterRoom = id;
    }

    public void writeMessage(final QueuedMessageWriter writer) {
        if (!this.getChannel().isConnected()) {
            return;
        }
        if (IDK.DEBUG) {
            logger.debug("SND #QUEUE " + writer.getDebugString().replace((char) 13, ' ').replace((char) 10, ' '));
        }
        this.getChannel().write(writer);

    }

    public void clearLoading() {
        this.roomId = 0;
        this.roomJoined = false;
        this.loadingsCheckPassed = false;
    }

    public void setLoadingRoomId(final int roomId) {
        this.roomId = roomId;
    }

    public void setRoomPlayer(final RoomPlayer player) {
        this.roomPlayer = player;
    }

    public void setLoadingChecksPassed(final boolean passed) {
        this.loadingsCheckPassed = passed;
    }

    public void setRoomJoined(final boolean joined) {
        this.roomJoined = joined;
    }

    public void dispose() {
        try {
            if (this.isAuthenticated()) {
                StringBuilder insertAchievementQuery = new StringBuilder();
                StringBuilder updateAchievementQuery = new StringBuilder();
                for (final PlayerAchievement achievement : this.playerInstance.getAchievements().values()) {

                    if (achievement.toInsert()) {
                        insertAchievementQuery.append(", (" + playerInstance.getInformation().getId() + ", " + achievement.getAchievementId() + ", " + achievement.getLevel() + ", " + achievement.getProgress() + ")");
                    } else if (achievement.toUpdate()) {
                        updateAchievementQuery.append(", (" + achievement.getId() + ", " + playerInstance.getInformation().getId() + ", " + achievement.getAchievementId() + ", " + achievement.getLevel() + ", " + achievement.getProgress() + ")");
                    }
                }
                if (insertAchievementQuery.length() > 0) {
                    Bootloader.getStorage().executeQuery("INSERT INTO player_achievements (player_id, achievement_id, level, progress) VALUES " + insertAchievementQuery.toString().substring(2));
                    insertAchievementQuery = null;
                }
                if (updateAchievementQuery.length() > 0) {
                    Bootloader.getStorage().executeQuery("REPLACE INTO player_achievements (id, player_id, achievement_id, level, progress) VALUES " + updateAchievementQuery.toString().substring(2));
                    updateAchievementQuery = null;
                }
                this.playerInstance.getInventory().save(this.playerInstance.getInformation().getId());
                if (this.isInRoom()) {
                    final RoomInstance oldRoom = Bootloader.getGame().getRoomManager().loadRoomInstance(this.getRoomId());
                    oldRoom.removePlayerFromRoom(this, false, false);
                }
                for (final MessengerBuddy buddy : this.getPlayerMessenger().getOnlineBuddies().values()) {
                    buddy.getSession().getPlayerMessenger().onStatusChanged(this.getPlayerInstance().getInformation().getId(), false);
                }
            }
            this.finalize();
        } catch (final Throwable e) {
            logger.error("", e);
        }
    }

    // fields
    private final int id;
    private final Channel channel;
    private PlayerInstance playerInstance;
    private Messenger playerMessenger;
    private FriendStream friendStream;
    private RoomPlayer roomPlayer;
    private boolean authenticated;
    private boolean loadingsCheckPassed;
    private boolean roomJoined;
    private int roomId;
    private int roomRequestId;
    private int targetTeleporter;
    private int targetTeleporterRoom;

}
