/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.wired.writers;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;

public class WiredTriggerStateChangedWriter extends RoomWiredTriggerWriter {
    private static final Logger logger = Logger.getLogger(WiredTriggerUserWalksOffFurniWriter.class);

    public WiredTriggerStateChangedWriter(final RoomItem item, final String furnis) {
        super.push(false);
        super.push(5);

        final GapList<Integer> items = new GapList<>();

        if (furnis.length() > 0) {
            try {
                for (final String furni : furnis.split(",")) {
                    items.add(Integer.valueOf(furni));
                }
            } catch (final NumberFormatException e) {
                logger.error("NumberFormatException", e);
            }
        }

        super.push(items.size());

        for (final int furni : items) {
            super.push(furni);
        }

        super.push(item.getBase().getSpriteId());
        super.push(item.getItemId());
        super.push("");
        super.push(0);
        super.push(0);
        super.push(4);
        super.push(0);
        super.push(0);
    }
}
