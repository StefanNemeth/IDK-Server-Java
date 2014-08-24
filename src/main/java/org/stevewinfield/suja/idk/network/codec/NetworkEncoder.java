/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.network.codec;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.QueuedMessageWriter;

import java.nio.charset.Charset;

public class NetworkEncoder extends SimpleChannelHandler {
    private static Logger logger = Logger.getLogger(NetworkDecoder.class);

    @Override
    public void writeRequested(final ChannelHandlerContext ctx, final MessageEvent e) {
        try {
            if (e.getMessage() instanceof String) {
                Channels.write(ctx, e.getFuture(), ChannelBuffers.copiedBuffer((String) e.getMessage(), Charset.forName("UTF-8")));
            } else if (e.getMessage() instanceof MessageWriter) {
                final MessageWriter msg = (MessageWriter) e.getMessage();
                Channels.write(ctx, e.getFuture(), msg.getBytes());
            } else if (e.getMessage() instanceof QueuedMessageWriter) {
                final QueuedMessageWriter msg = (QueuedMessageWriter) e.getMessage();
                Channels.write(ctx, e.getFuture(), msg.getBytes());

                return;
            }
        } catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
