package site.lankui.impaler.client.bean;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;
import site.lankui.impaler.command.Command;
import site.lankui.impaler.constant.SessionType;

@Builder
public class Session {
	@Setter
	@Getter
	private String sessionId;
	@Setter
	@Getter
	private SessionType type;
	@Setter
	@Getter
	private String ipAddress;
	@Setter
	@Getter
	private int port;
	@Setter
	private Channel channel;
	@Setter
	@Getter
	private Client client;

	public void sendCommand(Command command) {
		channel.writeAndFlush(command).addListener((ChannelFutureListener) future -> {
			if (future.isSuccess()) {
				return;
			}
			if (ObjectUtils.isEmpty(client)) {
				return;
			}
			client.addCommand(command);
		});
	}
}
