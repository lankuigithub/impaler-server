package site.lankui.impaler.handler;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import site.lankui.impaler.client.Client;
import site.lankui.impaler.client.ClientManager;
import site.lankui.impaler.constant.AttributeMapConstant;
import site.lankui.impaler.util.SpringBeanUtils;

import java.net.InetSocketAddress;
import java.util.UUID;

public class ConnectHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private Client client;
	private ClientManager clientManager;

	public ConnectHandler() {
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
		ctx.fireChannelRead(byteBuf);
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
