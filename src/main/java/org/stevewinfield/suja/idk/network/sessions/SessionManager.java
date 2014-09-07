/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.network.sessions;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.event.session.SessionMakeEvent;
import org.stevewinfield.suja.idk.game.event.session.SessionRemoveEvent;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager {
    private static final Logger logger = Logger.getLogger(SessionManager.class);

    public SessionManager(final int maxConnections) {
        this.maxConnections = maxConnections;
        this.activeSessions = 0;
        this.sessions = new ConcurrentHashMap<>();
        this.authenticatedSessions = new ConcurrentHashMap<>();
        logger.info("Session Manager ready (Max. " + maxConnections + " sessions).");
    }

    public boolean hasSession(final Channel channel) {
        return this.sessions.containsKey(channel.getId());
    }

    public Collection<Session> getSessions() {
        return this.sessions.values();
    }

    public boolean hasAuthenticatedSession(final int playerId) {
        return this.authenticatedSessions.containsKey(playerId);
    }

    public Session getAuthenticatedSession(final int playerId) {
        return this.authenticatedSessions.containsKey(playerId) ? this.authenticatedSessions.get(playerId) : null;
    }

    public boolean removeSession(final Channel channel) {
        if (this.sessions.containsKey(channel.getId())) {
            Bootloader.getGame().getEventManager().callEvent(new SessionRemoveEvent(this.sessions.remove(channel.getId())));
            if (((Session) channel.getAttachment()).isAuthenticated()) {
                activeSessions--;
                this.authenticatedSessions.remove(((Session) channel.getAttachment()).getPlayerInstance().getInformation().getId());
            }
            return true;
        }
        return false;
    }

    public void makeAuthenticatedSession(final int playerId, final Session session) {
        if (!this.authenticatedSessions.containsKey(playerId)) {
            activeSessions++;
            this.authenticatedSessions.put(playerId, session);
        }
    }

    public boolean makeSession(final Channel channel) {
        if (this.activeSessions >= this.maxConnections) {
            logger.warn("Reached Max-Connections!");
            return false;
        }

        final Session session = new Session(channel.getId(), channel);
        channel.setAttachment(session);

        Bootloader.getGame().getEventManager().callEvent(new SessionMakeEvent(session));

        return this.sessions.putIfAbsent(channel.getId(), session) == null;
    }

    public int getActiveSessionCount() {
        return activeSessions;
    }

    private final int maxConnections;
    private int activeSessions;
    private final ConcurrentMap<Integer, Session> sessions;
    private final ConcurrentMap<Integer, Session> authenticatedSessions;
}
