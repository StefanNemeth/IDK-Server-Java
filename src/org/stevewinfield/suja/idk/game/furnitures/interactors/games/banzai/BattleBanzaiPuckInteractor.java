/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors.games.banzai;

import java.util.List;

import org.stevewinfield.suja.idk.game.furnitures.interactors.DefaultInteractor;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.coordination.Rotation;
import org.stevewinfield.suja.idk.game.rooms.coordination.TileState;
import org.stevewinfield.suja.idk.game.rooms.coordination.Vector2;

public class BattleBanzaiPuckInteractor extends DefaultInteractor {

    @Override
    public void onTrigger(final RoomPlayer player, final RoomItem item, final int request, final boolean hasRights) {
        super.onTrigger(player, item, request, hasRights);

        if (!hasRights && item.getBase().hasRightCheck())
            return;

        if (!item.isTouching(player.getPosition())) {
            final Vector2 goal = item.getFrontPosition(player.getPosition().getVector2());
            player.moveTo(goal,
            Rotation.calculate(player.getPosition().getX(), player.getPosition().getY(), goal.getX(), goal.getY()),
            item);
            return;
        }

        if (true) {
            // Buggy.
            return;
        }

        final int rotation = Rotation.calculate(player.getPosition().getX(), player.getPosition().getY(), item.getPosition()
        .getX(), item.getPosition().getY());
        int newX = 0;
        int newY = 0;

        if (rotation == 0) {
            newY = -1;
        } else if (rotation == 1) {
            newX = 1;
            newY = -1;
        } else if (rotation == 2) {
            newX = 1;
        } else if (rotation == 3) {
            newX = 1;
            newY = 1;
        } else if (rotation == 4) {
            newY = +1;
        } else if (rotation == 5) {
            newX = -1;
            newY = 1;
        } else if (rotation == 6) {
            newX = -1;
        } else if (rotation == 7) {
            newX = -1;
            newY = -1;
        }

        item.setNextInfoData(new Vector2(newX, newY));
        item.setActingPlayer(player);
        item.requestCycles(1);

        this.processMove(player, item, 1);
        this.processMove(player, item, 2);
    }

    public void processMove(final RoomPlayer player, final RoomItem item, final int fields) {
        int newX = item.getNextInfoData().getX();
        int newY = item.getNextInfoData().getY();
        int x = -1;
        int y = -1;
        int rem = 0;

        for (int i = 1; i <= fields; i++) {
            final int checkX = item.getPosition().getX() + (newX * i);
            final int checkY = item.getPosition().getY() + (newY * i);

            boolean end = item.getRoom().getGamemap().getTileState(checkX, checkY) != TileState.OPEN;

            if (!end) {
                final List<RoomPlayer> players = item.getRoom().getRoomPlayersForTile(new Vector2(checkX, checkY));
                for (final RoomPlayer vPlayer : players) {
                    if (vPlayer.getVirtualId() != player.getVirtualId()) {
                        end = true;
                        break;
                    }
                }
            }

            if (end) {
                if (x != -1 && y != -1) {
                    item.getRoom().setFloorItem(null, item, new Vector2(x, y), item.getRotation(), true);
                }
                rem = fields - i;
                break;
            }

            x = checkX;
            y = checkY;
            rem = i;
        }

        if (x == -1 && y == -1) {
            newX *= -1;
            newY *= -1;

            for (int i = 1; i <= rem; i++) {
                final int checkX = item.getPosition().getX() + (newX * i);
                final int checkY = item.getPosition().getY() + (newY * i);
                boolean end = false;

                if (item.getRoom().getGamemap().getTileState(checkX, checkY) != TileState.OPEN) {
                    end = true;
                    break;
                } else {

                    final List<RoomPlayer> players = item.getRoom().getRoomPlayersForTile(new Vector2(checkX, checkY));
                    for (final RoomPlayer vPlayer : players) {
                        if (vPlayer.getVirtualId() != player.getVirtualId()) {
                            end = true;
                            break;
                        }
                    }
                }

                if (end) {
                    break;
                } else {
                    x = checkX;
                    y = checkY;
                }
            }
        }

        if (x != -1 && y != -1) {
            item.setNextInfoData(new Vector2(newX, newY));
            item.getRoom().setFloorItem(null, item, new Vector2(x, y), item.getRotation(), true);
        }
    }

    @Override
    public void onCycle(final RoomItem item) {
        this.processMove(item.getActingPlayer(), item, 1);
    }

    @Override
    public void onPlayerWalksOn(final RoomPlayer player, final RoomItem item) {
        super.onPlayerWalksOn(player, item);

        if (true) {
            // Buggy.
            return;
        }

        final int rotation = Rotation.calculate(player.getPosition().getX(), player.getPosition().getY(), item.getPosition()
        .getX(), item.getPosition().getY());
        int newX = 0;
        int newY = 0;

        if (rotation == 0) {
            newY = -1;
        } else if (rotation == 1) {
            newX = 1;
            newY = -1;
        } else if (rotation == 2) {
            newX = 1;
        } else if (rotation == 3) {
            newX = 1;
            newY = 1;
        } else if (rotation == 4) {
            newY = +1;
        } else if (rotation == 5) {
            newX = -1;
            newY = 1;
        } else if (rotation == 6) {
            newX = -1;
        } else if (rotation == 7) {
            newX = -1;
            newY = -1;
        }

        item.setNextInfoData(new Vector2(newX, newY));
        item.setActingPlayer(player);

        this.processMove(player, item, 1);
    }

}
