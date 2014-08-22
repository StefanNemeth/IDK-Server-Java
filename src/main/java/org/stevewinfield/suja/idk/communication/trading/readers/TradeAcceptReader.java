/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.trading.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.trading.writers.TradeAcceptStateWriter;
import org.stevewinfield.suja.idk.communication.trading.writers.TradeFinalizingWriter;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.trading.Trade;
import org.stevewinfield.suja.idk.game.trading.TradeStage;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class TradeAcceptReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom())
            return;

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        Trade trade = null;

        if (room == null
        || (trade = room.getTradeManager().getTrade(session.getPlayerInstance().getInformation().getId())) == null
        || !trade.acceptTrade(session.getPlayerInstance().getInformation().getId()))
            return;

        final MessageWriter acceptState = new TradeAcceptStateWriter(session.getPlayerInstance().getInformation().getId(),
        true);

        final MessageWriter finalizingWriter = trade.getStage() == TradeStage.FINALIZING ? new TradeFinalizingWriter() : null;

        session.writeMessage(acceptState);

        if (finalizingWriter != null)
            session.writeMessage(finalizingWriter);

        Session targetSession = null;
        final int targetId = session.getPlayerInstance().getInformation().getId() == trade.getPlayerOne() ? trade
        .getPlayerTwo() : trade.getPlayerOne();

        for (final RoomPlayer player : room.getRoomPlayers().values())
            if (player.getSession() != null
            && player.getSession().getPlayerInstance().getInformation().getId() == targetId)
                targetSession = player.getSession();

        if (targetSession != null) {
            targetSession.writeMessage(acceptState);

            if (finalizingWriter != null)
                targetSession.writeMessage(finalizingWriter);
        }

    }

}
