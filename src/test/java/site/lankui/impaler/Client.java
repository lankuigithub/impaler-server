package site.lankui.impaler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Client {
    private String ip;
    private int port;
    private Bootstrap bootstrap = new Bootstrap();
    private EventLoopGroup group = new NioEventLoopGroup();
    private ChannelFuture future;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void run() throws InterruptedException {
        try {
            SocketAddress address = new InetSocketAddress(ip, port);
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(address)
                    .handler(new ClientInitializer());
            future = bootstrap.connect().sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

}
