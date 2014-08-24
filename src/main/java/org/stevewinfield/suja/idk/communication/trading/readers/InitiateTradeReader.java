/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.trading.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.trading.writers.RoomTradeCannotInitiateWriter;
import org.stevewinfield.suja.idk.communication.trading.writers.TradeInitiatedWriter;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class InitiateTradeReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final int targetId = reader.readInteger();
        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || !room.getRoomPlayers().containsKey(targetId)) {
            return;
        }

        final RoomPlayer target = room.getRoomPlayers().get(targetId);

        if (target == null || target.getSession() == null || target.getVirtualId() == session.getRoomPlayer().getVirtualId()) {
            return;
        }

        if (!room.getTradeManager().initiateTrade(session.getRoomPlayer().getPlayerInformation().getId(), target.getPlayerInformation().getId())) {
            session.writeMessage(new RoomTradeCannotInitiateWriter());
            return;
        }

        final MessageWriter tradeInitiated = new TradeInitiatedWriter(
                session.getRoomPlayer().getPlayerInformation().getId(),
                session.getRoomPlayer().getPlayerInformation().canTrade(),
                target.getPlayerInformation().getId(),
                target.getPlayerInformation().canTrade()
        ); // TODO: can trade?

        session.writeMessage(tradeInitiated);
        target.getSession().writeMessage(tradeInitiated);

        session.getRoomPlayer().addStatus("trd", "");
        session.getRoomPlayer().update();

        target.addStatus("trd", "");
        target.update();
    }

}
