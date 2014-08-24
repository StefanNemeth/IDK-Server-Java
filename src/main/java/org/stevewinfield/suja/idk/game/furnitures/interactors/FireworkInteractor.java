/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors;

import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.catalog.writers.CatalogPurchaseResultWriter;
import org.stevewinfield.suja.idk.communication.catalog.writers.OpenFireworkChargeDialogWriter;
import org.stevewinfield.suja.idk.communication.player.writers.ActivityPointsWriter;
import org.stevewinfield.suja.idk.communication.player.writers.CreditsBalanceWriter;
import org.stevewinfield.suja.idk.communication.player.writers.CurrencyErrorWriter;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class FireworkInteractor extends DefaultInteractor {

    @Override
    public void onTrigger(final RoomPlayer player, final RoomItem item, final int request, final boolean hasRights) {
        super.onTrigger(player, item, request, hasRights);

        if (!hasRights && item.getBase().hasRightCheck()) {
            return;
        }

        int currentCharges = 0;
        try {
            currentCharges = Integer.valueOf(item.getTermFlags()[0]);
        } catch (final NumberFormatException ex) {
            ex.printStackTrace();
            currentCharges = 0;
        }
        if (request == 2) {
            boolean creditsError = false;
            boolean pixelsError = false;

            if (player.getSession().getPlayerInstance().getInformation().getCreditsBalance() < IDK.CATA_FIREWORKS_CHARGES_CREDITS) {
                creditsError = true;
            }

            if (player.getSession().getPlayerInstance().getInformation().getPixelsBalance() < IDK.CATA_FIREWORKS_CHARGES_PIXELS) {
                pixelsError = true;
            }

            if (creditsError || pixelsError) {
                player.getSession().writeMessage(new CurrencyErrorWriter(creditsError, pixelsError, false));
                return;
            }

            if (IDK.CATA_FIREWORKS_CHARGES_CREDITS > 0) {
                player.getSession().getPlayerInstance().getInformation().setCredits(-IDK.CATA_FIREWORKS_CHARGES_CREDITS);
                player.getSession().writeMessage(new CreditsBalanceWriter(player.getSession().getPlayerInstance().getInformation().getCreditsBalance()));
            }

            if (IDK.CATA_FIREWORKS_CHARGES_PIXELS > 0) {
                player.getSession().getPlayerInstance().getInformation().setPixels(-IDK.CATA_FIREWORKS_CHARGES_PIXELS);
                player.getSession().writeMessage(new ActivityPointsWriter(player.getSession().getPlayerInstance().getInformation().getPixelsBalance(), player.getSession().getPlayerInstance().getInformation().getShellsBalance()));
            }

            item.setTermFlags(new String[]{(currentCharges += 10) + ""});
            item.update(true, false);
            player.getSession().writeMessage(new OpenFireworkChargeDialogWriter(item.getItemId(), currentCharges, IDK.CATA_FIREWORKS_CHARGES_CREDITS, IDK.CATA_FIREWORKS_CHARGES_PIXELS, IDK.CATA_FIREWORKS_CHARGES_AMOUNT));
            player.getSession().writeMessage(new CatalogPurchaseResultWriter(item.getItemId(), "fireworks_charge_01", IDK.CATA_FIREWORKS_CHARGES_CREDITS, IDK.CATA_FIREWORKS_CHARGES_PIXELS, 0));
        } else if (request == 1 && player != null) {
            player.getSession().writeMessage(new OpenFireworkChargeDialogWriter(item.getItemId(), currentCharges, IDK.CATA_FIREWORKS_CHARGES_CREDITS, IDK.CATA_FIREWORKS_CHARGES_PIXELS, IDK.CATA_FIREWORKS_CHARGES_AMOUNT));
        } else {
            if (currentCharges <= 0) {
                if (player != null) {
                    player.getSession().writeMessage(new OpenFireworkChargeDialogWriter(item.getItemId(), currentCharges, IDK.CATA_FIREWORKS_CHARGES_CREDITS, IDK.CATA_FIREWORKS_CHARGES_PIXELS, IDK.CATA_FIREWORKS_CHARGES_AMOUNT));
                }
                return;
            }

            final int state = item.getFlagsState();

            if (state <= 0) {
                item.setFlags(1);
                item.update(false, true);
            } else if (state == 1) {
                if (player != null && !item.isTouching(player.getPosition(), player.getRotation())) {
                    player.moveTo(item.getFrontPosition(), item.getFrontRotation(), item);
                }

                item.setTermFlags(new String[]{(currentCharges -= 1) + ""});
                item.setFlags(2);
                item.update();

                int time = 10;

                // Hardcoding? Well, no need to change.
                if (item.getBase().getName() == "fireworks_01") {
                    time = 6;
                } else if (item.getBase().getName() == "fireworks_07") {
                    time = 20;
                }

                item.requestCycles(time);

                if (player != null) {
                    item.getRoom().getWiredHandler().onPlayerChangedState(player, item);
                }
            }
        }
    }

    @Override
    public void onCycle(final RoomItem item) {
        super.onCycle(item);
        int currentCharges = 0;

        try {
            currentCharges = Integer.valueOf(item.getTermFlags()[0]);
        } catch (final NumberFormatException ex) {
            ex.printStackTrace();
            currentCharges = 0;
        }

        if (item.getFlagsState() == 2) {
            item.setFlags(currentCharges > 0 ? 1 : 0);
            item.update(false, true);
        }
    }

}
