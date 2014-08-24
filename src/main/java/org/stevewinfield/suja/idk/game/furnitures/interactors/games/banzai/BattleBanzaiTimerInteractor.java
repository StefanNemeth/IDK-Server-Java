/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors.games.banzai;

import org.stevewinfield.suja.idk.game.furnitures.interactors.DefaultInteractor;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class BattleBanzaiTimerInteractor extends DefaultInteractor {

    @Override
    public void onLoaded(final RoomInstance room, final RoomItem item) {
        super.onLoaded(room, item);
        int seconds = item.getFlagsState();

        if (seconds <= 30) {
            seconds = 30;
        } else if (seconds <= 60) {
            seconds = 60;
        } else if (seconds <= 120) {
            seconds = 120;
        } else if (seconds <= 180) {
            seconds = 180;
        } else if (seconds <= 300) {
            seconds = 300;
        } else if (seconds <= 600) {
            seconds = 600;
        }

        item.setFlags(seconds + "");
        item.update(false, true);
    }

    @Override
    public void onRemove(final RoomPlayer player, final RoomItem item) {
        super.onRemove(player, item);
        item.getRoom().getRoomTask().getBanzaiTask().onGameEnds(item);
    }

    @Override
    public void onTrigger(final RoomPlayer player, final RoomItem item, final int request, final boolean hasRights) {
        super.onTrigger(player, item, request, hasRights);

        boolean dbUpdate = false;
        int seconds = item.getFlagsState();

        if (request == 2 && !item.getRoom().getRoomTask().getBanzaiTask().isRunning()) {

            if (seconds <= 30) {
                seconds = 30;
            } else if (seconds <= 60) {
                seconds = 60;
            } else if (seconds <= 120) {
                seconds = 120;
            } else if (seconds <= 180) {
                seconds = 180;
            } else if (seconds <= 300) {
                seconds = 300;
            } else if (seconds <= 600) {
                seconds = 600;
            }

            if (item.getRoom().getRoomTask().getBanzaiTask().hasEnded()) {
                switch (seconds) {
                    case 600:
                        seconds = 30;
                        break;
                    case 300:
                        seconds = 600;
                        break;
                    case 180:
                        seconds = 300;
                        break;
                    case 120:
                        seconds = 180;
                        break;
                    case 60:
                        seconds = 120;
                        break;
                    case 30:
                        seconds = 60;
                        break;
                }

                dbUpdate = true;
            } else {
                item.getRoom().getRoomTask().getBanzaiTask().onGameEnds(item);
            }
            item.setCounter(seconds);
        } else if (request == 1) {
            if (item.getRoom().getRoomTask().getBanzaiTask().isRunning()) {
                item.getRoom().getRoomTask().getBanzaiTask().onGameRests(item);
            } else {
                if (item.getRoom().getRoomTask().getBanzaiTask().hasEnded()) {
                    item.setCounter(seconds);
                }
                item.getRoom().getRoomTask().getBanzaiTask().onGameStarts(item);
                item.requestCycles(1);
            }
        }

        item.setFlags(seconds + "");
        item.update(dbUpdate, true);
    }

    @Override
    public void onCycle(final RoomItem item) {
        super.onCycle(item);

        if (item.getRoom().getRoomTask().getBanzaiTask().hasEnded()) {
            item.setFlags(item.getCounter() + "");
            item.update(false, true);
            return;
        }

        int seconds = item.getFlagsState();

        if (!item.getRoom().getRoomTask().getBanzaiTask().isRunning()) {
            if (item.getRoom().getRoomTask().getBanzaiTask().isPaused()) {
                return;
            }
            seconds = 1;
        }

        boolean endGame = false;

        if (--seconds <= 0) {
            endGame = true;
            seconds = item.getCounter();
        } else {
            item.requestCycles(1);
        }

        item.setFlags(seconds + "");
        item.update(false, true);

        if (endGame) {
            item.getRoom().getRoomTask().getBanzaiTask().onGameEnds(item);
        }
    }

}
