package site.lankui.impaler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import site.lankui.impaler.handler.SendHandler;
import site.lankui.impaler.handler.ReceiveHandler;


public class ClientInitializer extends ChannelInitializer<Channel> {
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new SendHandler());
        pipeline.addLast(new ReceiveHandler());
    }
}
