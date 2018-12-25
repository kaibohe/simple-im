package im.kaibo.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: kaibo.he
 * @create: 2018-12-19 17:30
 **/
public class IMHandler extends SimpleChannelInboundHandler<MqttMessage>{
    public static final IMHandler INSTANCE = new IMHandler();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MqttMessage msg) throws Exception {

    }

    public static void main(String[] args) {
        List<String> strList = new ArrayList<>();

        List<Integer> intList = strList.stream().map(s -> NumberUtils.toInt(s)>1 ? NumberUtils.toInt(s):null).collect(Collectors.toList());
        System.out.println(intList);
    }
}
