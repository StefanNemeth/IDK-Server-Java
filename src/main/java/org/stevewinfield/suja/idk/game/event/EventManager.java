package org.stevewinfield.suja.idk.game.event;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.game.event.impl.EventBus;
import org.stevewinfield.suja.idk.game.event.impl.EventBusLifetimeListener;
import org.stevewinfield.suja.idk.game.plugins.GamePlugin;
import org.stevewinfield.suja.idk.game.plugins.PluginInterfaces;

import javax.script.Invocable;

public class EventManager {
    private final static Logger logger = Logger.getLogger(EventManager.class);

    public void addEventListener(final GamePlugin plugin, final Class<? extends Event> eventClass, final String obj) {
        final PluginInterfaces.EventListener executor = ((Invocable) plugin.getScript())
                .getInterface(
                        plugin.getScript().get(obj),
                        PluginInterfaces.EventListener.class
                );
        if (executor == null) {
            logger.error("Invalid event executor");
            logger.warn("You didn't set the method onEvent in " + obj);
            return;
        }
        eventBus.registerLifetimeListener(new EventBusLifetimeListener() {
            @Override
            public void onEvent(Object event) {
                if (event.getClass().equals(eventClass)) {
                    executor.onEvent((Event) event);
                }
            }
        });
    }

    public void registerListener(GamePlugin plugin, Listener listener) {
        eventBus.register(listener);
    }

    public void unregisterListener(Listener listener) {
        eventBus.unregister(listener);
    }

    public <T extends Event> T callEvent(T event) {
        long start = System.nanoTime();
        try {
            eventBus.post(event);
            event.postCall();
        } catch (Throwable t) {
            logger.error("Plugin error", t);
        }

        long elapsed = start - System.nanoTime();
        if (elapsed > 250000) {
            logger.warn("Event " + event.getClass().getName() + " took " + elapsed + "ns to process!");
        }
        return event;
    }

    private final EventBus eventBus = new EventBus();
}
