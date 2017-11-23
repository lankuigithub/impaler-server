package site.lankui.impaler.handler;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import site.lankui.impaler.command.Command;

public class CommandEncoder extends MessageToByteEncoder<Command> {
	@Override
	protected void encode(ChannelHandlerContext ctx, Command command, ByteBuf out) throws Exception {
		out.writeInt(command.getType());
		out.writeInt(command.getDataLength());
		out.writeBytes(command.getData());
		out.writeBytes(Unpooled.copiedBuffer("\r\n", CharsetUtil.UTF_8));
	}
}
