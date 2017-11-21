package site.lankui.impaler.launcher;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Service
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class ServerLauncher {

	@Value("${impaler.server.port}")
	private int port;
	private final static int BOSS_GROUP_THREAD_NUM = 1;
	private EventLoopGroup bossGroup = new NioEventLoopGroup(BOSS_GROUP_THREAD_NUM);
	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	@PostConstruct
	public void start() throws InterruptedException {
		new Thread(() -> {
			try {
				ServerBootstrap bootstrap = new ServerBootstrap();
				bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ServerInitializer())
					.childOption(ChannelOption.SO_KEEPALIVE, true);
				InetSocketAddress address = new InetSocketAddress(port);
				ChannelFuture future = bootstrap.bind(address).sync();
				future.channel().closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}

}
