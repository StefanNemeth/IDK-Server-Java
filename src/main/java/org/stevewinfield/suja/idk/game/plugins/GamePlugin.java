/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.plugins;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.bots.IBotInteractor;
import org.stevewinfield.suja.idk.game.event.Event;
import org.stevewinfield.suja.idk.game.event.IEventListener;
import org.stevewinfield.suja.idk.game.event.impl.EventUtils;
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

    public void addEventListener(final String eventName, final String obj) {
        Class<? extends Event> eventClass = EventUtils.eventStringToClass(eventName);
        if (eventClass == null) {
            throw new Error("Unable to find event " + eventName);
        }
        addEventListener(eventClass, obj);
    }

    public void addEventListener(final Class<? extends Event> eventClass, final String obj) {
        Bootloader.getGame().getEventManager().addEventListener(this, eventClass, obj);
    }

    public void addEventListener(final Class<? extends Event> eventClass, final IEventListener listener) {
        Bootloader.getGame().getEventManager().registerListener(this, eventClass, listener);
    }

    public String getName() {
        return name;
    }

    private final String name;
    private final ScriptEngine script;
    private final boolean loadedExternally;
}
