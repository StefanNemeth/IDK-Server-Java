package org.stevewinfield.suja.idk.game.event.session;

import org.stevewinfield.suja.idk.game.event.Event;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class SessionAuthenticatedEvent extends Event {
    public Session getSession() {
        return session;
    }

    public SessionAuthenticatedEvent(Session session) {
        this.session = session;
    }

    private final Session session;
}
