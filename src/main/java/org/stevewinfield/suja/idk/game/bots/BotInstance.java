/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.bots;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.players.PlayerInformation;
import org.stevewinfield.suja.idk.game.rooms.coordination.Vector3;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class BotInstance {
    private static Logger logger = Logger.getLogger(BotInstance.class);

    public int getId() {
        return id;
    }

    public int getStartRoomId() {
        return startRoomId;
    }

    public String getBotName() {
        return botName;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getGender() {
        return gender;
    }

    public String getMission() {
        return mission;
    }

    public int getStartRotation() {
        return startRotation;
    }

    public Vector3 getStartPosition() {
        return startPosition;
    }

    public IBotInteractor getInteractor() {
        return Bootloader.getGame().getBotManager().getInteractors().get(this.interactorId);
    }

    public int getInteractorId() {
        return interactorId;
    }

    public boolean isMovingEnabled() {
        return movingEnabled;
    }

    public GapList<BotPhrase> getPhrases() {
        return phrases;
    }

    public ConcurrentHashMap<String, BotKeywordReaction> getKeywordReactions() {
        return keywordReactions;
    }

    public BotInstance() {
        this.botName = "";
        this.avatar = "";
        this.mission = "";
        this.startPosition = new Vector3(0, 0, 0);
        this.phrases = new GapList<BotPhrase>();
        this.keywordReactions = new ConcurrentHashMap<String, BotKeywordReaction>();
    }

    public void set(final ResultSet row, final ConcurrentHashMap<Integer, IBotInteractor> interactors) {
        try {
            this.id = row.getInt("id");
            this.startRoomId = row.getInt("start_room_id");
            this.botName = row.getString("nickname");
            this.avatar = row.getString("figurecode");
            this.gender = row.getString("gender").toUpperCase() == "F" ? PlayerInformation.FEMALE_GENDER : PlayerInformation.MALE_GENDER;
            this.mission = row.getString("motto");
            this.startRotation = row.getInt("start_rotation");
            this.startPosition = new Vector3(row.getInt("start_position_x"), row.getInt("start_position_y"), row.getDouble("start_position_altitude"));
            this.interactorId = row.getInt("interactor");
            this.movingEnabled = row.getInt("moving_enabled") == 1;
        } catch (final SQLException ex) {
            logger.error("SQL Exception", ex);
        }
    }

    public void addPhrase(final BotPhrase phrase) {
        this.phrases.add(phrase);
    }

    public void addKeyword(final String keyword, final BotKeywordReaction reaction) {
        this.keywordReactions.put(keyword, reaction);
    }

    private int id;
    private int startRoomId;
    private String botName;
    private String avatar;
    private int gender;
    private String mission;
    private int startRotation;
    private Vector3 startPosition;
    private int interactorId;
    private boolean movingEnabled;
    private final GapList<BotPhrase> phrases;
    private final ConcurrentHashMap<String, BotKeywordReaction> keywordReactions;
}
