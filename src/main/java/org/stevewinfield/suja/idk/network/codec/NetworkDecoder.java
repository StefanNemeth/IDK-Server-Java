/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.network.codec;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.MessageHandler;
import org.stevewinfield.suja.idk.communication.MessageReaderFactory;
import org.stevewinfield.suja.idk.encryption.Base64Encryption;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class NetworkDecoder extends FrameDecoder {
    private static Logger logger = Logger.getLogger(NetworkDecoder.class);

    @Override
    protected Object decode(final ChannelHandlerContext ctx, final Channel channel, final ChannelBuffer buffer) {
        
        if (buffer.readableBytes() < 5) {
            return null;
        }
        
        int handledObjects = 0;
        
        while (buffer.readableBytes() > 0) {
            try {
                final byte testXmlLength = buffer.readByte();
                if (testXmlLength == 60) {
                    ctx.getChannel().write(IDK.XML_POLICY);
                    return null;
                }
                
                final int    messageLength = Base64Encryption.decode(new String(new byte[] { testXmlLength, buffer.readByte(), buffer.readByte() }));
                final short  messageId     = (short) Base64Encryption.decode(new String(new byte[] { buffer.readByte(), buffer.readByte() }));
                final byte[] content       = new byte[messageLength - 2];
    
                buffer.readBytes(content);
                
                MessageHandler.handleMessage((Session) ctx.getChannel().getAttachment(),
                MessageReaderFactory.getMessageReader(messageId, content));
                
                ++handledObjects;
            } catch (final Exception e) {
                // ignore logger
            }
        }
        
        return handledObjects;
    }
}
