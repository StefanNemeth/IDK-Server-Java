/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.communication.room.writers.RoomItemPlacementErrorWriter;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureType;
import org.stevewinfield.suja.idk.game.inventory.PlayerItem;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.coordination.Vector2;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class PlaceItemReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null) {
            return;
        }

        final String[] itemData;
        boolean breakOut = true;
        int itemId;

        if (reader.getMessageId() == OperationCodes.getIncomingOpCode("RoomItemPlaceSticky")) {
            if (!room.guestStickysAreAllowed() && !room.hasRights(session)) {
                return;
            }
            int stickieCount = 0;
            for (final RoomItem item : room.getRoomItems().values()) {
                if (item.getInteractorId() == FurnitureInteractor.POST_IT) {
                    ++stickieCount;
                }
            }
            if (stickieCount >= IDK.CATA_STICKIES_MAX_COUNT) {
                session.writeMessage(new RoomItemPlacementErrorWriter(12));
                return;
            }
            breakOut = false;
            itemId = reader.readInteger();
            final String[] termData = reader.readUTF().split(" ");
            itemData = new String[termData.length + 1];
            itemData[0] = "";
            itemData[1] = termData[0];
            itemData[2] = termData[1];
            itemData[3] = termData[2];
        } else {
            itemData = reader.readUTF().split(" ");
            itemId = Integer.valueOf(itemData[0]);
        }

        if (breakOut && !room.hasRights(session)) {
            return;
        }

        final PlayerItem item = session.getPlayerInstance().getInventory().getItem(itemId);

        if (item == null || (!item.getBase().getType().equals(FurnitureType.FLOOR) && !item.getBase().getType().equals(FurnitureType.WALL))) {
            return;
        }

        final RoomItem roomItem = new RoomItem(room, itemId, item.getBase(), item.getInteractorId(), item.getFlags());

        boolean goneRight = false;

        if (roomItem.getInteractorId() == FurnitureInteractor.ROLLER) {
            int rollerCount = 0;
            for (final RoomItem rollerItem : room.getRoomItems().values()) {
                if (rollerItem.getInteractorId() == FurnitureInteractor.ROLLER) {
                    ++rollerCount;
                }
            }
            if (rollerCount > IDK.CATA_STICKIES_MAX_COUNT) {
                session.writeMessage(new RoomItemPlacementErrorWriter(22));
                return;
            }
        }

        switch (item.getBase().getType()) {
            default:
            case FurnitureType.FLOOR: {
                final Vector2 itemPosition = new Vector2(Integer.valueOf(itemData[1]), Integer.valueOf(itemData[2]));
                final int itemRotation = Integer.valueOf(itemData[3]);

                if (!session.getPlayerInstance().getInventory().hasItem(itemId) || room.getRoomItems().containsKey(itemId)) {
                    return;
                }

                if (room.setFloorItem(session, roomItem, itemPosition, itemRotation, session.getPlayerInstance().getInventory().itemHasToUpdate(item.getItemId()))) {
                    goneRight = true;
                }
                break;
            }
            case FurnitureType.WALL: {
                final String[] correctedData = new String[itemData.length - 1];

                System.arraycopy(itemData, 1, correctedData, 0, itemData.length - 1);

                if (room.setWallItem(session, roomItem, correctedData, session.getPlayerInstance().getInventory().itemHasToUpdate(item.getItemId()))) {
                    goneRight = true;
                }

                break;
            }
        }

        if (goneRight) {
            session.getPlayerInstance().getInventory().removeItem(itemId, session);
        }

    }

}
