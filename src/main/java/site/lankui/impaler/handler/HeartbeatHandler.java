package site.lankui.impaler.handler;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import site.lankui.impaler.client.Client;
import site.lankui.impaler.client.ClientManager;
import site.lankui.impaler.constant.AttributeMapConstant;
import site.lankui.impaler.util.SpringBeanUtils;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class HeartbeatHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private static final String PING = "ping";
	private static final String PONG = "pong";


	private Client client;
	private ClientManager clientManager;

	public HeartbeatHandler() {
		clientManager = SpringBeanUtils.getBean(ClientManager.class);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
		client = Client.builder()
			.clientId(UUID.randomUUID().toString())
			.ipAddress(socketAddress.getHostName())
			.port(socketAddress.getPort())
			.channel(ctx.channel())
			.build();
		ctx.channel().attr(AttributeMapConstant.KEY_CLIENT).set(client);
		clientManager.addClient(client);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
		byte[] bytes = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(bytes);
		String message = new String(bytes, CharsetUtil.UTF_8);
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
			+ " IpAddress: " + ctx.channel().remoteAddress().toString()
			+ " Message: " + message);
		if (PING.equalsIgnoreCase(message)) {
			ctx.channel().writeAndFlush(PONG);
		} else {
			ctx.fireChannelRead(message);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		clientManager.removeClient(client);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		clientManager.removeClient(client);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			switch (e.state()) {
				case READER_IDLE:
				case WRITER_IDLE:
				case ALL_IDLE:
					clientManager.removeClient(client);
					ChannelFuture future = ctx.channel().closeFuture();
					future.sync();
			}
		}
	}

}
