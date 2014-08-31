package org.stevewinfield.suja.idk.game.plugins;

import org.stevewinfield.suja.idk.game.event.Event;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatCommandArguments;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatMessage;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class PluginInterfaces {
    public static interface OnLoadedInterface {
        public void onLoaded(final RoomInstance room, final RoomPlayer bot);
    }

    public static interface OnLeftInterface {
        public void onLeft(final RoomInstance room, final RoomPlayer bot);
    }

    public static interface OnCycleInterface {
        public void onCycle(final RoomPlayer bot);
    }

    public static interface OnPlayerSaysInterface {
        public void onPlayerSays(final RoomPlayer player, final RoomPlayer bot, final ChatMessage message);
    }

    public static interface ChatCommandExecutor {
        public boolean execute(RoomPlayer player, ChatCommandArguments arguments);
    }

    public static interface EventListener {
        public void onEvent(Event event);
    }

    public static interface ScriptPlugin {
        public void initializePlugin();
    }
}
