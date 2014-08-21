/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.moderation.writers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.players.PlayerInformation;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class ModerationPlayerInfoWriter extends MessageWriter {

    public ModerationPlayerInfoWriter(final PlayerInformation playerInfo, final Session session) {
        super(OperationCodes.getOutgoingOpCode("ModerationPlayerInfo"));
        super.push(playerInfo.getId());
        super.push(playerInfo.getPlayerName());
        super.push(((int) (Bootloader.getTimestamp() - playerInfo.getRegisteredTimestamp())) / 60); // time
                                                                                                    // registered
        super.push(((int) (Bootloader.getTimestamp() - playerInfo.getLastLoginTimestamp())) / 60); // time
                                                                                                   // last
                                                                                                   // login
        super.push(session != null);
        super.push(playerInfo.getTotalCFHS()); // created tickets
        super.push(playerInfo.getAbusiveCFHS()); // tickets abusive
        super.push(playerInfo.getCautions()); // cautions
        super.push(playerInfo.getBans()); // bans
    }

}
