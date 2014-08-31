package org.stevewinfield.suja.idk.game.event.impl;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.event.Event;
import org.stevewinfield.suja.idk.game.event.session.SessionAuthenticatedEvent;
import org.stevewinfield.suja.idk.game.event.session.SessionMakeEvent;
import org.stevewinfield.suja.idk.game.event.session.SessionRemoveEvent;

import java.util.HashMap;
import java.util.Map;

public class EventUtils {
    private final static Map<String, Class<? extends Event>> eventClasses = new HashMap<>();
    private final static Logger logger = Logger.getLogger(EventUtils.class);

    public static Class<? extends Event> eventStringToClass(String name) {
        if (eventClasses.containsKey(name)) {
            return eventClasses.get(name);
        }
        try {
            Class<?> clazz = Class.forName(name, true, Bootloader.getCustomClassLoader());

            if (Event.class.isAssignableFrom(clazz)) {
                return (Class<? extends Event>) clazz;
            }
            throw new Error("Class " + name + " is not assignable to an event.");
        } catch (ClassNotFoundException e) {
            throw new Error("Unable to find event class " + name);
        }
    }

    public static void addEventClass(Class<? extends Event> eventClass) {
        if (eventClasses.containsKey(eventClass.getSimpleName())) {
            logger.warn("Event " + eventClass.getSimpleName() + " already exists in EventUtils.");
        }
        eventClasses.put(eventClass.getSimpleName(), eventClass);
        eventClasses.put(eventClass.getSimpleName().substring(0, eventClass.getSimpleName().length() - 5), eventClass);
    }

    static {
        addEventClass(SessionAuthenticatedEvent.class);
        addEventClass(SessionMakeEvent.class);
        addEventClass(SessionRemoveEvent.class);
    }
}
