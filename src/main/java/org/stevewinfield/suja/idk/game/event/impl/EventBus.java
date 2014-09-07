package org.stevewinfield.suja.idk.game.event.impl;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.game.event.Event;
import org.stevewinfield.suja.idk.game.event.EventPriority;
import org.stevewinfield.suja.idk.game.event.IEventListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EventBus {
    private final static Logger logger = Logger.getLogger(EventBus.class);

    /**
     * Registers a listener
     * @param eventClass Event for which the event listener must be called
     * @param eventListener Listener that may or may not have a generic type attached
     * @param eventPriority Priority of the event
     */
    public void registerListener(final Class<? extends Event> eventClass, final IEventListener eventListener, final EventPriority eventPriority) {
        lock.writeLock().lock();
        try {
            Map<EventPriority, List<IEventListener>> eventPriorityMap = eventListenersByPriority.get(eventClass);
            if (eventPriorityMap == null) {
                eventPriorityMap = new HashMap<>();
                eventListenersByPriority.put(eventClass, eventPriorityMap);
            }
            List<IEventListener> eventListenerList = eventPriorityMap.get(eventPriority);
            if (eventListenerList == null) {
                eventListenerList = new ArrayList<>();
                eventPriorityMap.put(eventPriority, eventListenerList);
            }
            eventListenerList.add(eventListener);
            bakeEventListeners(eventClass);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Registers a listener with the {@link EventPriority#DEFAULT} priority
     * @param eventClass Event for which the event listener must be called
     * @param eventListener Listener that may or may not have a generic type attached
     */
    public void registerListener(final Class<? extends Event> eventClass, final IEventListener eventListener) {
        registerListener(eventClass, eventListener, EventPriority.NORMAL);
    }

    /**
     * Registers a listener, automatically detecting the event class
     * @param eventListener Must have a generic type attached so it can detect the event for which this listener must be registered
     * @param eventPriority Priority of the event
     */
    public void registerListener(final IEventListener eventListener, final EventPriority eventPriority) {
        Type[] interfaces = eventListener.getClass().getGenericInterfaces();
        boolean found = false;
        for (Type type : interfaces) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type rawType = parameterizedType.getRawType();
                if (rawType.equals(IEventListener.class)) {
                    ParameterizedType pt = (ParameterizedType) type;
                    Type[] types = pt.getActualTypeArguments();
                    Class<? extends Event> eventClass = (Class) types[0];
                    registerListener(eventClass, eventListener, eventPriority);
                    found = true;
                }
            }
        }
        if (!found) {
            throw new UnsupportedOperationException("Unable to find generic interface of type IEventListener on class " + eventListener.getClass().getName());
        }
    }

    /**
     * Registers a listener with the {@link EventPriority#DEFAULT} priority, automatically detecting the event class
     * @param eventListener Must have a generic type attached so it can detect the event for which this listener must be registered
     */
    public void registerListener(final IEventListener eventListener) {
        registerListener(eventListener, EventPriority.NORMAL);
    }

    /**
     * Unregisters a listener, it will be unregistered from all events for which this listener is listed
     * @param eventListener Event listener that will be unregistered
     */
    public void unregisterListener(final IEventListener eventListener) {
        lock.writeLock().lock();
        try {
            for (Map.Entry<Class<? extends Event>, Map<EventPriority, List<IEventListener>>> eventPriorityListMap : eventListenersByPriority.entrySet()) {
                boolean found = false;
                for (List<IEventListener> eventListenerList : eventPriorityListMap.getValue().values()) {
                    while (eventListenerList.contains(eventListener)) {
                        eventListenerList.remove(eventListener);
                        found = true;
                    }
                }
                if (found) {
                    bakeEventListeners(eventPriorityListMap.getKey());
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Posts an event to all listeners
     * @param event Event that will be posted
     */
    public void post(final Event event) {
        lock.readLock().lock();
        try {
            IEventListener[] eventListenerList = eventListeners.get(event.getClass());
            if (eventListenerList != null) {
                for (IEventListener eventListener : eventListenerList) {
                    try {
                        eventListener.onEvent(event);
                    } catch (Throwable t) {
                        logger.warn("Error dispatching event " + event + " to listener " + eventListener.getClass().getName(), t);
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private void bakeEventListeners(final Class<? extends Event> eventClass) {
        Map<EventPriority, List<IEventListener>> eventPriorityMap = eventListenersByPriority.get(eventClass);
        if (eventPriorityMap != null) {
            List<IEventListener> eventListenerList = new ArrayList<>();
            for (EventPriority eventPriority : EventPriority.values()) {
                List<IEventListener> eventListenersByPriorityMap = eventPriorityMap.get(eventPriority);
                if (eventListenersByPriorityMap != null) {
                    for (IEventListener eventListener : eventListenersByPriorityMap) {
                        eventListenerList.add(eventListener);
                    }
                }
            }
            eventListeners.put(eventClass, eventListenerList.toArray(new IEventListener[eventListenerList.size()]));
        } else {
            eventListeners.put(eventClass, null);
        }
    }

    private final Map<Class<? extends Event>, IEventListener[]> eventListeners = new HashMap<>();
    private final Map<Class<? extends Event>, Map<EventPriority, List<IEventListener>>> eventListenersByPriority = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
}
