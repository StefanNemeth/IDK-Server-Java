package org.stevewinfield.suja.idk.game.event.session;

import org.stevewinfield.suja.idk.network.sessions.Session;

/**
 * This event is fired whenever a session is created, there will <b>not</b> be any player information available yet!
 */
public class SessionMakeEvent extends SessionEvent {
    public SessionMakeEvent(Session session) {
        super(session);
    }
}
