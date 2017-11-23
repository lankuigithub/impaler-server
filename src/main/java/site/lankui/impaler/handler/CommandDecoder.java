package site.lankui.impaler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.util.ObjectUtils;
import site.lankui.impaler.client.Client;
import site.lankui.impaler.command.Command;
import site.lankui.impaler.constant.AttributeMapConstant;
import site.lankui.impaler.service.CommandService;
import site.lankui.impaler.util.SpringBeanUtils;

public class CommandDecoder extends SimpleChannelInboundHandler<ByteBuf> {

	private CommandService commandService;
	private Client client;

	public CommandDecoder() {
		commandService = SpringBeanUtils.getBean(CommandService.class);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
		Command command = new Command();
		command.setType(byteBuf.readInt());
		command.setDataLength(byteBuf.readInt());
		byte[] bytes = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(bytes);
		command.setData(bytes);
		commandService.execute(command, getClient(ctx.channel()));
	}


	private Client getClient(Channel channel) {
		if (ObjectUtils.isEmpty(client)) {
			client = channel.attr(AttributeMapConstant.KEY_CLIENT).get();
		}
		return client;
	}

}
