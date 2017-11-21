package site.lankui.impaler.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.util.ObjectUtils;
import site.lankui.impaler.client.Client;
import site.lankui.impaler.constant.AttributeMapConstant;
import site.lankui.impaler.service.MessageService;
import site.lankui.impaler.util.SpringBeanUtils;

public class CommandInboundHandler extends SimpleChannelInboundHandler<String> {

	private Client client;
	private MessageService messageService;
	private static final String RESPONSE_MESSAGE = "ok";

	public CommandInboundHandler() {
		messageService = SpringBeanUtils.getBean(MessageService.class);
	}

	protected void channelRead0(ChannelHandlerContext ctx, String string) throws Exception {
		sendMessageNotExistsSelf(ctx.channel(), string);
		ctx.writeAndFlush(RESPONSE_MESSAGE);
	}

	private void sendMessageNotExistsSelf(Channel channel, String message) {
		messageService.sendNotExistsSelf(getClient(channel), message);
	}

	private Client getClient(Channel channel) {
		if (ObjectUtils.isEmpty(client)) {
			client = channel.attr(AttributeMapConstant.KEY_CLIENT).get();
		}
		return client;
	}

}
