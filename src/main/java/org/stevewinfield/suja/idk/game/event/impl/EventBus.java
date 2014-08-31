/**
 * Copyright (c) 2012, md_5. All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 The name of the author may not be used to endorse or promote products derived
 from this software without specific prior written permission.

 You may not use the software for commercial software hosting services without
 written permission from the author.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.

 Modified by koesie10
 */
package org.stevewinfield.suja.idk.game.event.impl;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.game.event.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class EventBus {
    private final static Logger logger = Logger.getLogger(EventBus.class);

    public void post(Object event) {
        lock.readLock().lock();
        try {
            for (EventBusLifetimeListener listener : eventBusLifetimeListeners) {
                try {
                    listener.onEvent(event);
                } catch (Throwable t) {
                    logger.warn("Error dispatching event " +
                                    event.getClass().getName() + " to lifetime listener " +
                                    listener.getClass().getName(), t
                    );
                }
            }
            EventHandlerMethod[] handlers = byEventBaked.get(event.getClass());
            if (handlers != null) {
                for (EventHandlerMethod method : handlers) {
                    try {
                        method.invoke(event);
                    } catch (IllegalAccessException ex) {
                        throw new Error("Method became inaccessible: " + event, ex);
                    } catch (IllegalArgumentException ex) {
                        throw new Error("Method rejected target/argument: " + event, ex);
                    } catch (InvocationTargetException ex) {
                        logger.warn("Error dispatching event " + event + " to listener " + method.getListener(), ex.getCause());
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private Map<Class<?>, Map<Byte, Set<Method>>> findHandlers(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = new HashMap<>();
        for (Method m : listener.getClass().getDeclaredMethods()) {
            EventHandler annotation = m.getAnnotation(EventHandler.class);
            if (annotation != null) {
                Class<?>[] params = m.getParameterTypes();
                if (params.length != 1) {
                    logger.info("Method " + m + " in class " + listener.getClass() + " annotated with " + annotation + " does not have single argument");
                    continue;
                }
                Map<Byte, Set<Method>> prioritiesMap = handler.get(params[0]);
                if (prioritiesMap == null) {
                    prioritiesMap = new HashMap<>();
                    handler.put(params[0], prioritiesMap);
                }
                Set<Method> priority = prioritiesMap.get(annotation.priority());
                if (priority == null) {
                    priority = new HashSet<>();
                    prioritiesMap.put(annotation.priority(), priority);
                }
                priority.add(m);
            }
        }
        return handler;
    }

    public void register(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = findHandlers(listener);
        lock.writeLock().lock();
        try {
            for (Map.Entry<Class<?>, Map<Byte, Set<Method>>> e : handler.entrySet()) {
                Map<Byte, Map<Object, Method[]>> prioritiesMap = byListenerAndPriority.get(e.getKey());
                if (prioritiesMap == null) {
                    prioritiesMap = new HashMap<>();
                    byListenerAndPriority.put(e.getKey(), prioritiesMap);
                }
                for (Map.Entry<Byte, Set<Method>> entry : e.getValue().entrySet()) {
                    Map<Object, Method[]> currentPriorityMap = prioritiesMap.get(entry.getKey());
                    if (currentPriorityMap == null) {
                        currentPriorityMap = new HashMap<>();
                        prioritiesMap.put(entry.getKey(), currentPriorityMap);
                    }
                    Method[] baked = new Method[entry.getValue().size()];
                    currentPriorityMap.put(listener, entry.getValue().toArray(baked));
                }
                bakeHandlers(e.getKey());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void registerLifetimeListener(EventBusLifetimeListener listener) {
        lock.writeLock().lock();
        try {
            eventBusLifetimeListeners.add(listener);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void unregister(Object listener) {
        Map<Class<?>, Map<Byte, Set<Method>>> handler = findHandlers(listener);
        lock.writeLock().lock();
        try {
            for (Map.Entry<Class<?>, Map<Byte, Set<Method>>> e : handler.entrySet()) {
                Map<Byte, Map<Object, Method[]>> prioritiesMap = byListenerAndPriority.get(e.getKey());
                if (prioritiesMap != null) {
                    for (Byte priority : e.getValue().keySet()) {
                        Map<Object, Method[]> currentPriority = prioritiesMap.get(priority);
                        if (currentPriority != null) {
                            currentPriority.remove(listener);
                            if (currentPriority.isEmpty()) {
                                prioritiesMap.remove(priority);
                            }
                        }
                    }
                    if (prioritiesMap.isEmpty()) {
                        byListenerAndPriority.remove(e.getKey());
                    }
                }
                bakeHandlers(e.getKey());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void bakeHandlers(Class<?> eventClass) {
        Map<Byte, Map<Object, Method[]>> handlersByPriority = byListenerAndPriority.get(eventClass);
        if (handlersByPriority != null) {
            List<EventHandlerMethod> handlersList = new ArrayList<>(handlersByPriority.size() * 2);

            // Either I'm really tired, or the only way we can iterate between Byte.MIN_VALUE and Byte.MAX_VALUE inclusively,
            // with only a byte on the stack is by using a do {} while() format loop.
            byte value = Byte.MIN_VALUE;
            do {
                Map<Object, Method[]> handlersByListener = handlersByPriority.get(value);
                if (handlersByListener != null) {
                    for (Map.Entry<Object, Method[]> listenerHandlers : handlersByListener.entrySet()) {
                        for (Method method : listenerHandlers.getValue()) {
                            EventHandlerMethod ehm = new EventHandlerMethod(listenerHandlers.getKey(), method);
                            handlersList.add(ehm);
                        }
                    }
                }
            } while (value++ < Byte.MAX_VALUE);
            byEventBaked.put(eventClass, handlersList.toArray(new EventHandlerMethod[handlersList.size()]));
        } else {
            byEventBaked.put(eventClass, null);
        }
    }

    private final Map<Class<?>, Map<Byte, Map<Object, Method[]>>> byListenerAndPriority = new HashMap<>();
    private final Map<Class<?>, EventHandlerMethod[]> byEventBaked = new HashMap<>();
    private final List<EventBusLifetimeListener> eventBusLifetimeListeners = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
}