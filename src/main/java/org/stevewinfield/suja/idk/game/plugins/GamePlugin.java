/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.plugins;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.bots.IBotInteractor;
import org.stevewinfield.suja.idk.game.miscellaneous.IChatCommand;

import javax.script.ScriptEngine;

public class GamePlugin {
    public ScriptEngine getScript() {
        return script;
    }

    public boolean isLoadedExternally() {
        return loadedExternally;
    }

    public GamePlugin(final String name, final ScriptEngine script, final boolean loadedExternally) {
        this.name = name;
        this.script = script;
        this.loadedExternally = loadedExternally;
    }

    public void addChatCommand(final String name, final String permission, final String obj) {
        Bootloader.getGame().getChatCommandHandler().addChatCommand(this, name, permission, obj);
    }

    public void addChatCommand(final String name, final IChatCommand chatCommand) {
        Bootloader.getGame().getChatCommandHandler().addChatCommand(this, name, chatCommand);
    }

    public void addBotInteractor(final int interactorId, final String obj) {
        Bootloader.getGame().getBotManager().addBotInteractor(this, interactorId, obj);
    }

    public void addBotInteractor(final int interactorId, final IBotInteractor botInteractor) {
        Bootloader.getGame().getBotManager().addBotInteractor(this, interactorId, botInteractor);
    }

    public String getName() {
        return name;
    }

    private final String name;
    private final ScriptEngine script;
    private final boolean loadedExternally;
}
