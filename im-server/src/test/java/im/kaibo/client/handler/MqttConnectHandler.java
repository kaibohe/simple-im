package im.kaibo.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;

import java.util.Objects;

/**
 * @description:
 * @author: kaibo.he
 * @create: 2018-12-25 15:58
 **/
public class MqttConnectHandler extends SimpleChannelInboundHandler<MqttConnAckMessage>{
    private static final InternalLogger logger = Log4JLoggerFactory.getInstance(MqttConnectHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttConnAckMessage msg) throws Exception {
        MqttConnAckVariableHeader variableHeader = msg.variableHeader();
        if (variableHeader.isSessionPresent() && Objects.equals(variableHeader.connectReturnCode(), MqttConnectReturnCode.CONNECTION_ACCEPTED)) {
            logger.info("mqtt connent is success!");
        } else {
            if (variableHeader.isSessionPresent()) {
                logger.info("mqtt connect failed, session is not persent!");
            } else {
                logger.info("mqtt connect is failed result code:" + variableHeader.connectReturnCode().name());
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MqttFixedHeader mqttFixedHeader = new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_LEAST_ONCE, false, 0);
        MqttConnectVariableHeader mqttConnectVariableHeader = new MqttConnectVariableHeader(
                MqttVersion.MQTT_3_1_1.protocolName(),
                (int) MqttVersion.MQTT_3_1_1.protocolLevel(),
                true,
                true,
                false,
                0,
                false,
                false,
                1);
        MqttConnectPayload mqttConnectPayload = new MqttConnectPayload("clientA", null, null, "clientA", "clientA".getBytes());
        MqttMessage mqttMessage = MqttMessageFactory.newMessage(
                mqttFixedHeader,
                mqttConnectVariableHeader,
                mqttConnectPayload);

        ctx.channel().writeAndFlush(mqttMessage);
        logger.info("send mqtt connect!");
    }
}
