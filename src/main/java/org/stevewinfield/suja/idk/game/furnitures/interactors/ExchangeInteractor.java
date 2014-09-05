/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.player.writers.ActivityPointsWriter;
import org.stevewinfield.suja.idk.communication.player.writers.CreditsBalanceWriter;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureExchange;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class ExchangeInteractor extends DefaultInteractor {

    @Override
    public void onTrigger(final RoomPlayer player, final RoomItem item, final int request, final boolean hasRights) {
        super.onTrigger(player, item, request, hasRights);

        if (player == null ||
                (player.getRoom().hasRights(player.getSession(), true) && item.getBase().hasRightCheck()) ||
                !Bootloader.getGame().getFurnitureManager().getFurnitureExchanges().containsKey(item.getBase().getId())) {
            return;
        }

        final FurnitureExchange exchange = Bootloader.getGame().getFurnitureManager().getFurnitureExchanges().get(item.getBase().getId());

        if (exchange.getChangeCoins() > 0) {
            player.getSession().getPlayerInstance().getInformation().addCredits(exchange.getChangeCoins());
            player.getSession().writeMessage(new CreditsBalanceWriter(player.getSession().getPlayerInstance().getInformation().getCreditsBalance()));
        }

        if (exchange.getChangePixels() > 0 || exchange.getChangeExtra() > 0) {
            if (exchange.getChangePixels() > 0) {
                player.getSession().getPlayerInstance().getInformation().setPixels(exchange.getChangePixels());
            } else {
                player.getSession().getPlayerInstance().getInformation().setShells(exchange.getChangeExtra());
            }
            player.getSession().writeMessage(
                    new ActivityPointsWriter(
                            player.getSession().getPlayerInstance().getInformation().getPixelsBalance(),
                            player.getSession().getPlayerInstance().getInformation().getShellsBalance()
                    )
            );
        }

        player.getSession().getPlayerInstance().getInformation().updateCurrencies();

        /**
         * Remove the shit
         */
        item.getRoom().removeItem(item, player.getSession());
        Bootloader.getStorage().executeQuery("DELETE FROM items WHERE id=" + item.getItemId());
    }

}
