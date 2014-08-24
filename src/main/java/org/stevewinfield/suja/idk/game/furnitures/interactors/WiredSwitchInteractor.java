/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors;

import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.coordination.Vector2;

public class WiredSwitchInteractor extends DefaultSwitchInteractor {

    @Override
    public void onTrigger(final RoomPlayer player, final RoomItem item, final int request, final boolean hasRights) {
        super.onTrigger(player, item, request, hasRights);

        if (!hasRights && item.getBase().hasRightCheck()) {
            return;
        }

        if (!item.isTouching(player.getPosition(), player.getRotation(), true)) {
            final Vector2 goal = item.getFrontPosition(player.getPosition().getVector2());
            if (goal != null) {
                player.moveTo(goal, item.getFrontRotation(goal), item);
            }
        }
    }

}
