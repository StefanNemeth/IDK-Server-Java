/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.room.writers.RollerEventWriter;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.coordination.TileState;
import org.stevewinfield.suja.idk.game.rooms.coordination.Vector2;
import org.stevewinfield.suja.idk.game.rooms.coordination.Vector3;

public class RollerInteractor extends DefaultInteractor {

    @Override
    public void onLoaded(final RoomInstance room, final RoomItem item) {
        super.onLoaded(room, item);
        item.requestCycles(1);
    }

    @Override
    public void onCycle(final RoomItem roller) {
        super.onCycle(roller);
        boolean reCycle = false;

        final RoomInstance room = roller.getRoom();
        final Vector2 front = roller.getFrontPosition(true);

        if (room.getGamemap().getTileState(front.getX(), front.getY()) == TileState.CLOSED) {
            return;
        }

        final Vector3 frontPosition = new Vector3(front.getX(), front.getY(), room.getAltitude(front));

        /**
         * Moving players
         */
        final GapList<RoomPlayer> players = room.getRoomPlayersForTile(roller.getPosition().getVector2());

        for (final RoomPlayer player : players) {
            if (player.isWalking()) {
                continue;
            }
            if (player.moveTo(front, true)) {
                room.writeMessage(new RollerEventWriter(player.getPosition(), frontPosition, player.getVirtualId(), roller.getItemId(), 0, false), null);
            } else {
                reCycle = true;
                break;
            }
        }

        /**
         * Moving items
         */
        try {
            if (room.roomItemsInitialized()) {
                final GapList<RoomItem> items = room.getRoomItemsForTile(roller.getPosition().getVector2());
                final int itemsCount = items.size();

                if (itemsCount > 1) {
                    final RoomItem item = items.get(itemsCount - 1);
                    int x = front.getX();
                    int y = front.getY();

                    if (roller.getRotation() == 0 || roller.getRotation() == 4) {
                        if (item.getRotation() == 2 || item.getRotation() == 6) {
                            if (item.getPosition().getY() != roller.getPosition().getY() && roller.getRotation() == 0) {
                                y = y - (item.getBase().getWidth() - 1);
                            }
                            if (item.getPosition().getX() != roller.getPosition().getX()) {
                                x = x - (item.getBase().getLength() - 1);
                            }
                        } else {
                            if (item.getPosition().getY() != roller.getPosition().getY() && roller.getRotation() == 0) {
                                y = y - (item.getBase().getLength() - 1);
                            }
                            if (item.getPosition().getX() != roller.getPosition().getX()) {
                                x = x - (item.getBase().getWidth() - 1);
                            }
                        }
                    } else if (roller.getRotation() == 2 || roller.getRotation() == 6) {
                        if (item.getRotation() == 0 || item.getRotation() == 4) {
                            if (item.getPosition().getX() != roller.getPosition().getX() && roller.getRotation() == 6) {
                                x = x - (item.getBase().getLength() - 1);
                            }
                            if (item.getPosition().getY() != roller.getPosition().getY()) {
                                y = y - (item.getBase().getWidth() - 1);
                            }
                        } else {
                            if (item.getPosition().getX() != roller.getPosition().getX() && roller.getRotation() == 6) {
                                x = x - (item.getBase().getWidth() - 1);
                            }
                            if (item.getPosition().getY() != roller.getPosition().getY()) {
                                y = y - (item.getBase().getLength() - 1);
                            }
                        }
                    }

                    /*
                     * if (item.getRotation() == 2 && (roller.getRotation() == 2
                     * || roller.getRotation() == 6)) { x = roller.getRotation()
                     * == 2 ? x + (roller.getBase().getWidth() - 1) : x -
                     * (roller.getBase().getWidth() - 1);
                     * 
                     * //x = roller.getRotation() == 2 ? x +
                     * (roller.getBase().getLength() - 1) : x -
                     * (item.getBase().getLength() - 1);
                     * //System.out.println("ITEM ROTATION: " +
                     * item.getRotation()); //System.out.println("ITEM WIDTH: "
                     * + item.getBase().getWidth());
                     * //System.out.println("ITEM LENGTH: " +
                     * item.getBase().getLength()); } else
                     * if((item.getRotation() == 2 || item.getRotation() == 6)
                     * && (roller.getRotation() == 4 || roller.getRotation() ==
                     * 0)) { System.out.println("ROLLER ROTATION: " +
                     * roller.getRotation());
                     * System.out.println("ITEM ROTATION: " +
                     * item.getRotation()); System.out.println("ITEM WIDTH: " +
                     * item.getBase().getWidth());
                     * System.out.println("ITEM LENGTH: " +
                     * item.getBase().getLength()); y = roller.getRotation() ==
                     * 4 ? y + (roller.getBase().getWidth() - 1) : y -
                     * (roller.getBase().getWidth() - 1); }
                     */

                    if (item.getItemId() != roller.getItemId() && item.getPosition().getAltitude() > roller.getPosition().getAltitude()) {
                        if (!room.setFloorItem(null, item, new Vector2(x, y), item.getRotation(), false, roller.getItemId(), roller.getAbsoluteHeight()) || items.size() > 1) {
                            reCycle = true;
                        }
                    }
                }
            } else {
                reCycle = true;
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        if (reCycle) {
            roller.requestCycles(IDK.CATA_ROLLERS_ROLL_DELAY);
        }
    }

    @Override
    public void onPlayerWalksOn(final RoomPlayer player, final RoomItem item) {
        super.onPlayerWalksOn(player, item);
        item.requestCycles(IDK.CATA_ROLLERS_ROLL_DELAY);
    }

}
