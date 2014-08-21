/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.tasks;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.game.rooms.GameTeam;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public abstract class GameTask {
    protected RoomInstance room;
    protected boolean running;
    protected boolean paused;
    protected boolean ended;
    protected int flexInteger;
    protected List<RoomItem> gameItems;
    protected ConcurrentHashMap<Integer, GameTeam> gameTeams;

    public GameTask(final RoomInstance room) {
        this.room = room;
        this.ended = true;
        this.gameItems = new GapList<RoomItem>();
        this.gameTeams = new ConcurrentHashMap<Integer, GameTeam>();
    }

    public int getFlexInteger() {
        return flexInteger;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public GameTeam getTeam(final int id) {
        return this.gameTeams.containsKey(id) ? this.gameTeams.get(id) : null;
    }

    public void addGameItem(final RoomItem item) {
        this.gameItems.add(item);
    }

    public void removeGameItem(final RoomItem item) {
        this.gameItems.remove(item);
    }

    public void addGameTeam(final int teamId, final GameTeam team) {
        this.gameTeams.put(teamId, team);
    }

    public boolean hasEnded() {
        return ended;
    }

    public void onGameEnds(final RoomItem timer) {
        this.room.getWiredHandler().onGameEnds();
    }

    public void onGameStarts(final RoomItem timer) {
        this.room.getWiredHandler().onGameStarts();
    }

    public abstract void onGameRests(RoomItem timer);

    public abstract void onHandle(RoomPlayer player, RoomItem item);
}
