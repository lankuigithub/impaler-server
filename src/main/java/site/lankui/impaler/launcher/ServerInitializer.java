package site.lankui.impaler.launcher;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import site.lankui.impaler.command.CommandDefine;
import site.lankui.impaler.handler.CommandDecoder;
import site.lankui.impaler.handler.CommandEncoder;
import site.lankui.impaler.handler.ConnectHandler;

import java.util.concurrent.TimeUnit;


public class ServerInitializer extends ChannelInitializer<Channel> {

	private static final int MAX_FRAME_LENGTH = Integer.MAX_VALUE;
	private static final int READER_IDLE_TIME = 30;
	private static final int WRITER_IDLE_TIME = 0;
	private static final int ALL_IDLE_TIME = 0;
	private static final ByteBuf delimiter = Unpooled.copiedBuffer(CommandDefine.SPLIT_WORD, CharsetUtil.UTF_8);

	protected void initChannel(Channel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		// out bound
		pipeline.addLast(new CommandEncoder());
		//  in bound
		pipeline.addLast(new IdleStateHandler(READER_IDLE_TIME, WRITER_IDLE_TIME, ALL_IDLE_TIME, TimeUnit.SECONDS));
		pipeline.addLast(new DelimiterBasedFrameDecoder(MAX_FRAME_LENGTH, delimiter));
		pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 4, 4));
		pipeline.addLast(new ConnectHandler());
		pipeline.addLast(new CommandDecoder());
	}
}