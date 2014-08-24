/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.wired.triggers;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.wired.WiredTrigger;

public class WiredTriggerPeriodically extends WiredTrigger {
    private static final Logger logger = Logger.getLogger(WiredTriggerPeriodically.class);

    public WiredTriggerPeriodically(final RoomInstance room, final RoomItem item, final String[] data) {
        this.room = room;
        this.item = item;
        this.set(data);
    }

    @Override
    public int getWiredType() {
        return 1;
    }

    @Override
    public RoomItem getItem() {
        return item;
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public void set(final String[] obj) {
        this.delay = 10;
        try {
            if (obj.length > 0 && obj[0].length() > 0) {
                delay = Integer.valueOf(obj[0]);
            }
        } catch (final NumberFormatException e) {
            logger.error("NumberFormatException", e);
        }
        this.delayState = 0;
    }

    @Override
    public String[] getObject(final MessageReader reader) {
        reader.readInteger();
        int delay = reader.readInteger();
        if (delay > 120) {
            delay = 120;
        }
        return new String[]{delay + ""};
    }

    @Override
    public boolean onTrigger(final RoomPlayer player, final Object data) {
        if (++delayState == delay) {
            delayState = 0;
            return true;
        }
        return false;
    }

    private final RoomInstance room;
    private final RoomItem item;
    private int delay;
    private int delayState;
}
