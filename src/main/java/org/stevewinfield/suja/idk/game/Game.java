/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game;

import org.stevewinfield.suja.idk.game.achievements.AchievementManager;
import org.stevewinfield.suja.idk.game.bots.BotManager;
import org.stevewinfield.suja.idk.game.catalog.CatalogManager;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureManager;
import org.stevewinfield.suja.idk.game.levels.LevelManager;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatCommandHandler;
import org.stevewinfield.suja.idk.game.moderation.ModerationManager;
import org.stevewinfield.suja.idk.game.navigator.NavigatorListManager;
import org.stevewinfield.suja.idk.game.rooms.RoomManager;

public class Game {

    // getters
    public LevelManager getLevelManager() {
        return levelManager;
    }

    public AchievementManager getAchievementManager() {
        return achievementManager;
    }

    public NavigatorListManager getNavigatorListManager() {
        return navigatorListManager;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public ModerationManager getModerationManager() {
        return moderationManager;
    }

    public FurnitureManager getFurnitureManager() {
        return furniManager;
    }

    public ChatCommandHandler getChatCommandHandler() {
        return commandHandler;
    }

    public CatalogManager getCatalogManager() {
        return catalogManager;
    }

    public BotManager getBotManager() {
        return botManager;
    }

    public Game() {
        this.levelManager = new LevelManager();
        this.achievementManager = new AchievementManager();
        this.roomManager = new RoomManager();
        this.furniManager = new FurnitureManager();
        this.catalogManager = new CatalogManager();
        this.commandHandler = new ChatCommandHandler();
        this.navigatorListManager = new NavigatorListManager(roomManager.getRoomCategoryCount());
        this.moderationManager = new ModerationManager();
        this.botManager = new BotManager();
    }

    // fields
    private final LevelManager levelManager;
    private final AchievementManager achievementManager;
    private final NavigatorListManager navigatorListManager;
    private final RoomManager roomManager;
    private final FurnitureManager furniManager;
    private final ChatCommandHandler commandHandler;
    private final CatalogManager catalogManager;
    private final ModerationManager moderationManager;
    private final BotManager botManager;
}
