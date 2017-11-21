package site.lankui.impaler.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;


public class ReceiveHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("InActive");
        ctx.writeAndFlush(Unpooled.copiedBuffer("I'm Client.\r\n", CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf)msg;
        byte[] result = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(result);
        System.out.println("received the server message : " + new String(result));
        ctx.writeAndFlush(result);
    }
}
