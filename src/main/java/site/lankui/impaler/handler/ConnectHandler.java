package site.lankui.impaler.handler;


import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import site.lankui.impaler.bean.Session;
import site.lankui.impaler.bean.SessionManager;
import site.lankui.impaler.constant.AttributeMapConstant;
import site.lankui.impaler.util.SpringBeanUtils;

import java.net.InetSocketAddress;
import java.util.UUID;

public class ConnectHandler extends ChannelInboundHandlerAdapter {

	private Session session;
	private SessionManager sessionManager;

	public ConnectHandler() {
		sessionManager = SpringBeanUtils.getBean(SessionManager.class);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		session = Session.builder()
			.sessionId(UUID.randomUUID().toString())
			.ipAddress(socketAddress.getHostName())
			.port(socketAddress.getPort())
			.channel(ctx.channel())
			.build();
		ctx.channel().attr(AttributeMapConstant.KEY_CLIENT).set(session);
		sessionManager.addSession(session);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ctx.fireChannelRead(msg);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		sessionManager.removeSession(session);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		sessionManager.removeSession(session);
		cause.printStackTrace();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			switch (e.state()) {
				case READER_IDLE:
				case WRITER_IDLE:
				case ALL_IDLE:
					sessionManager.removeSession(session);
					ChannelFuture future = ctx.channel().closeFuture();
					future.sync();
			}
		}
	}

}
