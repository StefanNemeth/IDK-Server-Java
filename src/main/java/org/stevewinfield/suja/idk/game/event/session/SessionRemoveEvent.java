package org.stevewinfield.suja.idk.game.event.session;

import org.stevewinfield.suja.idk.network.sessions.Session;

/**
 * This event is called when a session is removed, i.e. a player disconnects.
 */
public class SessionRemoveEvent extends SessionEvent {
    public SessionRemoveEvent(Session session) {
        super(session);
    }
}
