/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.plugins;

import org.stevewinfield.suja.idk.Bootloader;

import javax.script.ScriptEngine;

public class GamePlugin {
    public ScriptEngine getScript() {
        return script;
    }

    public GamePlugin(final String name, final ScriptEngine script) {
        this.name = name;
        this.script = script;
    }

    public void addEventListener(final String type, final Object obj) {

    }

    public void addChatCommand(final String name, final String permission, final String obj) {
        Bootloader.getGame().getChatCommandHandler().addChatCommand(this, name, permission, obj);
    }

    public void addBotInteractor(final int interactorId, final String obj) {
        Bootloader.getGame().getBotManager().addBotInteractor(this, interactorId, obj);
    }

    public String getName() {
        return name;
    }

    private final String name;
    private final ScriptEngine script;
}
