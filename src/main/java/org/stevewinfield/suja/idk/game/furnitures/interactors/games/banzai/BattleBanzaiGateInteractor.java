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

public class BattleBanzaiGateInteractor extends DefaultInteractor {
    protected int teamId;
    protected GameTask game;

    public BattleBanzaiGateInteractor(final int teamId) {
        this.teamId = teamId;
    }

    @Override
    public void onLoaded(final RoomInstance room, final RoomItem item) {
        super.onLoaded(room, item);

        this.game = room.getRoomTask().getBanzaiTask();
        if (this.game.getTeam(teamId) == null) {
            this.game.addGameTeam(teamId, new GameTeam(teamId, item, null));
            return;
        }
        this.game.getTeam(teamId).setGate(item);
    }

    @Override
    public void onRemove(final RoomPlayer player, final RoomItem item) {
        super.onRemove(player, item);
        this.game.getTeam(teamId).setGate(null);
    }

    @Override
    public void onPlayerWalksOn(final RoomPlayer player, final RoomItem item) {
        super.onPlayerWalksOn(player, item);
        int players = item.getFlagsState();
        final GameTeam team = this.game.getTeam(this.teamId);

        if (team == null) {
            return;
        }

        if (player.getEffectId() > 0 && player.getEffectId() != this.teamId) {
            final GameTeam oldTeam = this.game.getTeam(player.getEffectId());

            if (oldTeam != null && oldTeam.getGate() != null) {
                int oldTeamPlayers = oldTeam.getGate().getFlagsState();

                oldTeam.removePlayer(player);
                oldTeam.getGate().setFlags(--oldTeamPlayers + "");
                oldTeam.getGate().update(false, true);
            }
        }

        if (player.getEffectId() == this.teamId) {
            player.applyEffect(0);
            team.removePlayer(player);
            players--;
        } else if (players < 5) {
            player.applyEffect(this.teamId);
            team.addPlayer(player);
            players++;
        }

        item.setFlags(players + "");
        item.update(false, true);
    }

}
