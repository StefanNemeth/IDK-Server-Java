/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.moderation;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class ModerationManager {
    private static final Logger logger = Logger.getLogger(ModerationManager.class);

    public ConcurrentHashMap<Integer, ModerationPresetMessage> getPresetMessages() {
        return presetMessages;
    }

    public ConcurrentHashMap<Integer, ModerationPresetAction> getPresetActions() {
        return presetActions;
    }

    public ModerationManager() {
        this.presetMessages = new ConcurrentHashMap<>();
        this.presetActions = new ConcurrentHashMap<>();
        try {
            int counter = 0;
            ResultSet row = Bootloader.getStorage().queryParams("SELECT * FROM moderation_presets").executeQuery();
            while (row.next()) {
                final ModerationPresetMessage message = new ModerationPresetMessage();
                message.set(row);
                this.presetMessages.put(message.getId(), message);
                counter++;
            }
            logger.info(counter + " Moderation Preset Message(s) loaded.");
            counter = 0;
            final GapList<ModerationPresetAction> subActions = new GapList<>();
            row = Bootloader.getStorage().queryParams("SELECT * FROM moderation_preset_actions").executeQuery();
            while (row.next()) {
                final ModerationPresetAction action = new ModerationPresetAction();
                action.set(row);
                if (action.getParentId() < 0) {
                    this.presetActions.put(action.getId(), action);
                    counter++;
                } else {
                    subActions.add(action);
                }
            }
            logger.info(counter + " Moderation Preset Action Categorie(s) loaded.");
            counter = 0;
            for (final ModerationPresetAction subAction : subActions) {
                if (this.presetActions.containsKey(subAction.getId())) {
                    this.presetActions.get(subAction.getId()).addSubItem(subAction);
                    counter++;
                }
            }
            logger.info(counter + " Moderation Preset Action(s) loaded.");
        } catch (final SQLException ex) {
            logger.error("SQL Exception", ex);
        }
    }

    public void logAction(final int moderatorId, final String logText) {
        try {
            final PreparedStatement statement = Bootloader.getStorage()
                    .queryParams("INSERT INTO moderation_logs (moderator_id, log_text, timestamp) " +
                                    "VALUES (" + moderatorId + ", ?, " + Bootloader.getTimestamp() + ")"
                    );
            statement.setString(1, logText);
            statement.execute();
        } catch (final SQLException ex) {
            logger.error("SQL Exception", ex);
        }

    }

    private final ConcurrentHashMap<Integer, ModerationPresetMessage> presetMessages;
    private final ConcurrentHashMap<Integer, ModerationPresetAction> presetActions;
}
