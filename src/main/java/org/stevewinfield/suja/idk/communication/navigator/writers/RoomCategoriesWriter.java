/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.navigator.writers;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.rooms.RoomCategory;

public class RoomCategoriesWriter extends MessageWriter {

    public RoomCategoriesWriter(final GapList<RoomCategory> categories) {
        super(OperationCodes.getOutgoingOpCode("RoomCategories"));
        super.push(categories.size());

        for (final RoomCategory category : categories) {
            super.push(category.getId());
            super.push(category.getTitle());
            super.push(!category.isStaffCategory());
        }
    }

}
