/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.recycler.writers;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.furnitures.Furniture;

public class CatalogRecyclerRewardsWriter extends MessageWriter {

    public CatalogRecyclerRewardsWriter(final LinkedHashMap<Integer, GapList<Integer>> rewards) {
        super(OperationCodes.getOutgoingOpCode("CatalogRecyclerRewards"));
        super.push(rewards.size());
        for (final Entry<Integer, GapList<Integer>> rewardEntry : rewards.entrySet()) {
            int chanceToDisplay = 0;
            switch (rewardEntry.getKey()) {
            case 5:
                chanceToDisplay = 40;
                break;
            case 4:
                chanceToDisplay = 25;
                break;
            case 3:
                chanceToDisplay = 15;
                break;
            case 2:
                chanceToDisplay = 5;
                break;
            }
            super.push(rewardEntry.getKey());
            super.push(chanceToDisplay);
            super.push(rewardEntry.getValue().size());
            for (final int itemId : rewardEntry.getValue()) {
                final Furniture furni = Bootloader.getGame().getFurnitureManager().getFurniture(itemId);
                if (furni == null) {
                    continue;
                }
                super.push(furni.getType());
                super.push(furni.getSpriteId());
            }
        }
    }

}
