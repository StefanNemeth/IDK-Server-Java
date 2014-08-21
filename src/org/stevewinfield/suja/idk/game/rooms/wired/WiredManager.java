/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.wired;

import java.util.List;

import org.stevewinfield.suja.idk.game.furnitures.Furniture;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.wired.actions.*;
import org.stevewinfield.suja.idk.game.rooms.wired.triggers.*;

import com.google.common.primitives.Ints;

public class WiredManager {
    public static final List<Integer> WIRED_FURNIS = Ints.asList(FurnitureInteractor.WF_TRG_SAYS,
    FurnitureInteractor.WF_ACT_SHOW_MESSAGE, FurnitureInteractor.WF_ACT_TOGGLE_FURNI,
    FurnitureInteractor.WF_TRG_ENTER_ROOM, FurnitureInteractor.WF_TRG_WALKS_ON_FURNI,
    FurnitureInteractor.WF_TRG_WALKS_OFF_FURNI, FurnitureInteractor.WF_TRG_STATE_CHANGED,
    FurnitureInteractor.WF_TRG_PERIODICALLY, FurnitureInteractor.WF_TRG_GAME_ENDS,
    FurnitureInteractor.WF_TRG_GAME_STARTS);

    public static IWiredItem getInstance(final RoomItem item, final RoomInstance room, final String[] data) {
        switch (item.getInteractorId()) {
        case FurnitureInteractor.WF_TRG_SAYS:
            return new WiredTriggerUserSays(room, item, data);
        case FurnitureInteractor.WF_ACT_SHOW_MESSAGE:
            return new WiredActionShowMessage(room, item, data);
        case FurnitureInteractor.WF_ACT_TOGGLE_FURNI:
            return new WiredActionToggleFurni(room, item, data);
        case FurnitureInteractor.WF_TRG_ENTER_ROOM:
            return new WiredTriggerEnterRoom(room, item, data);
        case FurnitureInteractor.WF_TRG_WALKS_ON_FURNI:
            return new WiredTriggerUserWalksOnFurni(room, item, data);
        case FurnitureInteractor.WF_TRG_WALKS_OFF_FURNI:
            return new WiredTriggerUserWalksOffFurni(room, item, data);
        case FurnitureInteractor.WF_TRG_STATE_CHANGED:
            return new WiredTriggerStateChanged(room, item, data);
        case FurnitureInteractor.WF_TRG_PERIODICALLY:
            return new WiredTriggerPeriodically(room, item, data);
        case FurnitureInteractor.WF_TRG_GAME_ENDS:
            return new WiredTriggerGameEnds(room, item, data);
        case FurnitureInteractor.WF_TRG_GAME_STARTS:
            return new WiredTriggerGameStarts(room, item, data);
        }
        return null;
    }

    public static boolean isWiredItem(final Furniture furni) {
        return WIRED_FURNIS.contains(furni.getInteractor());
    }
}
