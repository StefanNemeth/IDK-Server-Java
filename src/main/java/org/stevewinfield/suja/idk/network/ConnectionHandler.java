/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.network;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.*;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class ConnectionHandler extends SimpleChannelHandler {
    private static final Logger logger = Logger.getLogger(ConnectionHandler.class);

    @Override
    public void channelOpen(final ChannelHandlerContext ctnx, final ChannelStateEvent e) {
        if (Bootloader.getSessionManager().makeSession(ctnx.getChannel())) {
            if (IDK.DEBUG) {
                logger.debug("Channel opened.");
            }
        } else {
            ctnx.getChannel().disconnect();
        }
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) {
        if (Bootloader.getSessionManager().removeSession(ctx.getChannel())) {
            ((Session) ctx.getChannel().getAttachment()).dispose();
            if (IDK.DEBUG) {
                logger.debug("Channel closed.");
            }
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) {
        if (Bootloader.getSessionManager().hasSession(ctx.getChannel())) {
            ((Session) ctx.getChannel().getAttachment()).dispose();
        }
        logger.error("Execption caught.", e.getCause());
        ctx.getChannel().close();
    }

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) {
        if (Bootloader.getSessionManager().hasSession(ctx.getChannel())) {
            if (!(e.getMessage() instanceof Integer)) {
                ctx.getChannel().close();
            }
        }
    }

}
