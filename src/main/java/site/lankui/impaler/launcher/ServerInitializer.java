package site.lankui.impaler.launcher;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import site.lankui.impaler.handler.CommandInboundHandler;
import site.lankui.impaler.handler.CommandOutboundHandler;
import site.lankui.impaler.handler.HeartbeatHandler;

import java.util.concurrent.TimeUnit;


public class ServerInitializer extends ChannelInitializer<Channel> {

	private static final int MAX_FRAME_LENGTH = 2048;
	private static final int READER_IDLE_TIME = 30;
	private static final int WRITER_IDLE_TIME = 0;
	private static final int ALL_IDLE_TIME = 0;
	private static final ByteBuf delimiter = Unpooled.copiedBuffer("\r\n".getBytes());

	protected void initChannel(Channel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		// out bound
		pipeline.addLast(new CommandOutboundHandler());
		//  in bound
		pipeline.addLast(new DelimiterBasedFrameDecoder(MAX_FRAME_LENGTH, delimiter));
		pipeline.addLast(new IdleStateHandler(READER_IDLE_TIME, WRITER_IDLE_TIME, ALL_IDLE_TIME, TimeUnit.SECONDS));
		pipeline.addLast(new HeartbeatHandler());
		pipeline.addLast(new CommandInboundHandler());
	}
}