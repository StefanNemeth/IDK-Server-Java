/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeleporterInteractor extends DefaultInteractor {
    private static final Logger logger = Logger.getLogger(TeleporterInteractor.class);

    @Override
    public void onLoaded(final RoomInstance room, final RoomItem item) {
        super.onLoaded(room, item);
        item.setFlags(0);
        Bootloader.getGame().getRoomManager().getTeleporterCache().put(item.getItemId(), room.getInformation().getId());
    }

    @Override
    public void onRemove(final RoomPlayer player, final RoomItem item) {
        super.onRemove(player, item);
        item.setFlags(0);
        Bootloader.getGame().getRoomManager().getTeleporterCache().put(item.getItemId(), -1);
    }

    @Override
    public void onTrigger(final RoomPlayer player, final RoomItem item, final int request, final boolean hasRights) {
        super.onTrigger(player, item, request, hasRights);

        if (player != null && !item.isTouching(player.getPosition(), player.getRotation(), item.getPosition().getAltitude())) {
            player.moveTo(item.getFrontPosition(), item.getFrontRotation(), item);
            return;
        }

        if (item.getFlagsState() == 0 && item.getInteractingPlayers().size() == 0) {
            // TODO: Can player be null?
            item.getInteractingPlayers().put(1, player.getVirtualId());
            player.setWalkingBlocked(true);
            player.moveTo(item.getPosition().getVector2(), false, true);
            item.setFlags(1);
            item.update(false, player.getSession());
            item.requestCycles(2);
        }
    }

    @Override
    public void onCycle(final RoomItem item) {
        RoomPlayer outgoingPlayer = null;
        RoomPlayer incomingPlayer = null;

        if (item.getInteractingPlayers().containsKey(1)) {
            outgoingPlayer = item.getRoom().getRoomPlayers().containsKey(
                    item.getInteractingPlayers().get(1)) ? item.getRoom().getRoomPlayers().get(item.getInteractingPlayers().get(1)) : null;
            if (outgoingPlayer == null) {
                item.getInteractingPlayers().get(1);
            }
        }

        if (item.getInteractingPlayers().containsKey(2)) {
            incomingPlayer = item.getRoom().getRoomPlayers().containsKey(
                    item.getInteractingPlayers().get(2)) ? item.getRoom().getRoomPlayers().get(item.getInteractingPlayers().get(2)) : null;
            if (incomingPlayer == null) {
                item.getInteractingPlayers().get(2);
            }
        }

        int flagState = 0;

        if (outgoingPlayer != null) {
            if (item.getFlagsState() == 2) {
                if (outgoingPlayer.getSession().isTeleporting()) {
                    outgoingPlayer.getSession().prepareRoom(Bootloader.getGame().getRoomManager().loadRoomInstance(outgoingPlayer.getSession().getTargetTeleporterRoom()), "", true);
                    outgoingPlayer.getSession().setTargetTeleporterRoom(0);
                }
                item.getInteractingPlayers().remove(1);
            } else {
                RoomItem targetItem = null;
                final int itemId = Integer.valueOf(item.getTermFlags()[0]);
                boolean showUpdate = false;

                if (item.getRoom().getRoomItems().containsKey(itemId)) {
                    targetItem = item.getRoom().getRoomItems().get(itemId);
                    showUpdate = true;
                } else {
                    int roomId = 0;
                    if (Bootloader.getGame().getRoomManager().getTeleporterCache().containsKey(itemId)) {
                        roomId = Bootloader.getGame().getRoomManager().getTeleporterCache().get(itemId);
                    } else if (itemId > 0) {
                        try {
                            final ResultSet row = Bootloader.getStorage().queryParams("SELECT room_id FROM room_items WHERE item_id=" + itemId).executeQuery();
                            if (row.next()) {
                                roomId = row.getInt("room_id");
                            }
                        } catch (final SQLException e) {
                            logger.error("SQL Exception", e);
                        }
                    }
                    if (roomId > 0) {
                        outgoingPlayer.getSession().setTargetTeleporterId(itemId);
                        outgoingPlayer.getSession().setTargetTeleporterRoom(roomId);
                        showUpdate = true;
                    }
                }

                if (outgoingPlayer.getPosition().getX() != item.getPosition().getX() || outgoingPlayer.getPosition().getY() != item.getPosition().getY()) {
                    outgoingPlayer.setWalkingBlocked(false);
                    item.getInteractingPlayers().remove(1);
                } else if (showUpdate) {
                    flagState = 2;
                    if (targetItem != null) {
                        outgoingPlayer.setPosition(targetItem.getPosition());
                        outgoingPlayer.setRotation(targetItem.getRotation());
                        outgoingPlayer.update();
                        if (targetItem.getFlagsState() != 2) {
                            targetItem.setFlags(2);
                            targetItem.update(false);
                            targetItem.requestCycles(1);
                        }
                        targetItem.getInteractingPlayers().put(2, outgoingPlayer.getVirtualId());
                        item.getInteractingPlayers().remove(1);
                    }
                    item.requestCycles(1);
                } else {
                    flagState = 1;
                    outgoingPlayer.moveTo(item.getFrontPosition(true), false, true);
                    outgoingPlayer.setWalkingBlocked(false);
                    item.getInteractingPlayers().remove(1);
                    item.requestCycles(1);
                }
            }

        }

        if (incomingPlayer != null) {
            if (incomingPlayer.getPosition().getX() != item.getPosition().getX() || incomingPlayer.getPosition().getY() != item.getPosition().getY()) {
                incomingPlayer.setWalkingBlocked(false);
                item.getInteractingPlayers().remove(2);
            } else {
                flagState = 1;
                incomingPlayer.moveTo(item.getFrontPosition(), false, true);
                incomingPlayer.setWalkingBlocked(false);
                item.getInteractingPlayers().remove(2);
                item.requestCycles(1);
            }
        }

        if (item.getFlagsState() != flagState) {
            item.setFlags(flagState);
            item.update(false);
        }

    }

}
