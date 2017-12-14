package site.lankui.impaler.handler;


import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import site.lankui.impaler.client.ConnectManager;
import site.lankui.impaler.client.bean.Client;
import site.lankui.impaler.client.bean.Session;
import site.lankui.impaler.command.CommandDefine;
import site.lankui.impaler.constant.AttributeMapConstant;
import site.lankui.impaler.constant.SessionType;
import site.lankui.impaler.util.SpringBeanUtils;

import java.net.InetSocketAddress;
import java.util.UUID;

@Slf4j
public class ConnectHandler extends ChannelInboundHandlerAdapter {

	private Session session;
	private ConnectManager connectManager;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		session = Session.builder()
			.sessionId(UUID.randomUUID().toString())
			.type(SessionType.NORMAL)
			.ipAddress(socketAddress.getHostName())
			.port(socketAddress.getPort())
			.channel(ctx.channel())
			.build();
		ctx.channel().attr(AttributeMapConstant.KEY_CLIENT).set(session);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ctx.fireChannelRead(msg);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		closeChannel(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("Connect error: ", cause);
		closeChannel(ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			switch (e.state()) {
				case READER_IDLE:
					log.info("Reader idle...");
					break;
				case WRITER_IDLE:
					log.info("Writer idle...");
					break;
				case ALL_IDLE:
					log.info("All idle...");
					break;
			}
			ctx.writeAndFlush(CommandDefine.COMMAND_HEART_BEAT).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
		}
	}

	private ConnectManager getConnectManager() {
		if (ObjectUtils.isEmpty(connectManager)) {
			connectManager = SpringBeanUtils.getBean(ConnectManager.class);
		}
		return connectManager;
	}

	private void closeChannel(ChannelHandlerContext ctx) {
		Client client = session.getClient();
		if(!ObjectUtils.isEmpty(client)) {
			getConnectManager().unRegisterClient(client, session);
		}
		if(!ObjectUtils.isEmpty(ctx)) {
			ctx.channel().closeFuture().addListener(ChannelFutureListener.CLOSE);
		}
	}

}
