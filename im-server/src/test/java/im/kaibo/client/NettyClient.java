package im.kaibo.client;

import im.kaibo.client.handler.MqttConnectHandler;
import im.kaibo.server.NettyServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: kaibo.he
 * @create: 2018-12-25 14:48
 **/
public class NettyClient {
    private static final InternalLogger logger = Log4JLoggerFactory.getInstance(NettyServer.class);

    private static final int MAX_RETRY = 5;
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 8000;

    public static void main(String[] args) {
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(MqttEncoder.INSTANCE);
                        ch.pipeline().addLast(new MqttDecoder());
                        ch.pipeline().addLast(new MqttConnectHandler());
                    }
                });
        connect(bootstrap, HOST, PORT, MAX_RETRY);
    }

    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                logger.info(new Date() + ":connect success! start console thread!");
                Channel channel = ((ChannelFuture)future).channel();

            } else if (retry == 0) {
                logger.warn("retry number out!");
                Channel channel = ((ChannelFuture)future).channel();
                bootstrap.group().shutdownGracefully();
            } else {
                //第几次重连
                int order = (MAX_RETRY - retry) + 1;
                //本次重连间隔
                int delay = 1 << order;
                logger.info(new Date() + "connect failed! [" + order + "]time for reconnect!");
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
            }
        });
    }
}
