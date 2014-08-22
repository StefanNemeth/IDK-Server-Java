/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors.games.banzai;

import org.stevewinfield.suja.idk.game.furnitures.interactors.DefaultInteractor;
import org.stevewinfield.suja.idk.game.rooms.GameTeam;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.tasks.GameTask;

public class BattleBanzaiScoreboardInteractor extends DefaultInteractor {
    protected int teamId;
    protected GameTask game;

    public BattleBanzaiScoreboardInteractor(final int teamId) {
        this.teamId = teamId;
    }

    @Override
    public void onLoaded(final RoomInstance room, final RoomItem item) {
        super.onLoaded(room, item);

        this.game = room.getRoomTask().getBanzaiTask();
        if (this.game.getTeam(teamId) == null) {
            this.game.addGameTeam(teamId, new GameTeam(teamId, null, item));
            return;
        }
        this.game.getTeam(teamId).setScoreBoard(item);
        item.setFlags(this.game.getTeam(teamId).getPoints() + "");
        item.update(false, true);
    }

    @Override
    public void onRemove(final RoomPlayer player, final RoomItem item) {
        super.onRemove(player, item);

        this.game.getTeam(teamId).setScoreBoard(null);
    }

}
