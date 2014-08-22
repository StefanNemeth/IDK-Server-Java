/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.bots;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.bots.interactors.*;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatMessage;
import org.stevewinfield.suja.idk.game.plugins.GamePlugin;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class BotManager {
    private static Logger logger = Logger.getLogger(BotManager.class);

    public ConcurrentHashMap<Integer, BotInstance> getBots() {
        return bots;
    }

    public ConcurrentHashMap<Integer, IBotInteractor> getInteractors() {
        return botInteractors;
    }

    public BotManager() {
        this.bots = new ConcurrentHashMap<Integer, BotInstance>();
        this.botInteractors = new ConcurrentHashMap<Integer, IBotInteractor>();
        this.botInteractors.put(BotInteractor.DEFAULT, new DefaultBotInteractor());
    }

    public void load() {
        try {
            ResultSet row = Bootloader.getStorage().queryParams("SELECT * FROM room_bots").executeQuery();
            while (row.next()) {
                final BotInstance bot = new BotInstance();
                bot.set(row, this.botInteractors);
                this.bots.put(bot.getId(), bot);
            }
            row = Bootloader.getStorage().queryParams("SELECT * FROM room_bot_phrases").executeQuery();
            logger.info(this.bots.size() + " Bot(s) loaded.");
            int phrases = 0;
            while (row.next()) {
                final BotPhrase phrase = new BotPhrase();
                phrase.set(row);
                if (this.bots.containsKey(phrase.getBotId())) {
                    ++phrases;
                    this.bots.get(phrase.getBotId()).addPhrase(phrase);
                }
            }
            logger.info(phrases + " Bot phrase(s) loaded.");
            row = Bootloader.getStorage().queryParams("SELECT * FROM room_bot_keywords").executeQuery();
            int keywords = 0;
            while (row.next()) {
                final BotKeywordReaction reaction = new BotKeywordReaction();
                reaction.set(row);
                if (!this.bots.containsKey(reaction.getBotId())) {
                    continue;
                }
                final BotInstance bot = this.bots.get(reaction.getBotId());
                for (final String keyword : row.getString("keywords").split(";")) {
                    ++keywords;
                    bot.addKeyword(keyword.toLowerCase(), reaction);
                }
            }
            logger.info(keywords + " Bot keyword(s) loaded.");
        } catch (final SQLException ex) {
            logger.error("SQL Exception", ex);
        }
    }

    public List<BotInstance> getBotsByRoomId(final int roomId) {
        final GapList<BotInstance> bots = new GapList<BotInstance>();
        for (final BotInstance bot : this.bots.values()) {
            if (bot.getStartRoomId() == roomId) {
                bots.add(bot);
            }
        }
        return bots;
    }

    public void addBotInteractor(final GamePlugin plugin, final int interactorId, final String obj) {
        botInteractors.put(interactorId, new IBotInteractor() {

            @Override
            public void onLoaded(final RoomInstance room, final RoomPlayer bot) {
                try {
                    ((Invocable) plugin.getScript()).invokeMethod(plugin.getScript().get(obj), "onLoaded", room, bot);
                } catch (final NoSuchMethodException e) {
                } catch (final ScriptException e) {
                    logger.error("Plugin Error", e);
                }
            }

            @Override
            public void onLeft(final RoomInstance room, final RoomPlayer bot) {
                try {
                    ((Invocable) plugin.getScript()).invokeMethod(plugin.getScript().get(obj), "onLeft", room, bot);
                } catch (final NoSuchMethodException e) {
                } catch (final ScriptException e) {
                    logger.error("Plugin Error", e);
                }
            }

            @Override
            public void onCycle(final RoomPlayer bot) {
                try {
                    ((Invocable) plugin.getScript()).invokeMethod(plugin.getScript().get(obj), "onCycle", bot);
                } catch (final NoSuchMethodException e) {
                } catch (final ScriptException e) {
                    logger.error("Plugin Error", e);
                }
            }

            @Override
            public void onPlayerSays(final RoomPlayer player, final RoomPlayer bot, final ChatMessage message) {
                try {
                    ((Invocable) plugin.getScript()).invokeMethod(plugin.getScript().get(obj), "onPlayerSays", player,
                    bot, message);
                } catch (final NoSuchMethodException e) {
                } catch (final ScriptException e) {
                    logger.error("Plugin Error", e);
                }
            }

        });
    }

    private final ConcurrentHashMap<Integer, BotInstance> bots;
    private final ConcurrentHashMap<Integer, IBotInteractor> botInteractors;
}
