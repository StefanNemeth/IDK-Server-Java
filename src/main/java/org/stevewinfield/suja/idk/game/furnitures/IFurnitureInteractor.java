/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures;

import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public interface IFurnitureInteractor {
    void onLoaded(RoomInstance room, RoomItem item);

    void onPlace(RoomPlayer player, RoomItem item);

    void onRemove(RoomPlayer player, RoomItem item);

    void onTrigger(RoomPlayer player, RoomItem item, int request, boolean hasRights);

    void onCycle(RoomItem item);

    void onPlayerWalksOn(RoomPlayer player, RoomItem item);

    void onPlayerWalksOff(RoomPlayer player, RoomItem item);
}
