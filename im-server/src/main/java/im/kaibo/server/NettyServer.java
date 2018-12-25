package im.kaibo.server;

import im.kaibo.server.handler.ConnAckHandler;
import im.kaibo.server.handler.IMHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;

import java.util.Date;

/**
 * @description:
 * @author: kaibo.he
 * @create: 2018-12-19 14:23
 **/
public class NettyServer {
    private static final InternalLogger logger = Log4JLoggerFactory.getInstance(NettyServer.class);
    private static  final  int PORT = 8000;

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup work = new NioEventLoopGroup(8);

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(MqttEncoder.INSTANCE);
                        ch.pipeline().addLast(new MqttDecoder());
                        ch.pipeline().addLast(new ConnAckHandler());
                    }
                })
                ;
        bind(serverBootstrap, PORT);

    }

    private static void bind(final ServerBootstrap serverBootstrap, int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                logger.info(new Date() + ":port[" + port + "]bind success!");
            } else {
                logger.error(new Date() + ":port[" + port + "]bingd fialed!");
                bind(serverBootstrap, port + 1);
            }
        });
    }
}
