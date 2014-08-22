/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.network;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.MessageHandler;
import org.stevewinfield.suja.idk.communication.MessageReaderFactory;
import org.stevewinfield.suja.idk.encryption.Base64Encryption;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class ConnectionHandler extends SimpleChannelHandler {
    private static Logger logger = Logger.getLogger(ConnectionHandler.class);

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
            final ChannelBuffer message = (ChannelBuffer) e.getMessage();
            if (message.readableBytes() < 5) {
                return;
            }
            while (message.readableBytes() > 0) {
                final byte testXmlLength = message.readByte();
                if (testXmlLength == 60) {
                    ctx.getChannel().write(IDK.XML_POLICY);
                    return;
                }
                final int messageLength = Base64Encryption.decode(new String(new byte[] { testXmlLength,
                        message.readByte(), message.readByte() }));
                final short messageId = (short) Base64Encryption.decode(new String(new byte[] { message.readByte(),
                        message.readByte() }));

                final byte[] content = new byte[messageLength - 2];

                message.readBytes(content);
                MessageHandler.handleMessage((Session) ctx.getChannel().getAttachment(),
                MessageReaderFactory.getMessageReader(messageId, content));
            }
        }
    }

}
