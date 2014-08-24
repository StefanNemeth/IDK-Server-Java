/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.wired.actions;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.wired.WiredAction;
import org.stevewinfield.suja.idk.game.rooms.wired.WiredDelayEvent;
import org.stevewinfield.suja.idk.game.rooms.wired.WiredManager;

import java.util.List;

public class WiredActionToggleFurni extends WiredAction {
    private static final Logger logger = Logger.getLogger(WiredActionToggleFurni.class);

    public WiredActionToggleFurni(final RoomInstance room, final RoomItem item, final String[] data) {
        this.room = room;
        this.item = item;
        this.set(data);
    }

    @Override
    public int getWiredType() {
        return 2;
    }

    @Override
    public RoomItem getItem() {
        return item;
    }

    @Override
    public List<Integer> getItems() {
        return this.items;
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public void set(final String[] obj) {
        this.items = new GapList<>();
        this.delay = 0;
        try {
            if (obj.length > 0 && obj[0].length() > 0) {
                for (final String furni : obj[0].split(",")) {
                    items.add(Integer.valueOf(furni));
                }
            }
            if (obj.length > 1 && obj[1].length() > 0) {
                delay = Integer.valueOf(obj[1]);
            }
        } catch (final NumberFormatException e) {
            logger.error("NumberFormatException", e);
        }
    }

    @Override
    public String[] getObject(final MessageReader reader) {
        reader.readInteger();
        reader.readInteger();
        reader.readUTF();
        String furniString = "";
        final int furniAmount = reader.readInteger();
        for (int i = 0; i < furniAmount; i++) {
            final int furniId = reader.readInteger();
            if (!item.getRoom().getRoomItems().containsKey(furniId) || WiredManager.isWiredItem(item.getRoom().getRoomItems().get(furniId).getBase())) {
                continue;
            }
            furniString += "," + furniId;
        }
        final int delay = reader.readInteger();
        return new String[]{furniString.length() > 0 ? furniString.substring(1) : furniString, delay + ""};
    }

    @Override
    public void onHandle(final RoomPlayer player) {
        if (this.delay > 0) {
            room.getRoomTask().offerWiredItemDelay(new WiredDelayEvent(this, player));
            return;
        }
        this.handle();
    }

    private void handle() {
        for (final Integer i : items) {
            final RoomItem item = room.getRoomItems().get(i);
            if (item != null) {
                item.getInteractor().onTrigger(null, item, item.getInteractorId() == FurnitureInteractor.BATTLE_BANZAI_TIMER ? 1 : 0, true);
            }
        }
    }

    @Override
    public void onHandle(final RoomPlayer player, final WiredDelayEvent event) {
        if (event.getIncrementedCycle() >= this.delay) {
            this.handle();
            event.finish();
        }
    }

    private final RoomInstance room;
    private final RoomItem item;
    private GapList<Integer> items;
    private int delay;

}
