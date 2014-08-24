/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors;

import org.stevewinfield.suja.idk.game.furnitures.FurnitureType;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.coordination.Vector2;

import java.security.SecureRandom;

public class RandomInteractor extends DefaultInteractor {

    @Override
    public void onTrigger(final RoomPlayer player, final RoomItem item, final int request, final boolean hasRights) {
        super.onTrigger(player, item, request, hasRights);

        if (item.getBase().getType().equals(FurnitureType.FLOOR) && player != null && player.getSession() != null && !item.isTouching(player.getPosition(), player.getRotation(), true)) {
            final Vector2 goal = item.getFrontPosition(player.getPosition().getVector2());
            if (goal != null) {
                player.moveTo(goal, item.getFrontRotation(goal), item);
            }
            return;
        }

        if (item.getFlagsState() == -1) {
            return;
        }

        if (request == 1) {
            item.setFlags(0);
            item.update(false, true);
            return;
        }

        int timeBreak = 3;

        /**
         * Well, hardcoding?.. MAN! YOU CAN DO IT BETTER
         */
        switch (item.getBase().getName().toLowerCase()) {
            case "habbowheel":
                timeBreak = 14;
                break;
            case "bottle":
                timeBreak = 4;
                break;
        }

        item.setFlags(-1);
        item.update(false, true);
        item.requestCycles(timeBreak);
    }

    @Override
    public void onCycle(final RoomItem item) {
        super.onCycle(item);
        if (item.getFlagsState() != -1) {
            return;
        }
        item.setFlags((new SecureRandom()).nextInt(item.getBase().getCycleCount()) + 1);
        item.update(false, true);
    }

}
