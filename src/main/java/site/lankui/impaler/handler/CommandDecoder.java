package site.lankui.impaler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.util.ObjectUtils;
import site.lankui.impaler.client.bean.Session;
import site.lankui.impaler.command.Command;
import site.lankui.impaler.constant.AttributeMapConstant;
import site.lankui.impaler.service.CommandService;
import site.lankui.impaler.util.SpringBeanUtils;

import java.util.Date;

public class CommandDecoder extends SimpleChannelInboundHandler<ByteBuf> {

	private Session session;
	private CommandService commandService;

	public CommandDecoder() {
		commandService = SpringBeanUtils.getBean(CommandService.class);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
		session = getSession(ctx.channel());
		Command command = new Command();
		command.setType(byteBuf.readInt());
		command.setTarget(byteBuf.readInt());
		command.setDataLength(byteBuf.readInt());
		byte[] bytes = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(bytes);
		command.setData(bytes);
		command.setSessionType(session.getType());
		command.setDate(new Date());
		commandService.execute(command, session);
	}

	private Session getSession(Channel channel) {
		if (ObjectUtils.isEmpty(session)) {
			session = channel.attr(AttributeMapConstant.KEY_CLIENT).get();
		}
		return session;
	}

}
