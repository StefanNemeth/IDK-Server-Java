/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.trading;

import java.util.concurrent.ConcurrentHashMap;

public class TradeManager {
    public Trade getTrade(final int playerId) {
        return this.tradeSessions.containsKey(playerId) ? this.tradeSessions.get(playerId) : null;
    }

    public TradeManager() {
        this.tradeSessions = new ConcurrentHashMap<>();
    }

    public boolean initiateTrade(final int a, final int b) {
        if (this.tradeSessions.containsKey(a) || this.tradeSessions.containsKey(b)) {
            return false;
        }

        final Trade trade = new Trade(a, b);

        this.tradeSessions.put(a, trade);
        this.tradeSessions.put(b, trade);
        return true;
    }

    public void stopTrade(final int playerId) {
        this.tradeSessions.remove(playerId);
    }

    private final ConcurrentHashMap<Integer, Trade> tradeSessions;
}
