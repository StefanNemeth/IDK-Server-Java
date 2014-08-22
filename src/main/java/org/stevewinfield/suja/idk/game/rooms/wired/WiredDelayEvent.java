/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.wired;

import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class WiredDelayEvent {
    public WiredAction getAction() {
        return action;
    }

    public RoomPlayer getPlayer() {
        return player;
    }

    public boolean isFinished() {
        return finished;
    }

    public int getIncrementedCycle() {
        return ++counter;
    }

    private final WiredAction action;
    private final RoomPlayer player;
    private int counter;
    private boolean finished;

    public WiredDelayEvent(final WiredAction action, final RoomPlayer player) {
        this.counter = 0;
        this.action = action;
        this.player = player;
    }

    public void finish() {
        this.finished = true;
    }
}
