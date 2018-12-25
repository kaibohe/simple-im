package im.kaibo.server.handler;

import im.kaibo.server.NettyServer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import org.apache.commons.lang3.StringUtils;

/**
 * @description:
 * @author: kaibo.he
 * @create: 2018-12-19 17:40
 **/
public class ConnAckHandler extends SimpleChannelInboundHandler<MqttConnectMessage>{
    private static final InternalLogger logger = Log4JLoggerFactory.getInstance(ConnAckHandler.class);

    private String clientId;
    private String userName;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttConnectMessage msg) throws Exception {
        logger.info("handle mqtt connect!");
        onConnect(ctx, msg);

    }

    private void onConnect(ChannelHandlerContext ctx, MqttConnectMessage msg) {
        this.clientId = msg.payload().clientIdentifier();

        boolean userNameFlag = msg.variableHeader().hasUserName();
        boolean passwordFlag = msg.variableHeader().hasPassword();
        this.userName = msg.payload().userName();

        String password = "" ;
        if( msg.payload().passwordInBytes() != null  && msg.payload().passwordInBytes().length > 0) {
            password =   new String(msg.payload().passwordInBytes());
        }

        boolean mistake = false;

        //如果有用户名标示，那么就必须有密码标示。
        //当有用户名标的时候，用户不能为空。
        //当有密码标示的时候，密码不能为空。
        if (userNameFlag) {
            if (StringUtils.isBlank(this.userName)) {
                mistake = true;
            }
        } else {
            if (StringUtils.isNotBlank(this.userName) || passwordFlag) mistake = true;
        }

        if (passwordFlag) {
            if (StringUtils.isBlank(password)) mistake = true;
        } else {
            if (StringUtils.isNotBlank(password)) mistake = true;
        }
        if (mistake) {
            ctx.writeAndFlush(MqttMessageFactory.newMessage(
                                             new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, false, 0),
                                     new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, false),
                                     null));
             ctx.close();
             return;
        }

        ctx.writeAndFlush( MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, false, 0),
                new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, true),
                null));
    }
}
