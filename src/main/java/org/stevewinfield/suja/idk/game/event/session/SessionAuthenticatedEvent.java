package org.stevewinfield.suja.idk.game.event.session;

import org.stevewinfield.suja.idk.network.sessions.Session;

/**
 * This event is called when a session is authenticated.
 */
public class SessionAuthenticatedEvent extends SessionEvent {

    public SessionAuthenticatedEvent(Session session) {
        super(session);
    }
}
