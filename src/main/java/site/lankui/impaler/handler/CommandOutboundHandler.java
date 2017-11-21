package site.lankui.impaler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class CommandOutboundHandler extends MessageToByteEncoder<String> {
	@Override
	protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
		msg += "\r\n";
		ctx.writeAndFlush(Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
	}
}
