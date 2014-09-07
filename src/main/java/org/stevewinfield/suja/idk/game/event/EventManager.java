package org.stevewinfield.suja.idk.game.event;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.game.event.impl.EventBus;
import org.stevewinfield.suja.idk.game.plugins.GamePlugin;
import org.stevewinfield.suja.idk.game.plugins.PluginInterfaces;

import javax.script.Invocable;

public class EventManager {
    private final static Logger logger = Logger.getLogger(EventManager.class);

    public void addEventListener(final GamePlugin plugin, final Class<? extends Event> eventClass, final String obj) {
        final IEventListener executor = ((Invocable) plugin.getScript())
                .getInterface(
                        plugin.getScript().get(obj),
                        IEventListener.class
                );
        if (executor == null) {
            logger.error("Invalid event executor");
            logger.warn("You didn't set the method onEvent in " + obj);
            return;
        }
        eventBus.registerListener(eventClass, new IEventListener() {
            @Override
            public void onEvent(Event event) {
                executor.onEvent(event);
            }
        });
    }

    /**
     * Registers a listener
     * @param plugin The plugin for which this event listener is registered
     * @param eventClass Event for which the event listener must be called
     * @param eventListener Listener that may or may not have a generic type attached
     * @param eventPriority Priority of the event
     */
    public void registerListener(final GamePlugin plugin, final Class<? extends Event> eventClass, final IEventListener eventListener, final EventPriority eventPriority) {
        eventBus.registerListener(eventClass, eventListener, eventPriority);
    }

    /**
     * Registers a listener with the {@link EventPriority#DEFAULT} priority
     * @param plugin The plugin for which this event listener is registered
     * @param eventClass Event for which the event listener must be called
     * @param eventListener Listener that may or may not have a generic type attached
     */
    public void registerListener(final GamePlugin plugin, final Class<? extends Event> eventClass, final IEventListener eventListener) {
        eventBus.registerListener(eventClass, eventListener);
    }

    /**
     * Registers a listener, automatically detecting the event class
     * @param plugin The plugin for which this event listener is registered
     * @param eventListener Must have a generic type attached so it can detect the event for which this listener must be registered
     * @param eventPriority Priority of the event
     */
    public void registerListener(final GamePlugin plugin, final IEventListener eventListener, final EventPriority eventPriority) {
        eventBus.registerListener(eventListener, eventPriority);
    }

    /**
     * Registers a listener with the {@link EventPriority#DEFAULT} priority, automatically detecting the event class
     * @param plugin The plugin for which this event listener is registered
     * @param eventListener Must have a generic type attached so it can detect the event for which this listener must be registered
     */
    public void registerListener(final GamePlugin plugin, final IEventListener eventListener) {
        eventBus.registerListener(eventListener);
    }

    /**
     * Unregisters a listener, it will be unregistered from all events for which this listener is listed
     * @param plugin The plugin for which this event listener is registered
     * @param eventListener Event listener that will be unregistered
     */
    public void unregisterListener(final GamePlugin plugin, final IEventListener eventListener) {
        eventBus.unregisterListener(eventListener);
    }

    /**
     * Calls an event
     * @param event Calls an event, propagating it to all associated listeners
     * @param <T> Type of event
     * @return The supplied event
     */
    public <T extends Event> T callEvent(final T event) {
        long start = System.nanoTime();
        try {
            eventBus.post(event);
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
