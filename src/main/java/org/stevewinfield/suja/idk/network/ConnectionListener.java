/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.network;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.stevewinfield.suja.idk.network.codec.NetworkDecoder;
import org.stevewinfield.suja.idk.threadpools.WorkerTasks;

public class ConnectionListener {
    private static Logger logger = Logger.getLogger(ConnectionListener.class);

    private final NioServerSocketChannelFactory factory;
    private final ServerBootstrap bootstrap;

    private final String ip;
    private final int port;

    public ConnectionListener(final String ip, final int port) {
        this.ip = ip;
        this.port = port;
        this.factory = new NioServerSocketChannelFactory(WorkerTasks.getNettyBossExecutor(),
        WorkerTasks.getNettyWorkerExecutor());
        this.bootstrap = new ServerBootstrap(this.factory);
    }

    public boolean tryListen() {
        try {
            this.bootstrap.getPipeline().addLast("decoder", new NetworkDecoder());
            this.bootstrap.getPipeline().addLast("handler", new ConnectionHandler());
            this.bootstrap.bind(new InetSocketAddress(this.ip, this.port));
        } catch (final ChannelException ex) {
            logger.error("Couldn't open connection to " + this.ip + ":" + this.port + ".", ex);
            return false;
        }
        logger.info("Connection to " + this.ip + ":" + this.port + " created.");
        return true;
    }
}
