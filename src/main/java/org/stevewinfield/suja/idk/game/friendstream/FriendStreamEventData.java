/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.friendstream;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.ISerialize;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.game.players.PlayerInformation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendStreamEventData implements ISerialize {
    private static final Logger logger = Logger.getLogger(FriendStreamEventData.class);

    public int getPostedTimeSpan() {
        final long diff[] = new long[]{0, 0, 0, 0, 0};
        long diffInSeconds = (Bootloader.getTimestamp() - timestamp);
        diff[4] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        diff[3] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        return (int) diff[2];
    }

    public int getEventType() {
        return this.eventType;
    }

    public String[] getEventData() {
        return this.eventData;
    }

    public int getId() {
        return this.id;
    }

    public FriendStreamEventData() {
        this.eventData = new String[0];
    }

    public FriendStreamEventData(final int id, final int playerId, final String name, final int gender,
                                 final String avatar, final int eventType, final long timestamp, final int linkType,
                                 final String[] eventData) {
        this.id = id;
        this.playerId = playerId;
        this.playerName = name;
        this.playerGender = gender;
        this.playerAvatar = avatar;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.linkType = linkType;
        this.eventData = eventData;
        this.image = String.format(IDK.FRIENDSTREAM_AVATAR_URL, avatar);
    }

    public void set(final ResultSet row) {
        try {
            this.id = row.getInt("id");
            this.playerId = row.getInt("player_id");
            this.playerName = row.getString("player_name");
            this.playerGender = row.getString("player_gender").toLowerCase().equals("f") ? PlayerInformation.FEMALE_GENDER : PlayerInformation.MALE_GENDER;
            this.playerAvatar = row.getString("player_avatar");
            this.eventType = row.getInt("event_type");
            this.timestamp = row.getInt("timestamp");
            this.linkType = row.getInt("link_type");
            this.likes = row.getInt("event_likes");
            this.eventData = row.getString("event_data").split((char) 13 + "");
            this.image = String.format(IDK.FRIENDSTREAM_AVATAR_URL, this.playerAvatar);
        } catch (final SQLException ex) {
            logger.error("SQL Exception", ex);
        }
    }

    @Override
    public void serialize(final MessageWriter writer) {
        writer.push(id);
        writer.push(eventType);
        writer.push("");
        writer.push(playerName);
        writer.push(playerGender == PlayerInformation.MALE_GENDER ? "m" : "f"); // gender
        writer.push(image);
        writer.push(this.getPostedTimeSpan());
        writer.push(linkType);
        writer.push(likes);
        writer.push(false);

        for (final String data : this.eventData) {
            writer.push(data);
        }
    }

    // fields
    private int id;
    private int eventType;
    private int playerId;
    private int playerGender;
    private String playerAvatar;
    private String playerName;
    private String image;
    private long timestamp;
    private int linkType;
    private int likes;
    private String[] eventData;
}
